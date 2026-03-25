package com.jmeyer2030.driftwood.search;

import com.jmeyer2030.driftwood.board.MoveEncoding;
import com.jmeyer2030.driftwood.board.Position;
import com.jmeyer2030.driftwood.board.SharedTables;
import com.jmeyer2030.driftwood.movegeneration.MoveGenerator;

import static com.jmeyer2030.driftwood.search.Search.NEG_INFINITY;
import static com.jmeyer2030.driftwood.search.Search.MATED_VALUE;

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

        // Stand-pat cutoffs only apply when NOT in check because when in check, we can't, "stand pat", we have to get out of check.
        if (!position.inCheck) {
            if (standPat >= beta)
                return bestValue;

            if (alpha < standPat)
                alpha = standPat;
        } else {
            bestValue = NEG_INFINITY;
        }

        // ===============Generate moves===============
        //  - We generate only captures if not in check
        //  - If in check, all moves are generated so we can detect mate
        int initialFirstNonMove = searchContext.firstNonMove;
        int newFirstNonMove = MoveGenerator.generateQSearchMoves(position, searchContext.moveBuffer, searchContext.firstNonMove);
        searchContext.firstNonMove = newFirstNonMove;

        // ===============See if Mated===============
        //  - probably don't care about stalemates because these should be seen by main search.
        int numMoves = newFirstNonMove - initialFirstNonMove;
        if (numMoves == 0 && position.inCheck) {
            searchContext.firstNonMove = initialFirstNonMove;
            return -(MATED_VALUE - ply);
        }

        // ===============Move order===============
        // TODO: Consider different move orderings if in check vs not in chceck
        MoveOrder.scoreMoves(position, searchContext, sharedTables, initialFirstNonMove, newFirstNonMove, ply);


        for (int i = initialFirstNonMove; i < newFirstNonMove; i++) {
            MoveOrder.bestMoveFirst(searchContext, i, newFirstNonMove);
            int move = searchContext.moveBuffer[i];
            // Skip non-captures when not in check (only captures were generated anyway).
            // When in check, all moves were generated and quiet evasions must be searched.
            if (!position.inCheck && !MoveEncoding.getIsCapture(move)) {
                continue;
            }

            // =============== SKIP LOSING CAPTURES ===============
            //  - Skip all captures that aren't good
            //  - Don't skip if in check to avoid skipping forced captures
            /*
            if (!position.inCheck && searchContext.see.see(move, position) < 0) {
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
