package com.jmeyer2030.driftwood.search;

import java.util.ArrayList;
import java.util.concurrent.*;

import com.jmeyer2030.driftwood.board.MoveEncoding;
import com.jmeyer2030.driftwood.board.Position;
import com.jmeyer2030.driftwood.board.SharedTables;
import com.jmeyer2030.driftwood.board.InvalidPositionException;
import com.jmeyer2030.driftwood.movegeneration.MoveGenerator;
import com.jmeyer2030.driftwood.board.ThreeFoldTable;

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
     * @param position      position to run the search on
     * @param limitMillis   time limit in milliseconds for the search
     * @param searchContext per-search scratch space
     * @param sharedTables  long-lived shared tables (TT, threefold)
     * @return moveValue associated with the deepest search of the position
     */
    public static MoveValue iterativeDeepening(Position position, long limitMillis, SearchContext searchContext, SharedTables sharedTables) {
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
            Callable<MoveValue> task = getSearchCallable(position, depth, searchContext, sharedTables);
            Future<MoveValue> future = executor.submit(task);

            try {
                // Compute the max amount of time this search can take
                long maxTimeForSearch = start + limitMillis - System.currentTimeMillis();

                // Try to get the result of the task. If time limit exceeded, throws timeout exception
                MoveValue result = future.get(maxTimeForSearch, TimeUnit.MILLISECONDS);
                searchResults.add(result);

                System.out.println("info depth " + depth + " pv " +
                        searchContext.pvTable.getPVLine()
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
                searchContext.firstNonMove = 0;

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
     * @param searchContext per-search scratch space
     * @param sharedTables  long-lived shared tables
     * @return MoveValue callable
     */
    public static Callable<MoveValue> getSearchCallable(Position position, int depth, SearchContext searchContext, SharedTables sharedTables) {
        return () -> {
            try {
                int score = negamax(NEG_INFINITY, POS_INFINITY, depth, position, searchContext, sharedTables, true, 0, false);
                return new MoveValue(score, searchContext.bestMoves[0]);
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
        SearchContext searchContext = new SearchContext();
        SharedTables sharedTables = new SharedTables(18);
        for (int i = 1; i <= depth; i++) {
            try {
                MoveValue result;

                result = getSearchCallable(position, i, searchContext, sharedTables).call();

                String moveLAN = MoveEncoding.getLAN(result.bestMove);
                System.out.println("Depth: " + i + " | Move: " + moveLAN + " | Value: " + result.value);
            } catch (Exception e) {
                throw new RuntimeException("Unexpected exception from search");
            }
        }

        System.out.println(searchContext.pvTable.getPVLine());
    }

    /**
     * PVS Search
     *
     * @param alpha         the score that the active player is guaranteed in another branch
     * @param beta          the score the non-active player is guaranteed in another branch
     * @param depthLeft     the depth left in the current search
     * @param position      the position to search
     * @param searchContext per-search scratch space
     * @param sharedTables  long-lived shared tables
     * @param isRoot        if this call is the root
     * @param ply           the current ply we are on
     * @param isPV          if the current node is the principle variation
     *
     * @return score the evaluation of the best line
     */
    public static int negamax(int alpha, int beta, int depthLeft, Position position, SearchContext searchContext, SharedTables sharedTables, boolean isRoot, int ply, boolean isPV)
            throws InterruptedException {

        // Check for signal to interrupt the search
        if (Thread.currentThread().isInterrupted()) {
            System.out.println("interrupted in negamax!");
            throw new InterruptedException("Negamax was interrupted by iterative deepening");
        }
        searchContext.pvTable.setPVLength(ply);

        int alphaOrig = alpha;

        //=============== Look for draw with 50-move or repetition ===============
        if (!isRoot) {
            if (sharedTables.threeFoldTable.positionRepeated(position.zobristHash)) {
                return 0;
            } else if (position.halfMoveCount >= 50) {
                return 0;
            }
        }

        //=============== Move Gen ===============
        int firstMove = searchContext.firstNonMove;
        int firstNonMove = MoveGenerator.generateAllMoves(position, searchContext.moveBuffer, searchContext.firstNonMove);
        searchContext.firstNonMove = firstNonMove;

        //=============== No move game end (mate or stalemate) ===============
        int numMoves = firstNonMove - firstMove;
        if (!isRoot && numMoves == 0) {
            searchContext.firstNonMove = firstMove;
            if (position.inCheck) {
                return -(MATED_VALUE - ply);
            } else {
                return 0;
            }
        }

        //=============== Check transposition table===============
        int eval = 0;
        boolean foundTTScore = false;
        if (sharedTables.tt != null && sharedTables.tt.elementIsUseful(position.zobristHash, depthLeft)) {
            foundTTScore = true;
            eval = sharedTables.tt.getScore(position.zobristHash);
            int nodeType = sharedTables.tt.getNodeType(position.zobristHash);
            int bestMove = sharedTables.tt.getBestMove(position.zobristHash);

            eval = scoreFromTT(eval, ply);

            if (nodeType == 0) { // Exact
                searchContext.firstNonMove = firstMove;
                searchContext.bestMoves[ply] = bestMove;
                return eval;
            } else if (nodeType == 1) { // Lower bound
                alpha = Math.max(alpha, eval);
            } else { // Upper bound
                beta = Math.min(beta, eval);
            }

            if (alpha >= beta) {
                searchContext.firstNonMove = firstMove;
                searchContext.bestMoves[ply] = bestMove;
                return eval;
            }
        }

        //=============== Return based on search depth ===============
        // Check if search depth reached (consider check extensions?)
        if (depthLeft <= 0) {
            searchContext.firstNonMove = firstMove;
            return Quiesce.quiescenceSearch(alpha, beta, position, searchContext, sharedTables, ply + 1);
        }

        //=============== Compute Eval ===============
        // Should be done before uses of eval in case TT didn't find
        if (!foundTTScore) {
            eval = position.evaluator.computeOutput(position.activePlayer);
        }

        // ===============Reverse Futility Pruning===============
        //  - If not root, eval is so good that it and a margin is better than beta
        //  - If opponent is getting mated, don't reduce so we can find quicker mates
        int margin = 150 * depthLeft;

        if (!isRoot && eval >= beta + margin && !position.inCheck && !isPV && beta > -MATED_SCORE) {
            searchContext.firstNonMove = firstMove;
            return eval;
        }

        // ===============Null Move Pruning===============
        //if (!isRoot && depthLeft >= 3 && eval >= beta && !position.inCheck && numMoves >= 7) {
        if (!isRoot && depthLeft >= 3 && eval >= beta && nmpConditionsMet(position)) {
            int reduction = NULL_MOVE_PRUNING_REDUCTION;
            position.makeNullMove();
            int score;
            try {
                score = -negamax(-beta, -beta + 1, depthLeft - reduction, position, searchContext, sharedTables, false, ply + 1, false);
            } finally {
                position.unmakeNullMove();
            }
            // If a null move failed high over beta, then certainly the best move would as well, so we prune
            if (score >= beta) {
                searchContext.firstNonMove = firstMove;
                return score;
            }
        }

        // Check if we can use futility pruning (low depth, not pv, not in check, not looking for mate, quiet move unlikely to increase alpha, more than one legal move)
        boolean useFutilityPruning = false;
        if (depthLeft <= 3 && !isPV && !position.inCheck && Math.abs(alpha) < MATED_SCORE && eval + futilityMargin[depthLeft] <= alpha) {
            useFutilityPruning = true;
        }

        // Move order
        MoveOrder.scoreMoves(position, searchContext, sharedTables, firstMove, firstNonMove, ply);
        int bestScore = Integer.MIN_VALUE;
        searchContext.bestMoves[ply] = 0;

        // Search loop
        for (int i = firstMove; i < firstNonMove; i++) {
            if (Thread.currentThread().isInterrupted()) {
                System.out.println("interrupted in negamax!");
                searchContext.firstNonMove = firstMove;
                throw new InterruptedException("Negamax was interrupted by iterative deepening");
            }

            // Selection sort and get move
            MoveOrder.bestMoveFirst(searchContext, i, firstNonMove);

            int move = searchContext.moveBuffer[i];

            // Store if we were in check to see if we can reduce later
            boolean wasInCheck = position.inCheck;

            // "open" the position
            position.makeMove(move);
            sharedTables.threeFoldTable.addPosition(position.zobristHash, move);


            if (useFutilityPruning && // Set to use fp
                    i != firstMove && // Not the pv move
                    !MoveEncoding.getIsCapture(move) && // not a capture (possible to increase alpha)
                    !MoveEncoding.getIsPromotion(move) && // not a promotion
                    !position.inCheck) { // not a check
                // Close the position
                sharedTables.threeFoldTable.popPosition();
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
                    score = -negamax(-beta, -alpha, depthLeft - 1, position, searchContext, sharedTables, false, ply + 1, isPV);
                } else {
                    // ===============Late Move Reduction===============
                    //  - Use late move reduction on non-pv moves with log formula
                    if (i >= firstMove + NUM_NON_PV_FULL_DEPTH_SEARCHES && depthLeft > 3) {
                        int reduction = (int) Math.round(.99 + (Math.log(depthLeft) * Math.log(i - firstMove)) / 3.14);
                        score = -negamax(-alpha - 1, -alpha, depthLeft - 1 - reduction, position, searchContext, sharedTables, false, ply + 1, false);

                        if (score >= beta) { // If search fails high, research at full depth
                            score = -negamax(-alpha - 1, -alpha, depthLeft - 1, position, searchContext, sharedTables, false, ply + 1, false);
                        }
                    } else {
                        score = -negamax(-alpha - 1, -alpha, depthLeft - 1, position, searchContext, sharedTables, false, ply + 1, false);
                    }

                    // If disproven, and not already null window, run full search
                    if (score > alpha && (beta - alpha) > 1) {
                        score = -negamax(-beta, -alpha, depthLeft - 1, position, searchContext, sharedTables, false, ply + 1, isPV); // It is now PV if parent is because it exceeded alpha
                    }
                }
            } finally {
                // Close the position
                sharedTables.threeFoldTable.popPosition();
                position.unMakeMove(move);
            }

            // Update best move when score is better
            if (score > bestScore) {
                bestScore = score;
                searchContext.bestMoves[ply] = move;
            }

            // If this is a new lower bound
            if (score > alpha) {
                // Update alpha
                alpha = score;

                searchContext.pvTable.writePVMove(move, ply);

                // Prune when alpha >= beta because the opponent wouldn't make a move that gets here
                if (alpha >= beta) {
                    // If not a capture, add to history heuristic
                    if (!MoveEncoding.getIsCapture(move)) {
                        // Check that the killer move isn't the same
                        if (searchContext.killerMoves.killerMoves[0][ply] != move) {
                            // If not, add it. Else nothing.
                            searchContext.killerMoves.killerMoves[1][ply] = searchContext.killerMoves.killerMoves[0][ply];
                            searchContext.killerMoves.killerMoves[0][ply] = move;
                        }

                        searchContext.historyHeuristic.addMove(position.activePlayer, move, depthLeft);

                        // For each other quiet searched, penalize
                        for (int j = firstMove; j < i; j++) {
                            if (!MoveEncoding.getIsCapture(searchContext.moveBuffer[j]))
                                searchContext.historyHeuristic.penalizeMove(position.activePlayer, searchContext.moveBuffer[j], depthLeft);
                        }
                    }

                    break;
                }
            }
        }

        // After search, reset first nonMove
        searchContext.firstNonMove = firstMove;

        if (sharedTables.tt != null && searchContext.bestMoves[ply] != 0) {
            NodeType nodeType;

            // Adjust for whatever ply we're on (if mate)
            int ttValue = scoreToTT(bestScore, ply);

            if (bestScore <= alphaOrig) { // Failed to find a better move (than is known to exist with alpha), so the value is AT MAXIMUM score
                nodeType = NodeType.UPPER_BOUND;
            } else if (bestScore >= beta) { // The value is greater than beta, so an opponent would PRUNE it, but the value is at LEAST score
                nodeType = NodeType.LOWER_BOUND;
            } else {
                nodeType = NodeType.EXACT;
            }

            sharedTables.tt.addElement(position.zobristHash, searchContext.bestMoves[ply], depthLeft, ttValue, nodeType);
        }

        return bestScore;
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
        if (moveListSize == 0 || position.halfMoveCount >= 50 || threeFoldTable.positionDrawn(position.zobristHash)) {
            return true;
        }
        return false;
    }

    public static boolean moveBasedGameOver(int moveListSize) {
        return moveListSize == 0;
    }

    public static boolean stateBasedDraw(Position position, ThreeFoldTable threeFoldTable) {
        if (position.halfMoveCount >= 50 || threeFoldTable.positionDrawn(position.zobristHash)) {
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
