package engine.search;

import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.*;

import board.MoveEncoding;
import board.Position;
import customExceptions.InvalidPositionException;
import engine.evaluation.StaticEvaluation;
import moveGeneration.MoveGenerator;
import zobrist.ThreeFoldTable;
import moveGeneration.MoveUtility;

import java.util.List;


public class Search {

    // Values representing very high scores minimizing risk of over/underflow
    public static final int POS_INFINITY = 100_000_000;
    public static final int NEG_INFINITY = -100_000_000;

    /**
     * Class representing a move and evaluation
     */
    public static class MoveValue {
        public int value;
        public int bestMove;

        public MoveValue(int value, int bestMove) {
            this.value = value;
            this.bestMove = bestMove;
        }
    }

    public static int searchedDepth = 0;

    /**
     * Runs negamax on increasing depths until the time limit is exceeded
     *
     * @param position    position to run the search on
     * @param limitMillis time  limit in milliseconds for the search
     * @return moveValue associated with the deepest search of the position
     */
    public static MoveValue iterativeDeepening(Position position, long limitMillis, SearchState searchState) {
        // Create a list representing the best moves generated at each depth
        List<MoveValue> searchResults = new ArrayList<>();

        // Store the current time so we know when to stop searching
        long start = System.currentTimeMillis();

        // Create a new thread to run the search on
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Initialize search depth and search while time hasn't been exceeded
        int depth = 0;
        while (depth < 1000) {
            depth++;

            // Create and submit search task
            Callable<MoveValue> task = getMoveValueCallable(position, depth, position, searchState);
            Future<MoveValue> future = executor.submit(task);

            try {
                // Compute the max amount of time this search can take
                long maxTimeForSearch = start + limitMillis - System.currentTimeMillis();

                // Try to get the result of the task. If time limit exceeded, throws timeout exception
                MoveValue result = future.get(maxTimeForSearch, TimeUnit.MILLISECONDS);
                searchResults.add(result);
                System.out.println("info depth " + depth + " pv " +
                        MoveUtility.toLongAlgebraic(searchResults.getLast().bestMove)
                        + " score cp " + searchResults.getLast().value);

                // Stop searching if mate
                if (Math.abs(searchResults.getLast().value) >= POS_INFINITY)
                    return searchResults.getLast();

                searchedDepth++;

            } catch (TimeoutException te) {
                System.out.println("Function timed out!");

                future.cancel(true); // Tells the thread that it was interrupted
                executor.shutdown();

                try {
                    if (!executor.awaitTermination(1, TimeUnit.SECONDS)) { // Wait for it to finish
                        executor.shutdownNow(); // Force shutdown if it didn't finish
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                }

                break; //Exit the search loop
            } catch (ExecutionException | InterruptedException e) { // This should never happen so we throw an exception
                Throwable cause = e.getCause();
                throw new RuntimeException();
            }

        }

        return searchResults.get(searchResults.size() - 1);
    }

    public static void iterativeDeepeningFixedDepth(Position position, int depth) {
        SearchState searchState = new SearchState(18);
        for (int i = 1; i <= depth; i++) {
            try {
                System.out.println(negamax(NEG_INFINITY, POS_INFINITY, depth, position, searchState).value);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (InvalidPositionException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private static Callable<MoveValue> getMoveValueCallable(Position position, int depth, Position copy, SearchState searchState) {
        return () -> {
            try {
                return negamax(NEG_INFINITY, POS_INFINITY, depth, position, searchState);
            } catch (InterruptedException e) {
                System.out.println("Negamax was interrupted.");
                throw e;
            } catch (InvalidPositionException ipe) {
                System.out.println("IPE caught at callable");
                ipe.printStackTrace();
                throw ipe;
            }
        };
    }

    /**
     */
    public static MoveValue negamax(int alpha, int beta, int depthLeft, Position position, SearchState searchState)
            throws InterruptedException, InvalidPositionException {

        // Check for signal to interrupt the search
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException("Negamax was interrupted by iterative deepening");
        }

        // Store alpha at the start of the search
        int alphaOrig = alpha;

        // Check transposition table
        if (searchState.tt != null && searchState.tt.elementIsUseful(position.zobristHash, depthLeft)) {
            int score = searchState.tt.getScore(position.zobristHash);
            int nodeType = searchState.tt.getNodeType(position.zobristHash);
            int bestMove = searchState.tt.getBestMove(position.zobristHash);

            if (nodeType == 0) { // Exact
                //searchState.firstNonMove = firstMove;
                return new MoveValue(score, bestMove);
            } else if (nodeType == 1) { // Lower bound
                alpha = Math.max(alpha, score);
            } else { // Upper bound
                beta = Math.min(beta, score);
            }

            if (alpha > beta) {
                //searchState.firstNonMove = firstMove;
                return new MoveValue(score, bestMove);
            }
        }

        // Check if search depth reached
        if (depthLeft == 0) {
            //searchState.firstNonMove = firstMove;
            return new MoveValue(quiescenceSearch(alpha, beta, position, searchState), 0);
        }

        // Generate moves and store buffer indices
        int firstMove = searchState.firstNonMove;
        int firstNonMove = MoveGenerator.generateAllMoves(position, searchState.moveBuffer, searchState.firstNonMove);
        searchState.firstNonMove = firstNonMove;

        // Test if game has ended
        int numMoves = firstNonMove - firstMove;
        if (gameOver(numMoves, position, searchState.threeFoldTable)) {
            searchState.firstNonMove = firstMove;
            if (MoveGenerator.kingAttacked(position, position.activePlayer)) {
                return new MoveValue(NEG_INFINITY - depthLeft, 0);
            } else {
                return new MoveValue(0, 0);
            }
        }

        // Move order
        MoveOrder.scoreMoves(position, searchState, firstMove, firstNonMove);
        MoveValue bestMoveValue = new MoveValue(Integer.MIN_VALUE, 0);

        // Search loop
        for (int i = firstMove; i < firstNonMove; i++) {
            if (Thread.currentThread().isInterrupted()) {
                searchState.firstNonMove = firstMove;
                throw new InterruptedException("Negamax was interrupted by iterative deepening");
            }

            // Selection sort and get move
            MoveOrder.bestMoveFirst(searchState, i, firstNonMove);
            int move = searchState.moveBuffer[i];

            // "open" the position
            position.makeMove(move);
            searchState.threeFoldTable.addPosition(position.zobristHash, move);

            int score;
            try {
                if (i == firstMove) {
                    // Full window search for first move
                    score = -negamax(-beta, -alpha, depthLeft - 1, position, searchState).value;
                } else {
                    // Else try to disprove that the pv is the best move with a null-window search
                    score = -negamax(-alpha - 1, -alpha, depthLeft - 1, position, searchState).value;

                    // If disproved, then re-search with full window
                    if (score > alpha) {
                        score = -negamax(-beta, -alpha, depthLeft - 1, position, searchState).value;
                    }
                }
            } finally {
                // Close the position
                searchState.threeFoldTable.popPosition();
                position.unMakeMove(move);
            }

            // Update best move when score is better
            if (score > bestMoveValue.value) {
                bestMoveValue.value = score;
                bestMoveValue.bestMove = move;
            }

            // Update alpha when score is better
            if (score > alpha) {
                alpha = score;
            }

            // Prune when alpha >= beta because the opponent wouldn't make a move that gets here
            if (alpha >= beta) {
                break;
            }
        }

        // After search, reset first nonMove
        searchState.firstNonMove = firstMove;

        if (searchState.tt != null) {
            NodeType nodeType;
            if (bestMoveValue.value <= alphaOrig) { // Failed to find a better move (than is known to exist with alpha), so the value is AT MAXIMUM score
                nodeType = NodeType.UPPER_BOUND;
            } else if (bestMoveValue.value >= beta) { // The value is greater than beta, so an opponent would PRUNE it, but the value is at LEAST score
                nodeType = NodeType.LOWER_BOUND;
            } else {
                nodeType = NodeType.EXACT;
            }

            searchState.tt.addElement(position.zobristHash, bestMoveValue.bestMove, depthLeft, bestMoveValue.value, nodeType);
        }

        return bestMoveValue;
    }

    public static int quiescenceSearch(int alpha, int beta, Position position, SearchState searchState) {
        int standPat = StaticEvaluation.negamaxEvaluatePosition(position);

        int bestValue = standPat;
        if (standPat >= beta)
            return standPat;
        if (alpha < standPat)
            alpha = standPat;

        // Generate loud moves (captures, promotions, checks, etc.)
        int initialFirstNonMove = searchState.firstNonMove;
        int newFirstNonMove = MoveGenerator.generateAllMoves(position, searchState.moveBuffer, searchState.firstNonMove);
        searchState.firstNonMove = newFirstNonMove;

        // Move order
        MoveOrder.scoreMoves(position, searchState, initialFirstNonMove, newFirstNonMove);

        for (int i = initialFirstNonMove; i < newFirstNonMove; i++) {
            MoveOrder.bestMoveFirst(searchState, i, newFirstNonMove);
            int move = searchState.moveBuffer[i];
            if (!MoveEncoding.getIsCapture(move))
                continue;

            // "open" the position
            position.makeMove(move);
            searchState.threeFoldTable.addPosition(position.zobristHash, move);

            // compute the score
            int score;
            try {
                score = -quiescenceSearch(-beta, -alpha, position, searchState);
            } finally {
                position.unMakeMove(move);
                searchState.threeFoldTable.popPosition();
            }

            // "close" the position


            if (score >= beta) {
                searchState.firstNonMove = initialFirstNonMove;
                return score;
            }
            if (score > bestValue)
                bestValue = score;
            if (score > alpha)
                alpha = score;
        }

        searchState.firstNonMove = initialFirstNonMove;
        return bestValue;
    }

    public static boolean gameOver(int moveListSize, Position position, ThreeFoldTable threeFoldTable) {
        if (moveListSize == 0) {
            return true;
        } else if (position.halfMoveCount >= 50) {
            return true;
        } else if (threeFoldTable.positionDrawn(position.zobristHash)) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * Move ordering with selection sort? e.g. choose
     */
     /*
    public static void moveOrder(List<Move> list, long zobristHash, TranspositionTable tt) {
        list.sort(Comparator.comparingInt(move -> -moveValue(move, zobristHash, tt)));
    }
    */

    /**
     * Returns an integer value for a move used for sorting.
     */
     /*
    public static int moveValue(Move move, long zobristHash, TranspositionTable tt) {
        int value = 0;

        if (tt != null) {
            //
        }

        if (move.moveType == MoveType.PROMOTION) {
            value += 500_000;
        }

        if (move.resultWhiteInCheck || move.resultBlackInCheck) {
            value += 1_000_000;
        }

        if (move.moveType == MoveType.CAPTURE) {
            assert move.captureType != null;
            value += 100_000 + StaticEvaluation.evaluateExchange(move);
        }

        return value;
    }
    */
/*
    // most valuable victim/ least valuable agressor
    public static void mVVLVA(List<Move> list) {
        list.sort(Comparator.comparingInt(move -> -(StaticEvaluation.evaluateExchange(move))));
    }
    */

}