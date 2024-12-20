package Search;

import board.Move;
import board.MoveType;
import board.Position;

public class StaticEvaluation {

    public enum GameStage {
        MIDGAME,
        ENDGAME,
    }


    // Piece values (pawn, knight, bishop, rook, queen, king)
    public static final int[] mgValue = {82, 337, 365, 477, 1025, 0};
    public static final int[] egValue = {94, 281, 297, 512, 936, 0};

    /* The flip array is used to calculate the piece/square
   values for DARK pieces. The piece/square value of a
   LIGHT pawn is pawn_pcsq[sq] and the value of a DARK
   pawn is pawn_pcsq[flip[sq]] */
    public static final int[] flip = {
        56,  57,  58,  59,  60,  61,  62,  63,
        48,  49,  50,  51,  52,  53,  54,  55,
        40,  41,  42,  43,  44,  45,  46,  47,
        32,  33,  34,  35,  36,  37,  38,  39,
        24,  25,  26,  27,  28,  29,  30,  31,
        16,  17,  18,  19,  20,  21,  22,  23,
        8,   9,  10,  11,  12,  13,  14,  15,
        0,   1,   2,   3,   4,   5,   6,   7
    };

    // Tables
    public static final int[] mgPawnTable = {
  0, 0, 0, 0, 0, 0, 0, 0, -22, 38, 24, -15, -23, -20, -1, -35, -12, 33, 3, 3, -10, -4, -4, -26, -25, 10, 6, 17, 12, -5, -2, -27, -23, 17, 12, 23, 21, 6, 13, -14, -20, 25, 56, 65, 31, 26, 7, -6, -11, 34, 126, 68, 95, 61, 134, 98, 0, 0, 0, 0, 0, 0, 0,0
    };

    public static final int[] egPawnTable = {
            0, 0, 0, 0, 0, 0, 0, 0, -7, 2, 0, 13, 10, 8, 8, 13, -8, -1, -5, 0, 1, -6, 7, 4, -1, 3, -8, -7, -7, -3, 9, 13, 17, 17, 4, -2, 5, 13, 24, 32, 84, 82, 53, 56, 67, 85, 100, 94, 187, 165, 132, 147, 134, 158, 173, 178, 0, 0, 0, 0, 0, 0, 0,0
    };

    public static final int[] mgKnightTable = {
            -23, -19, -28, -17, -33, -58, -21, -105, -19, -14, 18, -1, -3, -12, -53, -29, -16, 25, 17, 19, 10, 12, -9, -23, -8, 21, 19, 28, 13, 16, 4, -13, 22, 18, 69, 37, 53, 19, 17, -9, 44, 73, 129, 84, 65, 37, 60, -47, -17, 7, 62, 23, 36, 72, -41, -73, -107, -15, -97, 61, -49, -34, -89,-167
    };

    public static final int[] egKnightTable = {
            -64, -50, -18, -22, -15, -23, -51, -29, -44, -23, -20, -2, -5, -10, -20, -42, -22, -20, -3, 10, 15, -1, -3, -23, -18, 4, 17, 16, 25, 16, -6, -18, -18, 8, 11, 22, 22, 22, 3, -17, -41, -19, -9, -1, 9, 10, -20, -24, -52, -24, -25, -9, -2, -25, -8, -25, -99, -63, -27, -31, -28, -13, -38,-58

    };

    public static final int[] mgBishopTable = {
            -21, -39, -12, -13, -21, -14, -3, -33, 1, 33, 21, 7, 0, 16, 15, 4, 10, 18, 27, 14, 15, 15, 15, 0, 4, 10, 12, 34, 26, 13, 13, -6, -2, 7, 37, 37, 50, 19, 5, -4, -2, 37, 50, 35, 40, 43, 37, -16, -47, 18, 59, 30, -13, -18, 16, -26, -8, 7, -42, -25, -37, -82, 4,-29

    };

    public static final int[] egBishopTable = {
    -17, -5, -16, -9, -5, -23, -9, -23, -27, -15, -9, 4, -1, -7, -18, -14, -15, -7, 3, 13, 10, 8, -3, -12, -9, -3, 10, 7, 19, 13, 3, -6, 2, 3, 10, 14, 9, 12, 9, -3, 4, 0, 6, -2, -1, 0, -8, 2, -14, -4, -13, -3, -12, 7, -4, -8, -24, -17, -9, -7, -8, -11, -21,-14
    };

    public static final int[] mgRookTable = {
            -26, -37, 7, 16, 17, 1, -13, -19, -71, -6, 11, -1, -9, -20, -16, -44, -33, -5, 0, 3, -17, -16, -25, -45, -23, 6, -7, 9, -1, -12, -26, -36, -20, -8, 35, 24, 26, 7, -11, -24, 16, 61, 45, 17, 36, 26, 19, -5, 44, 26, 67, 80, 62, 58, 32, 27, 43, 31, 9, 63, 51, 32, 42,32

    };

    public static final int[] egRookTable = {
            -20, 4, -13, -5, -1, 3, 2, -9, -3, -11, -9, -9, 2, 0, -6, -6, -16, -8, -12, -7, -1, -5, 0, -4, -11, -8, -6, -5, 4, 8, 5, 3, 2, -1, 1, 2, 1, 13, 3, 4, -3, -5, -3, 4, 5, 7, 7, 7, 3, 8, 3, -3, 11, 13, 13, 11, 5, 8, 12, 12, 15, 18, 10,13

    };

    public static final int[] mgQueenTable = {
            -50, -31, -25, -15, 10, -9, -18, -1, 1, -3, 15, 8, 2, 11, -8, -35, 5, 14, 2, -5, -2, -11, 2, -14, -3, 3, -4, -2, -10, -9, -26, -9, 1, -2, 17, -1, -16, -16, -27, -27, 57, 47, 56, 29, 8, 7, -17, -13, 54, 28, 57, -16, 1, -5, -39, -24, 45, 43, 44, 59, 12, 29, 0,-28

    };

    public static final int[] egQueenTable = {
            -41, -20, -32, -5, -43, -22, -28, -33, -32, -36, -23, -16, -16, -30, -23, -22, 5, 10, 17, 9, 6, 15, -27, -16, 23, 39, 34, 31, 47, 19, 28, -18, 36, 57, 40, 57, 45, 24, 22, 3, 9, 19, 35, 47, 49, 9, 6, -20, 0, 30, 25, 58, 41, 32, 20, -17, 20, 10, 19, 27, 27, 22, 22,-9

    };

    public static final int[] mgKingTable = {
            14, 24, -28, 8, -54, 12, 36, -15, 8, 9, -16, -43, -64, -8, 7, 1, -27, -15, -30, -44, -46, -22, -14, -14, -51, -33, -44, -46, -39, -27, -1, -49, -36, -14, -25, -30, -27, -12, -20, -17, -22, 22, 6, -20, -16, 2, 24, -9, -29, -38, -4, -8, -7, -20, -1, 29, 13, 2, -34, -56, -15, 16, 23,-65
    };

    public static final int[] egKingTable = {
   -43, -24, -14, -28, -11, -21, -34, -53, -17, -5, 4, 14, 13, 4, -11, -27, -9, 7, 16, 23, 21, 11, -3, -19, -11, 9, 23, 27, 24, 21, -4, -18, 3, 26, 33, 26, 27, 24, 22, -8, 13, 44, 45, 20, 15, 23, 17, 10, 11, 23, 38, 17, 17, 14, 17, -12, -17, 4, 15, -11, -18, -18, -35,-74
    };

    public static int gameStageInc[] = {0,0,1,1,1,1,2,2,4,4,0,0};

    public static final int[][] mgTables = {mgPawnTable, mgKnightTable, mgBishopTable, mgRookTable, mgQueenTable, mgKingTable};
    public static final int[][] egTables = {egPawnTable, egKnightTable, egBishopTable, egRookTable, egQueenTable, egKingTable};

    public static final int[][][] pieceTables = {mgTables, egTables};
    public static final int[][] pieceValues = {mgValue, egValue};

    public static int evaluatePosition(Position position) {
       int mgScore = 0;
       int egScore = 0;
       int gamePhase = 0;

       for (int i = 0; i <= 1; i++) { //iterate for each color
            for (int j = 0; j < 5; j++) { //iterate for each pieceType
                long pieces = position.pieceColors[i] & position.pieces[j];
                while (pieces != 0) {
                    int square = Long.numberOfTrailingZeros(pieces);
                    gamePhase += gameStageInc[j];
                    if (i == 0) {//if white
                        mgScore += pieceValues[0][j] + pieceTables[0][j][square];
                        egScore += pieceValues[1][j] + pieceTables[1][j][square];
                    } else {
                        mgScore -= pieceValues[0][j] + pieceTables[0][j][flip[square]];
                        egScore -= pieceValues[1][j] + pieceTables[1][j][flip[square]];
                    }

                    pieces &= pieces - 1;
                }
            }
       }
       int mgPhase = gamePhase;
       int egPhase = gamePhase;
       if (mgPhase > 24) mgPhase = 24;

       return (mgScore * mgPhase + egScore * egPhase) / 24;

   }

   public static int evaluateExchange(Move move) {
        assert move.moveType == MoveType.CAPTURE;
        return pieceValues[0][move.captureType.ordinal()] - pieceValues[0][move.movePiece.ordinal()];
   }

}
