package com.jmeyer2030.driftwood.search;

import com.jmeyer2030.driftwood.board.Position;
import com.jmeyer2030.driftwood.board.SharedTables;

import static com.jmeyer2030.driftwood.search.Search.NEG_INFINITY;
import static com.jmeyer2030.driftwood.search.Search.MATED_VALUE;

public class Quiesce {

    /**
     * Performs a search of capture moves only to reduce the horizon effect.
     * Uses {@link QSearchMovePicker} for staged move generation:
     * <ul>
     *   <li>Not in check: TT move (if capture) → generate captures → yield captures</li>
     *   <li>In check: TT move → generate all evasions → yield evasions</li>
     * </ul>
     * This avoids per-move TT probes and enables early cutoffs before full generation.
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

        // =============== Staged move generation via QSearchMovePicker ===============
        // Probe TT once for move ordering (replaces N per-move TT probes in the old scoreMoves path)
        int ttMove = (sharedTables.tt != null) ? sharedTables.tt.checkedGetBestMove(position.zobristHash) : 0;

        QSearchMovePicker picker = searchContext.qSearchMovePickers[ply];
        picker.init(position, searchContext, ttMove, position.inCheck);

        int move;
        while ((move = picker.nextMove()) != 0) {

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
                picker.restoreBuffer();
                return bestValue;
            }
        }

        // =============== Mate detection ===============
        // If in check and no legal moves were yielded, it's checkmate
        if (position.inCheck && picker.moveCount() == 0) {
            picker.restoreBuffer();
            return -(MATED_VALUE - ply);
        }

        picker.restoreBuffer();
        return bestValue;
    }
}
