package engine.search;

import java.util.ArrayList;
import java.util.concurrent.*;

import board.MoveEncoding;
import board.Position;
import board.PositionState;
import customExceptions.InvalidPositionException;
import moveGeneration.MoveGenerator;
import zobrist.ThreeFoldTable;

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

    /**
     * Runs negamax on increasing depths until the time limit is exceeded
     *
     * @param position    position to run the search on
     * @param limitMillis time  limit in milliseconds for the search
     * @return moveValue associated with the deepest search of the position
     */
    public static MoveValue iterativeDeepening(Position position, long limitMillis, PositionState positionState) {
        // Create a list representing the best moves generated at each depth
        List<MoveValue> searchResults = new ArrayList<>();

        // Store the current time so we know when to stop searching
        long start = System.currentTimeMillis();

        // Create a new thread to run the search on
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Initialize search depth and search while time hasn't been exceeded
        int depth = 0;
        while (depth < 256) {
            depth++;

            // Create and submit search task
            Callable<MoveValue> task = getMoveValueCallable(position, depth, positionState);
            Future<MoveValue> future = executor.submit(task);

            try {
                // Compute the max amount of time this search can take
                long maxTimeForSearch = start + limitMillis - System.currentTimeMillis();

                // Try to get the result of the task. If time limit exceeded, throws timeout exception
                MoveValue result = future.get(maxTimeForSearch, TimeUnit.MILLISECONDS);
                searchResults.add(result);

                System.out.println("info depth " + depth + " pv " +
                        MoveEncoding.getLAN(searchResults.getLast().bestMove)
                        + " score cp " + searchResults.getLast().value);

                // Stop searching if mate
                if (Math.abs(searchResults.getLast().value) >= (POS_INFINITY - 1000))
                    return searchResults.getLast();

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

                // reset firstNonMove since search timed out
                positionState.firstNonMove = 0;


                break; //Exit the search loop
            } catch (ExecutionException e) { // This should never happen so we throw an exception
                System.out.println(e.getCause());
                System.out.println(e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Unexpected execution exception caught");
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException("Unexpected interrupted exception caught");
            }
        }

        return searchResults.get(searchResults.size() - 1);
    }

    /**
     * Realistically tests the search speed by stopping search at a certain depth rather than time
     * Uses tt and starts at depth 1 which makes it preferable to just testing negamax search
     *
     * @param position position to search
     * @param depth    depth to search to
     */
    public static void iterativeDeepeningFixedDepth(Position position, int depth) {
        PositionState positionState = new PositionState(18);
        for (int i = 1; i <= depth; i++) {
            try {
                MoveValue result;

                result = negamax(NEG_INFINITY, POS_INFINITY, i, position, positionState, true, 0);

                String moveLAN = MoveEncoding.getLAN(result.bestMove);
                System.out.println("Depth: " + i + " | Move: " + moveLAN + " | Value: " + result.value);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (InvalidPositionException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private static Callable<MoveValue> getMoveValueCallable(Position position, int depth, PositionState positionState) {
        return () -> {
            try {
                return negamax(NEG_INFINITY, POS_INFINITY, depth, position, positionState, true, 0);
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
     * PVS Search
     * @param ply the current ply we are on. The initial call to search is ply 0, then if we look at their children that would be ply 1.
     */
    public static MoveValue negamax(int alpha, int beta, int depthLeft, Position position, PositionState positionState, boolean isRoot, int ply)
            throws InterruptedException, InvalidPositionException {

        // Check for signal to interrupt the search
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException("Negamax was interrupted by iterative deepening");
        }

        // Store alpha at the start of the search (so that we can see if search increased it)
        int alphaOrig = alpha;

        // Generate moves and store buffer indices
        int firstMove = positionState.firstNonMove;
        int firstNonMove = MoveGenerator.generateAllMoves(position, positionState.moveBuffer, positionState.firstNonMove);
        positionState.firstNonMove = firstNonMove;

        // Test if game has ended
        int numMoves = firstNonMove - firstMove;
        if (!isRoot && gameOver(numMoves, position, positionState.threeFoldTable)) { // if root, we shouldn't check for game end
            positionState.firstNonMove = firstMove;
            if (position.inCheck && numMoves == 0) { // Checkmate IF in check AND no moves (otherwise could be repetition)
                return new MoveValue(NEG_INFINITY, 0);
            } else {
                return new MoveValue(0, 0);
            }
        }

        // Check transposition table
        if (positionState.tt != null && positionState.tt.elementIsUseful(position.zobristHash, depthLeft)) {
            int score = positionState.tt.getScore(position.zobristHash);
            int nodeType = positionState.tt.getNodeType(position.zobristHash);
            int bestMove = positionState.tt.getBestMove(position.zobristHash);

            if (nodeType == 0) { // Exact
                positionState.firstNonMove = firstMove;
                return new MoveValue(score, bestMove);
            } else if (nodeType == 1) { // Lower bound
                alpha = Math.max(alpha, score);
            } else { // Upper bound
                beta = Math.min(beta, score);
            }

            if (alpha > beta) {
                positionState.firstNonMove = firstMove;
                return new MoveValue(score, bestMove);
            }
        }

        // Check if search depth reached
        if (depthLeft == 0) {
            if (position.inCheck) {
                depthLeft++;
            } else {
                positionState.firstNonMove = firstMove;
                return new MoveValue(quiescenceSearch(alpha, beta, position, positionState, ply + 1), 0);
            }
        }

        // Futility Pruning
        int eval = position.nnue.computeOutput(position.activePlayer);
        int margin = 150 * depthLeft;

        if (!isRoot && eval >= beta + margin && fpConditionsMet(position, positionState)) { // First check easy exits
            positionState.firstNonMove = firstMove;
            return new MoveValue(eval, 0);
        }

        // Null Move Pruning
        //boolean isNullWindow = (alpha == beta - 1);
        if (!isRoot && depthLeft >= 3 && eval >= beta && nmpConditionsMet(position)) {
            // Plys fewer to search
            int reduction = 3;
            position.makeNullMove();
            int score;
            try {
                score = -negamax(-beta, -beta + 1, depthLeft - reduction, position, positionState, false, ply + 1).value;
            } finally {
                position.unmakeNullMove();
            }
            // If a null move failed high over beta, then certainly the best move would as well, so we prune
            if (score >= beta) {
                positionState.firstNonMove = firstMove;
                return new MoveValue(score, 0); // Proposed change return score instead of beta
            }
        }

        // Move order
        MoveOrder.scoreMoves(position, positionState, firstMove, firstNonMove, ply);
        MoveValue bestMoveValue = new MoveValue(Integer.MIN_VALUE, 0);

        // Search loop
        for (int i = firstMove; i < firstNonMove; i++) {
            if (Thread.currentThread().isInterrupted()) {
                positionState.firstNonMove = firstMove;
                throw new InterruptedException("Negamax was interrupted by iterative deepening");
            }

            // Selection sort and get move
            MoveOrder.bestMoveFirst(positionState, i, firstNonMove);
            int move = positionState.moveBuffer[i];

            // "open" the position
            position.makeMove(move);
            positionState.threeFoldTable.addPosition(position.zobristHash, move);

            int score;
            try {
                if (i == firstMove) {
                    // Full window search for first move
                    score = -negamax(-beta, -alpha, depthLeft - 1, position, positionState, false, ply + 1).value;
                } else {
                    // Else try to disprove that the pv is the best move with a null-window search
                    score = -negamax(-alpha - 1, -alpha, depthLeft - 1, position, positionState, false, ply + 1).value;

                    // If disproved, then re-search with full window
                    if (score > alpha && (beta - alpha > 1)) {
                        score = -negamax(-beta, -alpha, depthLeft - 1, position, positionState, false, ply + 1).value;
                    }
                }
            } finally {
                // Close the position
                positionState.threeFoldTable.popPosition();
                position.unMakeMove(move);
            }

            if (score > 99_000_000) { // If score evaluates to a mate for us, decrement since that evaluation is on next ply
                score = score - 1;
            } else if (score < -99_000_000) { // If score is mate for them, increment since its on next ply
                score = score + 1;
            }

            // Update best move when score is better
            if (score > bestMoveValue.value) {
                bestMoveValue.value = score;
                bestMoveValue.bestMove = move;
            }

            // If this is a new lower bound
            if (score > alpha) {
                // Update alpha
                alpha = score;

                // If not a capture, add to history heuristic
                int bonus = 300 * depthLeft - 250;
                if (!MoveEncoding.getIsCapture(move)) {
                    // Check that the killer move isn't the same
                    //if (positionState.killerMoves.killerMoves[0][ply] != move) {
                        // If not, add it. Else nothing.
                        positionState.killerMoves.killerMoves[1][ply] = positionState.killerMoves.killerMoves[0][ply];
                        positionState.killerMoves.killerMoves[0][ply] = move;
                    //}



                    positionState.historyHeuristic.addMove(position.activePlayer, move, depthLeft, bonus);
                }

                // Prune when alpha >= beta because the opponent wouldn't make a move that gets here
                if (alpha >= beta) {
                    break;
                }
            }
        }

        // After search, reset first nonMove
        positionState.firstNonMove = firstMove;

        if (positionState.tt != null && bestMoveValue.bestMove != 0) {
            NodeType nodeType;
            if (bestMoveValue.value <= alphaOrig) { // Failed to find a better move (than is known to exist with alpha), so the value is AT MAXIMUM score
                nodeType = NodeType.UPPER_BOUND;
            } else if (bestMoveValue.value >= beta) { // The value is greater than beta, so an opponent would PRUNE it, but the value is at LEAST score
                nodeType = NodeType.LOWER_BOUND;
            } else {
                nodeType = NodeType.EXACT;
            }

            positionState.tt.addElement(position.zobristHash, bestMoveValue.bestMove, depthLeft, bestMoveValue.value, nodeType);
        }

        return bestMoveValue;
    }

    public static int quiescenceSearch(int alpha, int beta, Position position, PositionState positionState, int ply) {
        int standPat = position.nnue.computeOutput(position.activePlayer);

        int bestValue = standPat;

        if (standPat >= beta)
            return bestValue;

        if (alpha < standPat)
            alpha = standPat;


        // Generate moves
        int initialFirstNonMove = positionState.firstNonMove;
        int newFirstNonMove = MoveGenerator.generateAllMoves(position, positionState.moveBuffer, positionState.firstNonMove);
        positionState.firstNonMove = newFirstNonMove;

        // look for checkmate
        int numMoves = newFirstNonMove - initialFirstNonMove;
        if (numMoves == 0 && position.inCheck) {
            return NEG_INFINITY;
        }


        // Move order
        MoveOrder.scoreMoves(position, positionState, initialFirstNonMove, newFirstNonMove, ply);

        for (int i = initialFirstNonMove; i < newFirstNonMove; i++) {
            MoveOrder.bestMoveFirst(positionState, i, newFirstNonMove);
            int move = positionState.moveBuffer[i];

            if (!MoveEncoding.getIsCapture(move))
                continue;


            // "open" the position
            position.makeMove(move);
            positionState.threeFoldTable.addPosition(position.zobristHash, move);

            // compute the score
            int score;
            try {
                score = -quiescenceSearch(-beta, -alpha, position, positionState, ply + 1);
            } finally { // "close" the position
                position.unMakeMove(move);
                positionState.threeFoldTable.popPosition();
            }

            bestValue = Math.max(bestValue, score);

            alpha = Math.max(alpha, score);

            if (alpha >= beta) {
                positionState.firstNonMove = initialFirstNonMove;
                return bestValue;
            }
        }

        positionState.firstNonMove = initialFirstNonMove;
        return bestValue;
    }

    /**
     * Returns true if the game has ended.
     * NOTE: This does not indicate HOW it ended. This is left to the search
     * - If there are legal moves, then score = 0
     * - Else: score = inCheck ? -inf : 0
     *
     * @param moveListSize   number of legal moves
     * @param position       position
     * @param threeFoldTable threeFoldTable
     * @return if the game has ended
     */
    public static boolean gameOver(int moveListSize, Position position, ThreeFoldTable threeFoldTable) {
        if (moveListSize == 0 || position.halfMoveCount >= 50 || threeFoldTable.positionRepeated(position.zobristHash)) {
            return true;
        }
        return false;
    }

    /**
     * Futility Pruning conditions NOT met if:
     * - Position is in check
     * - TT move doesn't exist
     *
     * @param position      position
     * @param positionState positionState (for tt)
     * @return if we can do FP
     */
    public static boolean fpConditionsMet(Position position, PositionState positionState) {
        if (position.inCheck && // In Check
                positionState.tt.getBestMove(position.zobristHash) == 0) // tt move dne
            return false;
        return true;
    }

    /**
     * Null Move pruning conditions are NOT met if:
     * - position is in check
     * - there are only kings and pawns
     *
     * @param position position
     * @return if we can do NMP
     */
    /*
    public static boolean nmpConditionsMet(Position position) {
        int color = position.activePlayer;
        if (position.inCheck || // If in check OR
                (position.pieceCounts[color][1] + position.pieceCounts[color][2] + // Active player only has pawns
                        position.pieceCounts[color][3] + position.pieceCounts[color][4]) == 0) {
            return false;
        }

        return true;
    }
    */

    public static boolean nmpConditionsMet(Position position) {
        return !position.inCheck && (position.pieceCounts[0][1] + position.pieceCounts[1][1] != 0 || position.pieceCounts[0][2] + position.pieceCounts[1][2] != 0 || position.pieceCounts[0][3] + position.pieceCounts[1][3] != 0 || position.pieceCounts[0][4] + position.pieceCounts[1][4] != 0);
    }
}


/*
    Aspiration window search for the getMoveValueCallable


                MoveValue result;
                if (positionState.tt.elementExists(position.zobristHash)) {
                    int score = positionState.tt.getScore(position.zobristHash);

                    int betaInc = 25;
                    int alphaInc = 25;

                    int alpha = score - alphaInc;
                    int beta = score + betaInc;

                    int scoreScaling = 2;

                    int numSearches = 0;
                    while (true) {
                        numSearches++;
                        result = negamax(alpha, beta, depth, position, positionState, true);

                        if (result.value >= beta) { // If failed high
                            betaInc *= scoreScaling;
                            beta += betaInc;
                            if (numSearches >= 3) { // If search limit exceeded
                                beta = POS_INFINITY;
                            }
                        } else if (result.value <= alpha) { // If failed low
                            alphaInc *= scoreScaling;
                            alpha -= alphaInc;
                            if (numSearches >= 3) { // If Search limit exceeded
                                alpha = NEG_INFINITY;
                            }
                        } else { // Fell within bounds
                            break;
                        }

                    }
                } else {
                    result = negamax(NEG_INFINITY, POS_INFINITY, depth, position, positionState, true);
                }

                return result;
                */

                /*
                Delta pruning
        int bigDelta = 975; // Queen value
        if (bestValue + bigDelta < alpha) {
            return alpha;
        }
        */

        /*
        if (alpha < bestValue) {
            alpha = bestValue;
        }


    public static int[] pieceValues = new int[]{100, 300, 325, 500, 900};
        */

        /*
                if (positionState.tt.elementExists(position.zobristHash)) {
                    int score = positionState.tt.getScore(position.zobristHash);

                    int betaInc = 25;
                    int alphaInc = 25;

                    int alpha = score - alphaInc;
                    int beta = score + betaInc;

                    int scoreScaling = 2;

                    int numSearches = 0;
                    while (true) {
                        numSearches++;
                        //System.out.println("Depth: " + i + " Alpha: " + alpha + " Beta: " + beta);
                        result = negamax(alpha, beta, i, position, positionState, true);

                        if (result.value >= beta) { // If failed high
                            //System.out.println("Failed High!");
                            betaInc *= scoreScaling;
                            beta += betaInc;
                            if (numSearches >= 3) {
                                beta = POS_INFINITY;
                            }
                        } else if (result.value <= alpha) { // If failed low
                            //System.out.println("Failed Low!");
                            alphaInc *= scoreScaling;
                            alpha -= alphaInc;
                            if (numSearches >= 3) {
                                alpha = NEG_INFINITY;
                            }
                        } else { // Fell within bounds
                            //System.out.println("Just right!");
                            break;
                        }

                    }
                } else {
                    result = negamax(NEG_INFINITY, POS_INFINITY, i, position, positionState, true);
                }
                */


// Delta cutoff
            /*
            if (standPat + pieceValues[MoveEncoding.getCapturedPiece(move)] + 200 < alpha &&
                    !MoveEncoding.getIsPromotion(move))
                continue;
                */

