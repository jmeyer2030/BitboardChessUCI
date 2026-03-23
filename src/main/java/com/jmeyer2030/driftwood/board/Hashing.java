package com.jmeyer2030.driftwood.board;
/*
* Generate an array of pseudorandom numbers
* - one for each piece at each square (12 * 64)
* - one number for side to move (1)
* - four numbers for castling rights (4)
* - eight numbers for the file of a valid en passant square (8)
* for a total of 781 numbers
*
* Pseudorandom is useful for reproducibility, and precomputation of opening books
*
* the Zobrist hash code for a position is the xor of all random numbers linked
*    to a given feature
*
* Since xor is its own inverse, incremental update can be done quickly in make/unmake
*
* It is standard to use 64 bit pseudorandom numbers
*
* Overlap is possible, but very rare and can be ignored at negligible cost
*/

import java.util.Random;

public class Hashing {
    public static final long RANDOM_SEED = 24;

    // Characteristic encoding longs
    public static final long PIECE_SQUARE[][][] = new long[64][][]; // use with: pieceSquare[square][color.ordinal][piece.ordinal]
    public static final long[] CASTLE_RIGHTS = new long[16];
    public static final long[] EN_PASSANT = new long[8];
    public static final long[] SIDE_TO_MOVE = new long[2];

    static {
        initializeRandomNumbers();
    }

    /**
    * Generates the pseudorandom numbers for main.java.zobrist hashing and initializes
    * the arrays of randoms
    */
    private static void initializeRandomNumbers() {
        Random random = new Random(RANDOM_SEED);

        // Init piece color square randoms
        for (int i = 0; i < 64; i++) {
            PIECE_SQUARE[i] = new long[2][]; // Init each color (for that square)
            for (int j = 0; j < 2; j++) {
                PIECE_SQUARE[i][j] = new long[6]; // Init each piece (for that square and color)
                for (int k = 0; k < 6; k++) {
                    PIECE_SQUARE[i][j][k] = random.nextLong();
                }
            }
        }

        // Init castleRights randoms
        for (int i = 0; i < 16; i++) {
            CASTLE_RIGHTS[i] = random.nextLong();
        }

        // Init enPassant
        for (int i = 0; i < 8; i++) {
            EN_PASSANT[i] = random.nextLong();
        }

        // Init sideToMove
        SIDE_TO_MOVE[0] = 0L;
        SIDE_TO_MOVE[1] = random.nextLong();
    }

    /**
    * Computes the main.java.zobrist hash for a position
    * @param position position
    * @return long main.java.zobrist hash
    */
    public static long computeZobrist(Position position) {
        assert PIECE_SQUARE != null;

        long zobrist = 0;

        // Pieces
        for (int color = 0; color < 2; color++) { // Iterate over color
            for (int piece = 0; piece < 6; piece++) { // Iterate over piece type
                long pieceColor = position.pieceColors[color] & position.pieces[piece];
                while (pieceColor != 0) {
                    int square = Long.numberOfTrailingZeros(pieceColor);
                    pieceColor &= (pieceColor - 1);

                    zobrist ^= PIECE_SQUARE[square][color][piece];
                }
            }
        }

        zobrist ^= CASTLE_RIGHTS[position.castleRights];

        // Enpassant
        if (position.enPassant != 0) {
            int file = position.enPassant % 8;
            zobrist ^= EN_PASSANT[file]; // file of the enPassant square
        }

        // SideToMove
        zobrist ^= SIDE_TO_MOVE[position.activePlayer];

        return zobrist;
    }
}
