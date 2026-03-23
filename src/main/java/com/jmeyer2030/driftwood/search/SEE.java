package com.jmeyer2030.driftwood.search;

import com.jmeyer2030.driftwood.board.MoveEncoding;
import com.jmeyer2030.driftwood.board.Position;
import com.jmeyer2030.driftwood.movegeneration.BishopLogic;
import com.jmeyer2030.driftwood.movegeneration.MoveGenerator;
import com.jmeyer2030.driftwood.movegeneration.RookLogic;

/**
* Static Exchange Evaluation:
* Evaluates the material change after a sequence of captures on a given square.
*
* <p>Instance-based so that the mutable scratch arrays ({@code gain}, {@code lvpBitboard},
* {@code lvpPieceType}) are not shared across threads. One instance lives in
* {@link SearchContext}.</p>
*/
public class SEE {

    public static final int[] value = {100, 325, 325, 500, 1000, Integer.MAX_VALUE - 100_000};

    // Pre-initialized gain array (perhaps saves some time allocating memory)
    private final int[] gain = new int[32];

    // Reusable return values for getLeastValuablePiece (avoids allocation)
    private long lvpBitboard;
    private int lvpPieceType;

    /**
    * Returns the static exchange evaluation of a move on a position
    */
    public int see(int move, Position position) {
        int attackPiece = MoveEncoding.getMovedPiece(move);
        int capturedPiece = MoveEncoding.getCapturedPiece(move);
        int start = MoveEncoding.getStart(move);
        int destination = MoveEncoding.getDestination(move);

        // attacker to evaluate bitboard
        long fromSet = 1L << start;

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

            // Get least valuable bitboard mask and pieceType (results stored in static fields)
            getLeastValuablePiece(attacksAndDefends, (position.activePlayer + depth) % 2, position);
            fromSet = lvpBitboard;
            attackPiece = lvpPieceType;

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
    * Sets lvpBitboard and lvpPieceType to the least valuable attacker/defender.
    * lvpBitboard is 0 when no piece is found.
    */
    private void getLeastValuablePiece(long attacksAndDefends, int activePlayer, Position position) {
        long colorAndAttacksAndDefends = position.pieceColors[activePlayer] & attacksAndDefends;

        // For each pieceType (pawn -> knight -> bishop -> rook -> queen -> king)
        for (int i = 0; i < 6; i++) {
            long attacksOfPiece = colorAndAttacksAndDefends & position.pieces[i];
            if (attacksOfPiece != 0) {
                // Isolate the least significant bit
                lvpBitboard = attacksOfPiece & -attacksOfPiece;
                lvpPieceType = i;
                return;
            }
        }

        lvpBitboard = 0;
        lvpPieceType = 0;
    }

}

