package engine.evaluation;

import board.Position;

public class MopUp {

    /**
     * Use mop up if:
     * - No pawns
     * - Only one player has pieces
     * - Sufficient material exists to mate
     *
     * Use AFTER sufficient material check in StaticEvaluation to assure that sufficient material exists
     *
     * @param position for piece numbers
     * @return true if we should use mopUpEvaluation
     */
    public static boolean shouldUse(Position position) {
        int numWhitePawns = position.pieceCounts[0][0];
        int numWhiteMinorMajor = position.pieceCounts[0][1] + position.pieceCounts[0][2] + position.pieceCounts[0][3] + position.pieceCounts[0][4];
        int numBlackPawns = position.pieceCounts[1][0];
        int numBlackMinorMajor = position.pieceCounts[1][1] + position.pieceCounts[1][2] + position.pieceCounts[1][3] + position.pieceCounts[1][4];

        if (numWhitePawns + numBlackPawns == 0 && // If no pawns and
                (numWhiteMinorMajor == 0 || numBlackMinorMajor == 0)) { // Either white or black has no minors or majors
            return true;
        }

        return false;
    }

    /**
     * Returns a side agnostic evaluation (positive for white favor, negative for black favor)
     *
     * Since a winning position, this evaluation should have a very high evaluation, and be more high/low
     * the closer we get to a mate
     *
     *
     *
     */
    public static int eval(Position position) {

        // Get ranks and files of kings
        int wKingRank = position.kingLocs[0] / 8;
        int wKingFile = position.kingLocs[0] % 8;
        int bKingRank = position.kingLocs[1] / 8;
        int bKingFile = position.kingLocs[1] % 8;

        // Get king distance
        int kingDistance = Math.max(Math.abs(wKingRank - bKingRank), Math.abs(wKingFile - bKingFile));

        // Winning player is the one with higher pst score (since other has no pieces)
        int winningPlayer = position.egScore > 0 ? 0 : 1;
        int sideCorrection = winningPlayer == 0 ? 1 : -1;

        // Bonus for being in a winning position
        int mateNearBonus = 10_000 * sideCorrection;

        // Punishment for king distance (IF white winning, we subtract, if black winning we add)
        int distanceMultiplier = 50;
        int distanceModifier = distanceMultiplier * kingDistance * (winningPlayer == 0 ? -1 : 1);


        // King pst eval
        int kingPSTEval = (PieceSquareTables.egKingTable[position.kingLocs[0]] - PieceSquareTables.egKingTable[position.kingLocs[1]]);

        return kingPSTEval + mateNearBonus + distanceModifier;
    }
}


/*
Evaluation should be absolute, e.g. if white is winning, very position, if black winning, very negative.

SO WRT to king distance: IF white is winning, we should subtract king distance (since they want to minimize distance). IF black is winning, we should add king distance


*/
