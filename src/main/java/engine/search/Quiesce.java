package engine.search;

import board.MoveEncoding;
import board.Position;
import board.PositionState;
import moveGeneration.MoveGenerator;

import static engine.search.Search.NEG_INFINITY;

public class Quiesce {

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

        if (alpha < standPat /*&& !position.inCheck*/) // and not in check, we shouldn't use standpat at all if in check
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


            // if not a capture and not in check, skip. In check, we search all evasions
            if (!MoveEncoding.getIsCapture(move)/* && !position.inCheck*/) {
                continue;
            }

            // If SEE evaluation is negative and not in check
            /*
            if (!position.inCheck && positionState.moveScores[i] - 50_000 + 200 < 0) {
                continue;
            }
            */
            /*
            if (!position.inCheck && SEE.see(move, position) + 200 < 0) {
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
