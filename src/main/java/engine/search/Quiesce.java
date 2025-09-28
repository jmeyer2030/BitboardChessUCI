package engine.search;

import board.MoveEncoding;
import board.Position;
import board.PositionState;
import moveGeneration.MoveGenerator;

import static engine.search.Search.NEG_INFINITY;

public class Quiesce {

    private static final int BIG_DELTA = 975;

    /**
     * Performs a search of capture moves only to reduce the horizon effect
     *
     * @param alpha         max score guaranteed for the position's active player
     * @param beta          max score guaranteed for the position's active player
     * @param position      position to search
     * @param positionState information about the search/state
     * @param ply           count
     * @return score
     */
    public static int quiescenceSearch(int alpha, int beta, Position position, PositionState positionState, int ply) {
        int standPat = position.nnue.computeOutput(position.activePlayer);

        int bestValue = standPat;

        if (standPat >= beta/* && !position.inCheck*/) // And not in check
            return bestValue;


        // ===============DELTA PRUNING===============
        // NOTES:
        if (bestValue < alpha - BIG_DELTA) {
            return alpha;
        }

        if (alpha < standPat && !position.inCheck) // and not in check, we shouldn't use standpat at all if in check
            alpha = standPat;


        // Generate moves
        int initialFirstNonMove = positionState.firstNonMove;
        int newFirstNonMove = MoveGenerator.generateAllMoves(position, positionState.moveBuffer, positionState.firstNonMove);
        positionState.firstNonMove = newFirstNonMove;

        // look for checkmate, probably don't care about stalemates because these should be seen by main search.
        int numMoves = newFirstNonMove - initialFirstNonMove;
        if (numMoves == 0 && position.inCheck) {
            return NEG_INFINITY;
        }

        // Move order
        MoveOrder.scoreMoves(position, positionState, initialFirstNonMove, newFirstNonMove, ply);

        for (int i = initialFirstNonMove; i < newFirstNonMove; i++) {
            MoveOrder.bestMoveFirst(positionState, i, newFirstNonMove);
            int move = positionState.moveBuffer[i];


            // =============== SKIP NON-CAPTURES ===============
            // NOTES:
            //  - Consider evaluating checks as well
            if (!MoveEncoding.getIsCapture(move) && !position.inCheck) {
                continue;
            }

            // =============== SKIP LOSING CAPTURES ===============
            //  - Skip all captures that aren't good
            //  - Don't skip if in check to avoid skipping forced captures
            if (!position.inCheck && (positionState.moveScores[i] - MoveOrder.CAPTURE_BONUS) < 0) {
                continue;
            }

            // "open" the position
            position.makeMove(move);
            positionState.threeFoldTable.addPosition(position.zobristHash, move);

            // compute the score
            int score;
            try {
                score = -quiescenceSearch(-beta, -alpha, position, positionState, ply + 1);
            } finally { // "close" the position
                positionState.threeFoldTable.popPosition();
                position.unMakeMove(move);
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
}
