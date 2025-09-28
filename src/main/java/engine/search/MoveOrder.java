package engine.search;

import board.MoveEncoding;
import board.Position;
import board.PositionState;

public class MoveOrder {

    // Rough piece values used for MVV-LVA
    public static final int[] pieceValues = new int[]{100, 300, 330, 500, 900, 1_000};
    public static final int PV_BONUS = 2_000_000;
    public static final int TT_BONUS = 1_000_000;
    public static final int PROMOTION_BONUS = 500_000;
    public static final int CAPTURE_BONUS = 50_000;
    public static final int FIRST_KILLER_BONUS = 30_000;
    public static final int SECOND_KILLER_BONUS = 29_000;


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
     * - Captures: If (MVV-LVA > 0 use that, else SEE)
     *   - Winning captures
     *   - Neutral captures
     *   - Losing captures
     * - KILLER MOVES
     * - Quiet moves (History Heuristic)
     *
     * @param position      used just for the hash
     * @param positionState used for the tt/pv/History/killers
     * @param move          move to evaluate
     * @param ply           current ply in the search
     * @return score of the move
     */
    private static int scoreMove(Position position, PositionState positionState, int move, int ply) {
        int value = 0;

        if (ply == 0) { // TODO: This is not needed because we have tt?
            // get pv from triangular pv table
            int pvMove = positionState.pvTable.getPVMove();

            if (pvMove == move) {
                value += PV_BONUS;
            }
        }

        if (positionState.tt.checkedGetBestMove(position.zobristHash) == move) {
            value += TT_BONUS;
        }

        if (MoveEncoding.getIsPromotion(move)) {
            value += PROMOTION_BONUS;
        }

        if (MoveEncoding.getIsCapture(move)) {
            value += CAPTURE_BONUS + evaluateExchange(move, position);
        } else {
            if (move == positionState.killerMoves.killerMoves[0][ply]) {
                value += FIRST_KILLER_BONUS;
            } else if (move == positionState.killerMoves.killerMoves[1][ply]) {
                value += SECOND_KILLER_BONUS;
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
