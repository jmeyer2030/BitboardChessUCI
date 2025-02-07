package zobrist;
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
* Since xor is it's own inverse, incremental update can be done quickly in make/unmake
*
* It is standard to use 64 bit pseudorandom numbers
*
* Overlap is possible, but very rare and can be ignored at negligible cost
*/

import board.Position;

import java.util.Random;

public class Hashing {
    public static long seed = 24;

    private static long pieceSquare[][][]; // use with: pieceSquare[square][color.ordinal][piece.ordinal]
    private static long[] castleRights;
    private static long[] enPassant;
    private static long[] sideToMove;

    /**
    * Generates the pseudorandom numbers for zobrist hashing and initializes
    * the arrays of randoms
    */
    public static void initializeRandomNumbers() {
        Random random = new Random(seed);

        // Init piece color square randoms
        pieceSquare = new long[64][][]; // Init each square
        for (int i = 0; i < 64; i++) {
            pieceSquare[i] = new long[2][]; // Init each color (for that square)
            for (int j = 0; j < 2; j++) {
                pieceSquare[i][j] = new long[6]; // Init each piece (for that square and color)
                for (int k = 0; k < 6; k++) {
                    pieceSquare[i][j][k] = random.nextLong();
                }
            }
        }

        // Init castleRights randoms
        castleRights = new long[16];
        for (int i = 0; i < 16; i++) {
            castleRights[i] = random.nextLong();
        }

        // Init enPassant
        enPassant = new long[8];
        for (int i = 0; i < 8; i++) {
            enPassant[i] = random.nextLong();
        }

        // Init sideToMove
        sideToMove = new long[2];
        sideToMove[0] = -1L;
        sideToMove[1] = random.nextLong();
    }

    /**
    * Computes the zobrist hash for a position
    * @param position position
    * @return long zobrist hash
    */
    public static long computeZobrist(Position position) {
        assert pieceSquare != null;

        long zobrist = 0;

        // Pieces
        for (int color = 0; color < 2; color++) { // Iterate over color
            for (int piece = 0; piece < 6; piece++) { // Iterate over piece type
                long pieceColor = position.pieceColors[color] & position.pieces[piece];
                while (pieceColor != 0) {
                    int square = Long.numberOfTrailingZeros(pieceColor);
                    pieceColor &= (pieceColor - 1);

                    zobrist ^= pieceSquare[square][color][piece];
                }
            }
        }

        zobrist ^= castleRights[position.castleRights];

        // Enpassant
        if (position.enPassant != 0) {
            int file = position.enPassant % 8;
            zobrist ^= enPassant[file]; // file of the enPassant square
        }

        // SideToMove
        zobrist ^= sideToMove[position.activePlayer];

        return zobrist;
    }
}
