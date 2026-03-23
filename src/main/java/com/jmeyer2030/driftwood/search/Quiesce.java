package com.jmeyer2030.driftwood.search;

import com.jmeyer2030.driftwood.board.MoveEncoding;
import com.jmeyer2030.driftwood.board.Position;
import com.jmeyer2030.driftwood.board.SharedTables;
import com.jmeyer2030.driftwood.movegeneration.MoveGenerator;

import static com.jmeyer2030.driftwood.search.Search.NEG_INFINITY;

public class Quiesce {

    private static final int BIG_DELTA = 975;

    /**
     * Performs a search of capture moves only to reduce the horizon effect
     *
     * @param alpha         max score guaranteed for the position's active player
     * @param beta          max score guaranteed for the position's active player
     * @param position      position to search
     * @param searchContext per-search scratch space (move buffer, scores)
     * @param sharedTables  shared tables (threefold)
     * @param ply           count
     * @return score
     */
    public static int quiescenceSearch(int alpha, int beta, Position position, SearchContext searchContext, SharedTables sharedTables, int ply) {
        int standPat = position.evaluator.computeOutput(position.activePlayer);

        int bestValue = standPat;

        // TODO: Consider not in check
        if (standPat >= beta)
            return bestValue;

        // TODO: Consider not in check
        if (alpha < standPat)
            alpha = standPat;

        // TODO: Consider Delta Pruning here

        // ===============Generate moves===============
        //  - We generate only captures if not in check
        // TODO: Consider using QSearch move generator: "int newFirstNonMove = MoveGenerator.generateQSearchMoves(position, searchContext.moveBuffer, searchContext.firstNonMove);"
        int initialFirstNonMove = searchContext.firstNonMove;
        int newFirstNonMove = MoveGenerator.generateAllMoves(position, searchContext.moveBuffer, searchContext.firstNonMove);
        searchContext.firstNonMove = newFirstNonMove;

        // ===============See if Mated===============
        //  - probably don't care about stalemates because these should be seen by main search.
        int numMoves = newFirstNonMove - initialFirstNonMove;
        if (numMoves == 0 && position.inCheck) {
            return NEG_INFINITY;
        }

        // ===============Move order===============
        // TODO: Consider QSearch move generator e.g:

        /*
         *if (position.inCheck) {
         *    MoveOrder.scoreMoves(position, searchContext, sharedTables, initialFirstNonMove, newFirstNonMove, ply);
         *} else {
         *    MoveOrder.scoreLoudMoves(position, searchContext, initialFirstNonMove, newFirstNonMove);
         * }
         */
        MoveOrder.scoreMoves(position, searchContext, sharedTables, initialFirstNonMove, newFirstNonMove, ply);


        for (int i = initialFirstNonMove; i < newFirstNonMove; i++) {
            MoveOrder.bestMoveFirst(searchContext, i, newFirstNonMove);
            int move = searchContext.moveBuffer[i];
            // Skip non-captures
            // TODO: Consider non-checks
            if (!MoveEncoding.getIsCapture(move)) {
                continue;
            }

            // =============== SKIP LOSING CAPTURES ===============
            //  - Skip all captures that aren't good
            //  - Don't skip if in check to avoid skipping forced captures
            /*
            if (!position.inCheck && searchContext.moveScores[i] - MoveOrder.CAPTURE_BONUS + 200 < 0) {
                continue;
            }
            */

            // "open" the position
            position.makeMove(move);
            sharedTables.threeFoldTable.addPosition(position.zobristHash, move);

            // compute the score
            int score;
            try {
                score = -quiescenceSearch(-beta, -alpha, position, searchContext, sharedTables, ply + 1);
            } finally { // "close" the position
                sharedTables.threeFoldTable.popPosition();
                position.unMakeMove(move);
            }

            bestValue = Math.max(bestValue, score);

            alpha = Math.max(alpha, score);

            if (alpha >= beta) {
                searchContext.firstNonMove = initialFirstNonMove;
                return bestValue;
            }
        }

        searchContext.firstNonMove = initialFirstNonMove;
        return bestValue;
    }
}
