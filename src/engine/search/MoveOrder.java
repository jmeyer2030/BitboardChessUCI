package engine.search;

import board.MoveEncoding;
import board.Position;

public class MoveOrder {

    // Rough piece values used for MVV-LVA
    private static final int[] pieceValues = new int[]{100, 300, 330, 500, 900, 1_000};

    /**
     * Assigns scores to moves in the corresponding searchState.moveScores
     *
     * @param searchState  source of move tables
     * @param firstMove    the start of the move-window in the table
     * @param firstNonMove the first non-move after the window
     */
    public static void scoreMoves(Position position, SearchState searchState, int firstMove, int firstNonMove) {
        // Iterate over moves in the window
        for (int i = firstMove; i < firstNonMove; i++) {
            searchState.moveScores[i] = scoreMove(position, searchState, searchState.moveBuffer[i]);
        }
    }

    /**
     * Performs one iteration of a selection sort by:
     * - Finding the move with the highest value within the window
     * - Swapping its location with the first element in the window
     *
     * @param searchState  used to access moveBuffer and moveScores
     * @param firstMove    the first move to start our search
     * @param firstNonMove the element after the last move to search
     */
    public static void bestMoveFirst(SearchState searchState, int firstMove, int firstNonMove) {
        // Initialize to first move, so that we are safe if firstMove == firstNonMove
        int bestIndex = firstMove;
        int bestScore = searchState.moveScores[firstMove];

        // Find the best scoring move and its index
        for (int i = firstMove; i < firstNonMove; i++) {
            int currentMoveScore = searchState.moveScores[i];

            if (currentMoveScore > bestScore) {
                bestScore = currentMoveScore;
                bestIndex = i;
            }
        }

        // Swap the first move/score with the best move/score
        int tempBestMove = searchState.moveBuffer[bestIndex];
        searchState.moveBuffer[bestIndex] = searchState.moveBuffer[firstMove];
        searchState.moveScores[bestIndex] = searchState.moveScores[firstMove];

        searchState.moveBuffer[firstMove] = tempBestMove;
        searchState.moveScores[firstMove] = bestScore;
    }

    /**
     * Returns the score of a move. Prioritizing in order:
     * - PV / hash move
     * - Promotions
     * - Winning captures
     * - Neutral captures
     * - @Todo: KILLER MOVES
     * - Quiet moves
     * - Losing captures
     *
     * @param position    used just for the hash
     * @param searchState used for the tt
     * @param move        move to evaluate
     * @return score of the move
     */
    private static int scoreMove(Position position, SearchState searchState, int move) {
        int value = 0;

        if (searchState.tt.checkedGetBestMove(position.zobristHash) != 0) {
            value += 1_000_000;
        }

        if (MoveEncoding.getIsPromotion(move)) {
            value += 500_000;
        }

        if (MoveEncoding.getIsCapture(move)) {
            // Shows preference to equal captures over quiet moves
            value += 50 + evaluateExchange(move);
        }

        return value;
    }

    /**
     * Returns the net value of a capture if the capturer is then taken
     *
     * @param move
     * @return exchange evaluation
     */
    private static int evaluateExchange(int move) {
        return pieceValues[MoveEncoding.getCapturedPiece(move)] - pieceValues[MoveEncoding.getMovedPiece(move)];
    }


}
