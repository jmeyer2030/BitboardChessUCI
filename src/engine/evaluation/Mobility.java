package engine.evaluation;

import board.Position;
import moveGeneration.MoveGenerator;
import moveGeneration.BishopLogic;
import moveGeneration.RookLogic;

public class Mobility {
    static int mgBishopMultiplier = 3;
    static int mgRookMultiplier = 2;
    static int mgQueenMultiplier = 1;

    static int egBishopMultiplier = 3;
    static int egRookMultiplier = 3;
    static int egQueenMultiplier = 3;

    public static int mobility(Position position, int mgPhase, int egPhase) {
        int square;
        int mgScore = 0;
        int egScore = 0;

        long whiteBishops = position.pieces[2] & position.pieceColors[0];
        while (whiteBishops != 0) {
            square = Long.numberOfLeadingZeros(whiteBishops);
            whiteBishops &= whiteBishops - 1;
            mgScore += numBishopAttacks(position, square) * mgBishopMultiplier;
            egScore += numBishopAttacks(position, square) * egBishopMultiplier;
        }

        long blackBishops = position.pieces[2] & position.pieceColors[1];
        while (blackBishops != 0) {
            square = Long.numberOfLeadingZeros(blackBishops);
            blackBishops &= blackBishops - 1;
            mgScore -= numBishopAttacks(position, square) * mgBishopMultiplier;
            egScore -= numBishopAttacks(position, square) * egBishopMultiplier;
        }

        long whiteRooks = position.pieces[3] & position.pieceColors[0];
        while (whiteRooks != 0) {
            square = Long.numberOfLeadingZeros(whiteRooks);
            whiteRooks &= whiteRooks - 1;
            mgScore += numRookAttacks(position, square) * mgRookMultiplier;
            egScore += numRookAttacks(position, square) * egRookMultiplier;
        }

        long blackRooks = position.pieces[3] & position.pieceColors[1];
        while (blackRooks != 0) {
            square = Long.numberOfLeadingZeros(blackRooks);
            blackRooks &= blackRooks - 1;
            mgScore -= numRookAttacks(position, square) * mgRookMultiplier;
            egScore -= numRookAttacks(position, square) * egRookMultiplier;
        }

        long whiteQueens = position.pieces[4] & position.pieceColors[0];
        while (whiteQueens != 0) {
            square = Long.numberOfLeadingZeros(whiteQueens);
            whiteQueens &= whiteQueens - 1;
            mgScore += numQueenAttacks(position, square) * mgQueenMultiplier;
            egScore += numQueenAttacks(position, square) * egQueenMultiplier;
        }

        long blackQueens = position.pieces[4] & position.pieceColors[1];
        while (blackQueens != 0) {
            square = Long.numberOfLeadingZeros(blackQueens);
            blackQueens &= blackQueens - 1;
            mgScore -= numQueenAttacks(position, square) * mgQueenMultiplier;
            egScore -= numQueenAttacks(position, square) * egQueenMultiplier;
        }

        return (mgScore * mgPhase + egScore * egPhase) / 24;
    }

    public static int numBishopAttacks(Position position, int square) {
        return Long.bitCount(BishopLogic.getAttackBoard(square, position));
    }

    public static int numRookAttacks(Position position, int square) {
        return Long.bitCount(RookLogic.getAttackBoard(square, position));
    }

    public static int numQueenAttacks(Position position, int square) {
        return numRookAttacks(position, square) + numBishopAttacks(position, square);
    }


}
