package engine.search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Stack;
import java.util.concurrent.*;
import board.Move;
import board.MoveType;
import board.Position;
import customExceptions.InvalidPositionException;
import engine.evaluation.StaticEvaluation;
import moveGeneration.MoveGenerator;
import zobrist.Hashing;
import zobrist.ThreeFoldTable;
import zobrist.transposition.TTElement;
import zobrist.transposition.TranspositionTable;

import java.util.List;
import java.util.stream.Collectors;


public class Search {

    // Values representing very high scores minimizing risk of over/underflow
    public static final int POS_INFINITY = 100_000_000;
    public static final int NEG_INFINITY = -100_000_000;

    // A stack that stores moves in case of negamax thread interruption
    public static final Stack<Move> moveStack = new Stack<Move>();

    /**
    * Class representing a move and evaluation
    */
    public static class MoveValue {
        public int value;
        public Move bestMove;

        public MoveValue(int value, Move bestMove) {
            this.value = value;
            this.bestMove = bestMove;
        }
    }

    /**
    * Runs negamax on increasing depths until the time limit is exceeded
    * @param position position to run the search on
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

            Position copy = new Position(position);
            depth++;
            Callable<MoveValue> task = getMoveValueCallable(position, depth, copy, searchState);
            Future<MoveValue> future = executor.submit(task);

            try {
                // Compute the maximal amount of time this search can take
                long maxTimeForSearch = limitMillis - (System.currentTimeMillis() - start);
                MoveValue result = future.get(maxTimeForSearch, TimeUnit.MILLISECONDS); // Throws timeout exception
                searchResults.add(result);
                System.out.println("info depth " + depth + " pv " + searchResults.getLast().bestMove.toLongAlgebraic() + " score cp " + searchResults.getLast().value);
                //System.out.println("\nDepth: " + depth + "\nEvaluation: " + searchResults.getLast().value + "\nMove: " + searchResults.getLast().bestMove);

                // Stop searching if mate
                if (Math.abs(searchResults.getLast().value) >= POS_INFINITY)
                    return searchResults.getLast();

            } catch (TimeoutException te) {
                System.out.println("Function timed out!");

                future.cancel(true); // Tells the thread that it was interrupted

                try {
                    future.get(); // Waits for the thread to complete or throw an exception
                } catch (CancellationException ce) { // This is expected
                    System.out.println("Task Canceled.");
                } catch (Exception e) {
                    System.out.println("An unexpected error has occurred");
                }

                System.out.println("ready to remove from stack!");

                while (!moveStack.isEmpty()) { // Unmake moves from copy, and remove items from the position hash table
                    Move move = moveStack.pop();

                    System.out.println("unmaking moves, stack size: " + moveStack.size());
                    searchState.threeFoldTable.popPosition();
                    copy.unMakeMove(move); // we should unmake from copy... right?
                }

                // consider adding a equal check for copy and position

                break; //Exit the search loop
            } catch (ExecutionException | InterruptedException e) { // This should never happen so we throw an exception
                Throwable cause = e.getCause();
                throw new RuntimeException();
            }
        }

        executor.shutdown();

        System.out.println("Evaluation: " + searchResults.getLast().value + "\nMove: " + searchResults.getLast().bestMove + ", " + searchResults.getLast().bestMove.moveType);

        return searchResults.get(searchResults.size() - 1);
    }

    private static Callable<MoveValue> getMoveValueCallable(Position position, int depth, Position copy, SearchState searchState) {
        int finalDepth = depth;

        Callable<MoveValue> task = () -> {
           try {
               return negamax(NEG_INFINITY, POS_INFINITY, finalDepth, position, searchState);
           } catch (InterruptedException e) {
               System.out.println("Negamax was interrupted.");
               throw e;
           } catch(InvalidPositionException ipe) {
               throw ipe;
           }
        };
        return task;
    }
    /**
    * Assumes that this will not be called on 0 on a
    *
    */
    public static MoveValue negamax(int alpha, int beta, int depthLeft, Position position, SearchState searchState)
            throws InterruptedException, InvalidPositionException {

        // Check for signal to interrupt the search
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException("Negamax was interrupted by iterative deepening");
        }

        // Store alpha at the start of the search
        int alphaOrig = alpha;

        List<Move> children = MoveGenerator.generateStrictlyLegal(position);
        GameStatus status = gameStatus(children.size(), position, searchState.threeFoldTable);

        long hash = position.zobristHash;

        switch (status) {
            case ONGOING :
                break;
            case WHITE_WIN:
            case BLACK_WIN:
                return new MoveValue(NEG_INFINITY - depthLeft, null);// prefer a higher depth
            case STALEMATE:
            case REPETITION:
            case RULE50:
                return new MoveValue(0, null);
            default:
        }

        if (depthLeft == 0) {
            return new MoveValue(quiescenceSearch(alpha, beta, position, searchState), null);
        }


        if (searchState.tt != null) {
            // Get the tt entry for this position
            TTElement ttEntry = searchState.tt.getElement(hash, depthLeft);

            if (ttEntry != null && ttEntry.bestMove() != null) { // Null if position is terminal
                if (ttEntry.nodeType() == NodeType.EXACT) {
                    return new MoveValue(ttEntry.score(), ttEntry.bestMove());
                } else if (ttEntry.nodeType() == NodeType.LOWER_BOUND) {
                    alpha = Math.max(alpha, ttEntry.score());
                } else {
                    beta = Math.min(beta, ttEntry.score());
                }
                if (alpha > beta) {
                    return new MoveValue(ttEntry.score(), ttEntry.bestMove());
                }
            }
        }

        moveOrder(children, hash, searchState.tt);

        MoveValue bestMoveValue = new MoveValue(Integer.MIN_VALUE, null);

        for (Move move : children) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException("Negamax was interrupted by iterative deepening");
            }

            // "open" the position
            position.makeMove(move);
            searchState.searchMonitor.addPair(move, position);
            searchState.threeFoldTable.addPosition(position.zobristHash, move);

            int score;
            // If we repeat a position twice, consider it a draw.
            if (searchState.threeFoldTable.positionRepeated(position.zobristHash)) {
                score = 0;
            } else {
                // compute it's score
                score = -negamax(-beta, -alpha, depthLeft - 1, position, searchState).value;
            }

            // "close" the position
            searchState.searchMonitor.popStack();
            searchState.threeFoldTable.popPosition();
            position.unMakeMove(move);

            // Update best move when score is better
            if (score > bestMoveValue.value) {
                bestMoveValue.value = score;
                bestMoveValue.bestMove = move;
            }

            // Update alpha when score is better
            if (score > alpha) {
                alpha = score;
            }
            // Prune when alpha >= beta
            if (alpha >= beta) {
                break;
            }
        }

        if (searchState.tt != null) {
            NodeType nodeType = null;
            if (bestMoveValue.value <= alphaOrig) {
                nodeType = NodeType.UPPER_BOUND;
            } else if (bestMoveValue.value >= beta) {
                nodeType = NodeType.LOWER_BOUND;
            } else {
                nodeType = NodeType.EXACT;
            }

            searchState.tt.addElement(hash, bestMoveValue.bestMove, depthLeft, bestMoveValue.value, nodeType);
        }

        return bestMoveValue;
    }


    public static int quiescenceSearch(int alpha, int beta, Position position, SearchState searchState) throws InvalidPositionException{
        int standPat = StaticEvaluation.negamaxEvaluatePosition(position);
        int bestValue = standPat;
        if (standPat >= beta)
            return standPat;
        if (alpha < standPat)
            alpha = standPat;

        List<Move> loudMoves = null;

        // Generate loud moves (captures, promotions, checks, etc.)
        try {
            position.validPosition();
        } catch (InvalidPositionException e) {

        }

        position.validPosition();

        try {
            loudMoves = MoveGenerator.generateStrictlyLegal(position).stream()
                    .filter(move -> move.captureType != null)
                    .collect(Collectors.toList());
        } catch (InvalidPositionException ipe) {
            throw ipe;
        }

        moveOrder(loudMoves, position.zobristHash, searchState.tt);

        for (Move move : loudMoves) {

            // "open" the position
            position.makeMove(move);
            searchState.searchMonitor.addPair(move, position);
            searchState.threeFoldTable.addPosition(position.zobristHash, move);

            // compute the score
            int score = -quiescenceSearch(-beta, -alpha, position, searchState);

            // "close" the position
            position.unMakeMove(move);
            searchState.searchMonitor.popStack();
            searchState.threeFoldTable.popPosition();

            if (score >= beta)
                return score;
            if (score > bestValue)
                bestValue = score;
            if (score > alpha)
                alpha = score;
        }
        return bestValue;
    }

    public static GameStatus gameStatus(int moveListSize, Position position, ThreeFoldTable threeFoldTable) {
        if (moveListSize == 0) {
            if (position.whiteInCheck) {
                return GameStatus.BLACK_WIN;
            } else if (position.blackInCheck) {
                return GameStatus.WHITE_WIN;
            } else {
                return GameStatus.STALEMATE;
            }
        } else if (position.rule50 >= 50) {
            return GameStatus.RULE50;
        } else if (threeFoldTable.positionDrawn(Hashing.computeZobrist(position))) {
            return GameStatus.REPETITION;
        } else {
            return GameStatus.ONGOING;
        }
    }

/*
    public static int quiescenceSearch(int alpha, int beta, Position position, SearchMonitor searchMonitor) throws InvalidPositionException{
        int standPat = StaticEvaluation.negamaxEvaluatePosition(position);
        int bestValue = standPat;
        if (standPat >= beta)
            return standPat;
        if (alpha < standPat)
            alpha = standPat;

        List<Move> loudMoves = null;

        // Generate loud moves (captures, promotions, checks, etc.)
        try {
            position.validPosition();
        } catch (InvalidPositionException e) {

        }

        position.validPosition();

        try {
            loudMoves = MoveGenerator.generateStrictlyLegal(position).stream()
                    .filter(move -> move.captureType != null)
                    .collect(Collectors.toList());
        } catch (InvalidPositionException ipe) {
            throw ipe;
        }
        for (Move move : loudMoves) {

            // "open" the position
            position.makeMove(move);
            searchMonitor.addPair(move, position);
            ThreePly.addPosition(position.zobristHash, move.reversible);

            // compute the score
            int score = -quiescenceSearch(-beta, -alpha, position, searchMonitor);

            // "close" the position
            position.unMakeMove(move);
            searchMonitor.popStack();
            ThreePly.popPosition();

            if (score >= beta)
                return score;
            if (score > bestValue)
                bestValue = score;
            if (score > alpha)
                alpha = score;
        }
        return bestValue;
    }
*/

/*
 * Move ordering with selection sort? e.g. choose
 */
    public static void moveOrder(List<Move> list, long zobristHash, TranspositionTable tt) {
        list.sort(Comparator.comparingInt(move -> -moveValue(move, zobristHash, tt)));
    }
    /**
    * Returns an integer value for a move used for sorting.
    */
    public static int moveValue(Move move, long zobristHash, TranspositionTable tt) {
       int value = 0;

       if (tt != null) {
           TTElement element = tt.getElement(zobristHash, 0);
           if (element != null) {
               if (move.equals(element.bestMove())) {
                   value += 10_000_000;
               }
           }
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

    // most valuable victim/ least valuable agressor
    public static void mVVLVA(List<Move> list) {
        list.sort(Comparator.comparingInt(move -> -(StaticEvaluation.evaluateExchange(move))));
    }

}