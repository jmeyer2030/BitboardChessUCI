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

        // TODO: Consider not in check
        if (standPat >= beta)
            return bestValue;

        // TODO: Consider not in check
        if (alpha < standPat)
            alpha = standPat;

        // TODO: Consider Delta Pruning here

        // ===============Generate moves===============
        //  - We generate only captures if not in check
        // TODO: Consider using QSearch move generator: "int newFirstNonMove = MoveGenerator.generateQSearchMoves(position, positionState.moveBuffer, positionState.firstNonMove);"
        int initialFirstNonMove = positionState.firstNonMove;
        int newFirstNonMove = MoveGenerator.generateAllMoves(position, positionState.moveBuffer, positionState.firstNonMove);
        positionState.firstNonMove = newFirstNonMove;

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
         *    MoveOrder.scoreMoves(position, positionState, initialFirstNonMove, newFirstNonMove, ply);
         *} else {
         *    MoveOrder.scoreLoudMoves(position, positionState, initialFirstNonMove, newFirstNonMove);
         * }
         */
        MoveOrder.scoreMoves(position, positionState, initialFirstNonMove, newFirstNonMove, ply);


        for (int i = initialFirstNonMove; i < newFirstNonMove; i++) {
            MoveOrder.bestMoveFirst(positionState, i, newFirstNonMove);
            int move = positionState.moveBuffer[i];
            // Skip non-captures
            // TODO: Consider non-checks
            if (!MoveEncoding.getIsCapture(move)) {
                continue;
            }

            // =============== SKIP LOSING CAPTURES ===============
            //  - Skip all captures that aren't good
            //  - Don't skip if in check to avoid skipping forced captures
            /*
            if (!position.inCheck && positionState.moveScores[i] - MoveOrder.CAPTURE_BONUS + 200 < 0) {
                continue;
            }
            */

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
