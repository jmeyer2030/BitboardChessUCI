package engine.search;

import board.FEN;
import board.MoveEncoding;
import board.Position;
import moveGeneration.BishopLogic;
import moveGeneration.MoveGenerator;
import moveGeneration.RookLogic;

/**
* Static Exchange Evaluation:
* Evaluates the material change after a sequence of captures on a given square
*
*/
public class SEE {

    public static void main(String[] args) {
        //FEN fen = new FEN("1k1r4/1pp4p/p7/4p3/8/P5P1/1PP4P/2K1R3 w - - 0 1");
        //FEN fen = new FEN("44k3/8/8/4Q3/8/3n2B1/6K1/8 b - - 0 1"); // nxQ, Bxn
        //FEN fen = new FEN("4k3/8/8/4Q3/8/3n4/6K1/8 b - - 0 1"); // nxQ
        FEN fen = new FEN("1k1r3q/1ppn3p/p4b2/4p3/8/P2N2P1/1PP1R1BP/2K1Q3 w - - 0 1");
        //FEN fen = new FEN("4k3/8/8/4Q3/5K2/3n4/8/8 b - - 0 1"); // nxQ, Kxn
        Position position = new Position(fen);

        int[] moveBuffer = new int[256];

        int move = MoveGenerator.getMoveFromLAN("d3e5", position, moveBuffer);
        int result = see(move, position);
        System.out.println(result);
    }


    public record pieceTypeAndBitboard(long bitboard, int pieceType) {};

    public static final int[] value = {100, 325, 325, 500, 1000, Integer.MAX_VALUE - 100_000};

    // Pre-initialized gain array (perhaps saves some time allocating memory)
    public static final int[] gain = new int[32];

    /**
    * Returns the static exchange evaluation of a move on a position
    */
    public static int see(int move, Position position) {
        int attackPiece = MoveEncoding.getMovedPiece(move);
        int capturedPiece = MoveEncoding.getCapturedPiece(move);
        int start = MoveEncoding.getStart(move);
        int destination = MoveEncoding.getDestination(move);

        // attacker to evaluate bitboard
        long fromSet = 1 << start;

        int depth = 0;

        // Pieces that can be blocking a sliding attack
        long mayXRay = position.pieces[0] | position.pieces[2] | position.pieces[3] | position.pieces[4];

        long occupancy = position.occupancy;
        long attacksAndDefends = MoveGenerator.getSEEAttackers(position, destination);

        gain[depth] = value[capturedPiece];

        do {
            depth++; // Next depth and side
            gain[depth] = value[attackPiece] - gain[depth - 1]; // Speculative score if defended
            attacksAndDefends ^= fromSet; // Reset bit in set in attacks/defends
            occupancy ^= fromSet; // Reset bit in temporary occupancy


            // If that piece could be blocking a sliding attack
            if ((fromSet & mayXRay) != 0) {
                attacksAndDefends |= considerXRays(occupancy, destination, position);
            }

            // Get least valuable bitboard mask and pieceType
            pieceTypeAndBitboard leastValuable = getLeastValuablePiece(attacksAndDefends, (position.activePlayer + depth) % 2, position);
            fromSet = leastValuable.bitboard;
            attackPiece = leastValuable.pieceType;

        } while (fromSet != 0);


        while (--depth > 0) {
            gain[depth - 1] = -Math.max(-gain[depth - 1], gain[depth]);
        }

        return gain[0];
    }


    /**
    * considers a removed piece, checks if there was something x-raying it, and returns a bitboard representing the location of that piece
    *
    */
    public static long considerXRays(long occupancy, int square, Position position) {
        long rookMatches = RookLogic.getAttackBoard(square, occupancy) & (position.pieces[3] | position.pieces[4]);
        long bishopMatches = BishopLogic.getAttackBoard(square, occupancy) & (position.pieces[2] | position.pieces[4]);

        return (rookMatches | bishopMatches) & occupancy;
    }

    /**
    * Returns a record containing a bitmask for the least valuable piece, and it's index in the value array
    *
    */
    public static pieceTypeAndBitboard getLeastValuablePiece(long attacksAndDefends, int activePlayer, Position position) {
        long colorAndAttacksAndDefends = position.pieceColors[activePlayer] & attacksAndDefends;

        // For each pieceType (pawn -> knight -> bishop -> rook -> queen -> king)
        for (int i = 0; i < 6; i++) {
            long attacksOfPiece = colorAndAttacksAndDefends & position.pieces[i];
            if (attacksOfPiece != 0) {
                // Returns the least significant bit
                return new pieceTypeAndBitboard(attacksOfPiece & -attacksOfPiece, i);
            }
        }

        return new pieceTypeAndBitboard(0, 0);
    }

}

