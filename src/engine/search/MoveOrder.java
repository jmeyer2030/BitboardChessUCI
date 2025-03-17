package engine.search;

import board.MoveEncoding;
import board.Position;
import board.PositionState;

import java.time.chrono.JapaneseChronology;

public class MoveOrder {

    // Rough piece values used for MVV-LVA
    private static final int[] pieceValues = new int[]{100, 300, 330, 500, 900, 1_000};

    /**
     * Assigns scores to moves in the corresponding positionState.moveScores
     *
     * @param positionState source of move tables
     * @param firstMove     the start of the move-window in the table
     * @param firstNonMove  the first non-move after the window
     */
    public static void scoreMoves(Position position, PositionState positionState, int firstMove, int firstNonMove, int ply) {
        // Iterate over moves in the window
        for (int i = firstMove; i < firstNonMove; i++) {
            positionState.moveScores[i] = scoreMove(position, positionState, positionState.moveBuffer[i], ply);
        }
    }

    /**
     * Performs one iteration of a selection sort by:
     * - Finding the move with the highest value within the window
     * - Swapping its location with the first element in the window
     *
     * @param positionState used to access moveBuffer and moveScores
     * @param firstMove     the first move to start our search
     * @param firstNonMove  the element after the last move to search
     */
    public static void bestMoveFirst(PositionState positionState, int firstMove, int firstNonMove) {
        // Initialize to first move, so that we are safe if firstMove == firstNonMove
        int bestIndex = firstMove;
        int bestScore = positionState.moveScores[firstMove];

        // Find the best scoring move and its index
        for (int i = firstMove; i < firstNonMove; i++) {
            int currentMoveScore = positionState.moveScores[i];

            if (currentMoveScore > bestScore) {
                bestScore = currentMoveScore;
                bestIndex = i;
            }
        }

        // Swap the first move/score with the best move/score
        int tempBestMove = positionState.moveBuffer[bestIndex];
        positionState.moveBuffer[bestIndex] = positionState.moveBuffer[firstMove];
        positionState.moveScores[bestIndex] = positionState.moveScores[firstMove];

        positionState.moveBuffer[firstMove] = tempBestMove;
        positionState.moveScores[firstMove] = bestScore;
    }

    /**
     * Returns the score of a move. Prioritizing in order:
     * - PV / hash move
     * - Promotions
     * - Winning captures
     * - Neutral captures
     * - Losing captures
     * - KILLER MOVES
     * - Quiet moves
     *
     * @param position      used just for the hash
     * @param positionState used for the tt
     * @param move          move to evaluate
     * @param ply           current ply in the search
     * @return score of the move
     */
    private static int scoreMove(Position position, PositionState positionState, int move, int ply) {
        int value = 0;

        if (positionState.tt.checkedGetBestMove(position.zobristHash) == move) {
            value += 1_000_000;
        }

        if (MoveEncoding.getIsPromotion(move)) {
            value += 500_000;
        }

        if (MoveEncoding.getIsCapture(move)) {
            //value += 50_000 + mvvlva(move);
            value += 50_000 + evaluateExchange(move, position);
            //value += 50_000 + SEE.see(move, position);
        } else {
            if (move == positionState.killerMoves.killerMoves[0][ply]) {
                value += 30_000;
            } else if (move == positionState.killerMoves.killerMoves[1][ply]) {
                value += 29_000;
            }

            value += positionState.historyHeuristic.getHeuristic(move, position.activePlayer);
        }

        return value;
    }

    /**
     * Returns the net value of a capture if the capturer is then taken
     *
     * @param move
     * @return exchange evaluation
     */
    private static int mvvlva(int move) {
        return pieceValues[MoveEncoding.getCapturedPiece(move)] - pieceValues[MoveEncoding.getMovedPiece(move)];
    }


    /**
    * For winning captures, just return MVVLVA
    * For losing captures, return SEE
    *
    * @param move to score
    * @param position the move is made on
    */
    private static int evaluateExchange(int move, Position position) {
        int mvvlvaScore = mvvlva(move);

        if (mvvlvaScore > 0) {
            return mvvlvaScore;
        } else {
            return SEE.see(move, position);
        }
    }

}
