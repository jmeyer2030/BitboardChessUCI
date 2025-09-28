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

    public static final int MATED_VALUE = 90_000_000;
    public static final int MATED_SCORE = 80_000_000;

    public static final int[] futilityMargin = {0, 200, 500, 900};

    public static final int MAX_SEARCH_DEPTH = 256;

    public static final int NULL_MOVE_PRUNING_REDUCTION = 3;
    public static final int NUM_NON_PV_FULL_DEPTH_SEARCHES = 3;

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
        while (depth < MAX_SEARCH_DEPTH) {
            depth++;

            // Create and submit search task
            Callable<MoveValue> task = getSearchCallable(position, depth, positionState);
            Future<MoveValue> future = executor.submit(task);

            try {
                // Compute the max amount of time this search can take
                long maxTimeForSearch = start + limitMillis - System.currentTimeMillis();

                // Try to get the result of the task. If time limit exceeded, throws timeout exception
                MoveValue result = future.get(maxTimeForSearch, TimeUnit.MILLISECONDS);
                searchResults.add(result);

                System.out.println("info depth " + depth + " pv " +
                        positionState.pvTable.getPVLine()
                        + " score cp " + searchResults.getLast().value);

                // If mate found, only search with 3 higher depth
                if (Math.abs(searchResults.getLast().value) >= (MATED_SCORE) && searchResults.size() >= 3) {
                    int size = searchResults.size();
                    int lastValue = searchResults.get(size - 1).value;
                    int secondValue = searchResults.get(size - 2).value;
                    int thirdValue = searchResults.get(size - 3).value;
                    if (lastValue == secondValue && secondValue == thirdValue)
                        break;
                }

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

        if (searchResults.size() == 0) {
            throw new RuntimeException("Search failed because a single depth search didn't finish in time");
        }

        return searchResults.get(searchResults.size() - 1);
    }

    /**
     * Generates a Callable task that runs negamax
     *
     * @param position      negamax param
     * @param depth         negamax param
     * @param positionState negamax param
     * @return MoveValue callable
     */
    public static Callable<MoveValue> getSearchCallable(Position position, int depth, PositionState positionState) {
        return () -> {
            try {
                return negamax(NEG_INFINITY, POS_INFINITY, depth, position, positionState, true, 0, false);
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

                result = getSearchCallable(position, i, positionState).call();
                //result = negamax(NEG_INFINITY, POS_INFINITY, i, position, positionState, true, 0, false);

                String moveLAN = MoveEncoding.getLAN(result.bestMove);
                System.out.println("Depth: " + i + " | Move: " + moveLAN + " | Value: " + result.value);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (InvalidPositionException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println(positionState.pvTable.getPVLine());
    }

    /**
     * PVS Search
     *
     * @param alpha         the score that the active player is guaranteed in another branch
     * @param beta          the score the non-active player is guaranteed in another branch
     * @param depthLeft     the depth left in the current search
     * @param position      the position to search
     * @param positionState the state of the position/search
     * @param isRoot        if this call is the root
     * @param ply           the current ply we are on. The initial call to search is ply 0, then if we look at their children that would be ply 1.
     * @param isPV          if the current node is the principle variation
     * @return moveValue the best move and it's associated score
     */
    public static MoveValue negamax(int alpha, int beta, int depthLeft, Position position, PositionState positionState, boolean isRoot, int ply, boolean isPV)
            throws InterruptedException {

        // Check for signal to interrupt the search
        if (Thread.currentThread().isInterrupted()) {
            System.out.println("interrupted in negamax!");
            throw new InterruptedException("Negamax was interrupted by iterative deepening");
        }
        positionState.pvTable.setPVLength(ply);

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
                return new MoveValue(-(MATED_VALUE - ply), 0);
            } else {
                return new MoveValue(0, 0);
            }
        }
        //=============== Check transposition table===============
        int eval = 0; // If tt, save eval for use in RFP
        boolean foundTTScore = false;
        if (positionState.tt != null && positionState.tt.elementIsUseful(position.zobristHash, depthLeft)) {
            foundTTScore = true;
            eval = positionState.tt.getScore(position.zobristHash);
            int nodeType = positionState.tt.getNodeType(position.zobristHash);
            int bestMove = positionState.tt.getBestMove(position.zobristHash);

            eval = scoreFromTT(eval, ply);

            if (nodeType == 0) { // Exact
                positionState.firstNonMove = firstMove;
                return new MoveValue(eval, bestMove);
            } else if (nodeType == 1) { // Lower bound
                alpha = Math.max(alpha, eval);
            } else { // Upper bound
                beta = Math.min(beta, eval);
            }

            if (alpha >= beta) {
                positionState.firstNonMove = firstMove;
                return new MoveValue(eval, bestMove);
            }
        }

        // Check if search depth reached (consider check extensions?)
        if (depthLeft == 0) {
            positionState.firstNonMove = firstMove;
            return new MoveValue(Quiesce.quiescenceSearch(alpha, beta, position, positionState, ply + 1), 0);
        }

        if (!foundTTScore) {
            eval = position.nnue.computeOutput(position.activePlayer);
        }

        // ===============Reverse Futility Pruning===============
        //  - If not root, eval is so good that it and a margin is better than beta
        //  - If opponent is getting mated, don't reduce so we can find quicker mates
        int margin = 150 * depthLeft;

        if (!isRoot && eval >= beta + margin && !position.inCheck && !isPV && beta > -MATED_SCORE) {
            positionState.firstNonMove = firstMove;
            return new MoveValue(eval, 0);
        }


        // ===============Null Move Pruning===============
        //if (!isRoot && depthLeft >= 3 && eval >= beta && !position.inCheck && numMoves >= 7) {
        if (!isRoot && depthLeft >= 3 && eval >= beta && nmpConditionsMet(position)) {
            int reduction = NULL_MOVE_PRUNING_REDUCTION;
            position.makeNullMove();
            int score;
            try {
                score = -negamax(-beta, -beta + 1, depthLeft - reduction, position, positionState, false, ply + 1, false).value;
            } finally {
                position.unmakeNullMove();
            }
            // If a null move failed high over beta, then certainly the best move would as well, so we prune
            if (score >= beta) {
                positionState.firstNonMove = firstMove;
                return new MoveValue(score, 0); // Proposed change return score instead of beta
            }
        }

        // Check if we can use futility pruning (low depth, not pv, not in check, not looking for mate, quiet move unlikely to increase alpha, more than one legal move)
        boolean useFutilityPruning = false;
        if (depthLeft <= 3 && !isPV && !position.inCheck && Math.abs(alpha) < MATED_SCORE && eval + futilityMargin[depthLeft] <= alpha) {
            useFutilityPruning = true;
        }


        // Move order
        MoveOrder.scoreMoves(position, positionState, firstMove, firstNonMove, ply);
        MoveValue bestMoveValue = new MoveValue(Integer.MIN_VALUE, 0);

        // Search loop
        for (int i = firstMove; i < firstNonMove; i++) {
            if (Thread.currentThread().isInterrupted()) {
                System.out.println("interrupted in negamax!");
                positionState.firstNonMove = firstMove;
                throw new InterruptedException("Negamax was interrupted by iterative deepening");
            }

            // Selection sort and get move
            MoveOrder.bestMoveFirst(positionState, i, firstNonMove);

            int move = positionState.moveBuffer[i];


            // Store if we were in check to see if we can reduce later
            boolean wasInCheck = position.inCheck;


            // "open" the position
            position.makeMove(move);
            positionState.threeFoldTable.addPosition(position.zobristHash, move);


            if (useFutilityPruning && // Set to use fp
                    i != firstMove && // Not the pv move
                    !MoveEncoding.getIsCapture(move) && // not a capture (possible to increase alpha)
                    !MoveEncoding.getIsPromotion(move) && // not a promotion
                    !position.inCheck) { // not a check
                // Close the position
                positionState.threeFoldTable.popPosition();
                position.unMakeMove(move);
                continue;
            }

            // ===============PV search===============
            //  - If first move:
            //      - Run full search
            //      - If non-pv, we have a null window so we don't need special handling.
            //  - else
            //      - Run a null window search, reducing depending on LMR
            int score;
            try {
                if (i == firstMove) {
                    score = -negamax(-beta, -alpha, depthLeft - 1, position, positionState, false, ply + 1, isPV).value;
                } else { // Note that we
                    // ===============Late Move Reduction===============
                    //  - Use late move reduction on non-pv moves with log formula
                    if (i >= firstMove + NUM_NON_PV_FULL_DEPTH_SEARCHES && depthLeft > 3) {
                        int reduction = (int) Math.round(.99 + (Math.log(depthLeft) * Math.log(i - firstMove)) / 3.14);
                        score = -negamax(-alpha - 1, -alpha, depthLeft - 1 - reduction, position, positionState, false, ply + 1, false).value;

                        if (score >= beta) { // If search fails high, research at full depth
                            score = -negamax(-alpha - 1, -alpha, depthLeft - 1, position, positionState, false, ply + 1, false).value;
                        }
                    } else {
                        score = -negamax(-alpha - 1, -alpha, depthLeft - 1, position, positionState, false, ply + 1, false).value;
                    }

                    // If dispwr
                    if (score > alpha && (beta - alpha) > 1) {
                        score = -negamax(-beta, -alpha, depthLeft - 1, position, positionState, false, ply + 1, isPV).value; // It is now PV if parent is because it exceeded alpha
                    }
                }
            } finally {
                // Close the position
                positionState.threeFoldTable.popPosition();
                position.unMakeMove(move);
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

                positionState.pvTable.writePVMove(move, ply);


                // Prune when alpha >= beta because the opponent wouldn't make a move that gets here
                if (alpha >= beta) {
                    // If not a capture, add to history heuristic
                    if (!MoveEncoding.getIsCapture(move)) {
                        // Check that the killer move isn't the same
                        if (positionState.killerMoves.killerMoves[0][ply] != move) {
                            // If not, add it. Else nothing.
                            positionState.killerMoves.killerMoves[1][ply] = positionState.killerMoves.killerMoves[0][ply];
                            positionState.killerMoves.killerMoves[0][ply] = move;
                        }

                        positionState.historyHeuristic.addMove(position.activePlayer, move, depthLeft);

                        // For each other quiet searched, penalize
                        for (int j = firstMove; j < i; j++) {
                            if (!MoveEncoding.getIsCapture(positionState.moveBuffer[j]))
                                positionState.historyHeuristic.penalizeMove(position.activePlayer, positionState.moveBuffer[j], depthLeft);
                        }
                    }

                    break;
                }
            }
        }

        // After search, reset first nonMove
        positionState.firstNonMove = firstMove;

        if (positionState.tt != null && bestMoveValue.bestMove != 0) {
            NodeType nodeType;

            // Adjust for whatever ply we're on (if mate)
            int ttValue = scoreToTT(bestMoveValue.value, ply);

            if (bestMoveValue.value <= alphaOrig) { // Failed to find a better move (than is known to exist with alpha), so the value is AT MAXIMUM score
                nodeType = NodeType.UPPER_BOUND;
            } else if (bestMoveValue.value >= beta) { // The value is greater than beta, so an opponent would PRUNE it, but the value is at LEAST score
                nodeType = NodeType.LOWER_BOUND;
            } else {
                nodeType = NodeType.EXACT;
            }

            positionState.tt.addElement(position.zobristHash, bestMoveValue.bestMove, depthLeft, ttValue, nodeType);
        }

        return bestMoveValue;
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
     * Futility Pruning conditions NOT met if any apply:
     * - Position is in check
     * - TT move doesn't exist
     * - PV node
     *
     * @param position position
     * @return if we can do FP
     */
    public static boolean fpConditionsMet(Position position, boolean isPV) {
        if (position.inCheck ||
                isPV)
            return false;
        return true;
    }

    /**
     * Null Move pruning conditions are NOT met if any:
     * - position is in check
     * - active player has only kings and pawns
     * An alternative approach worth testing:
     * - We can NMP iff legal moves >= 8 (8 is arbitrary, but suggests no zugzwang)
     *
     * @param position position
     * @return if we can do NMP
     */
    public static boolean nmpConditionsMet(Position position) {
        int color = position.activePlayer;
        if (position.inCheck || // If in check OR
                (position.pieceCounts[color][1] == 0 && position.pieceCounts[color][2] == 0 && // STM no non-pawn pieces
                        position.pieceCounts[color][3] == 0 && position.pieceCounts[color][4] == 0))
            return false;
        return true;
    }

    public static int scoreToTT(int score, int ply) {
        return (score > MATED_SCORE) ? score + ply : score < -MATED_SCORE ? score - ply : score;
    }

    public static int scoreFromTT(int score, int ply) {
        return (score > MATED_SCORE) ? score - ply : score < -MATED_SCORE ? score + ply : score;
    }

}

     /*


    }

    public static boolean nmpConditionsMet(Position position) {
        return !position.inCheck && (position.pieceCounts[0][1] + position.pieceCounts[1][1] != 0 || position.pieceCounts[0][2] + position.pieceCounts[1][2] != 0 || position.pieceCounts[0][3] + position.pieceCounts[1][3] != 0 || position.pieceCounts[0][4] + position.pieceCounts[1][4] != 0);
    }
    */

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

