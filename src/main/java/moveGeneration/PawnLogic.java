package main.java.moveGeneration;

import main.java.board.Position;

public class PawnLogic {
    private static final long[] whitePawnPushes = new long[64];
    private static final long[] whitePawnAttacks = new long[64];
    private static final long[] whitePawnEnPassants = new long[64];
    private static final long[] blackPawnPushes = new long[64];
    private static final long[] blackPawnAttacks = new long[64];
    private static final long[] blackPawnEnPassants = new long[64];

    private static final long[] whitePawnPushBlockerMask = new long[64];
    private static final long[] blackPawnPushBlockerMask = new long[64];

    private static final long thirdRankMask = 0b00000000_00000000_00000000_00000000_00000000_11111111_00000000_00000000L;

    private static final long sixthRankMask = 0b00000000_00000000_11111111_00000000_00000000_00000000_00000000_00000000L;

    static {
        generateWhitePawnPushes();
        generateWhitePawnAttacks();
        generateWhitePawnEnPassants();
        generateBlackPawnPushes();
        generateBlackPawnAttacks();
        generateBlackPawnEnPassants();
        generateWhitePawnPushes();
        generateBlackPawnPushes();
    }

    public static long getAttackBoard(int square, int color) {
        if (color == 0) {
            return whitePawnAttacks[square];
        }
        return blackPawnAttacks[square];
    }

    public static long getQuietMoves(int square, Position position) {
        if ((position.activePlayer == 0)) {
            return getWhitePawnPushes(square, position.occupancy);
        }
        return getBlackPawnPushes(square, position.occupancy);
    }

    public static long getCaptures(int square, Position position) {
        if ((position.pieceColors[0] & (1L << square)) != 0) {
            return whitePawnAttacks[square] & position.pieceColors[1];
        }
        return blackPawnAttacks[square] & position.pieceColors[0];
    }

    public static long getEnPassant(int square, Position position) {
        if (position.enPassant == 0 ||
                (((1L << position.enPassant) & (position.activePlayer == 0 ? whitePawnAttacks[square] :
                        blackPawnAttacks[square])) == 0))// ||//If square doesn't attack the enPassant square//(position.enPassant + 1 != square && position.enPassant - 1 != square) ||
            //(square / 8 != 3 || square / 8 != 4)) // enpassant doesn't exist or isn't next to the piece
            return 0L;
        //if (position.whiteToPlay) {//if its a white pawn to be taken
        //	return (1L << (position.enPassant));
        //}
        return (1L << (position.enPassant));
    }

    private static long getWhitePawnPushes(int square, long occupancyBoard) {
        long pushes = whitePawnPushes[square];
        if (square <= 15) { // Check if pawn is on the second rank
            if ((occupancyBoard & whitePawnPushBlockerMask[square]) != 0) {
                return 0L; // Blocker present, no pushes possible
            }
        }
        return pushes & ~occupancyBoard; // Mask with available squares
    }

    private static long getBlackPawnPushes(int square, long occupancyBoard) {
        long pushes = blackPawnPushes[square];
        if (square >= 48) { // Check if pawn is on the 6th rank
            if ((occupancyBoard & blackPawnPushBlockerMask[square]) != 0) {
                return 0L; // Blocker present, no pushes possible
            }
        }
        return pushes & ~occupancyBoard; // Mask with available squares
    }


    private static void generateWhitePawnPushes() {
        for (int i = 0; i < 64; i++) {
            long whitePawnPush = generateWhitePawnPush(i);
            whitePawnPushes[i] = whitePawnPush;

            if (i < 16) {
                whitePawnPushBlockerMask[i] = whitePawnPush & thirdRankMask;
            } else {
                whitePawnPushBlockerMask[i] = whitePawnPush;
            }
        }
    }

    private static long generateWhitePawnPush(int square) {
        long whitePawnPush = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
        if (square < 56)
            whitePawnPush |= (1L << (square + 8));
        if (square < 16 && square > 7) {
            whitePawnPush |= (1L << (square + 16));
        }
        return whitePawnPush;
    }

    private static void generateWhitePawnAttacks() {
        for (int i = 0; i < 64; i++) {
            whitePawnAttacks[i] = generateWhitePawnAttack(i);
        }
    }

    private static long generateWhitePawnAttack(int square) {
        long whitePawnAttack = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
        if (square < 56) { //below top rank
            if (square % 8 != 7) //left of right file
                whitePawnAttack |= (1L << (square + 9));
            if (square % 8 != 0) //right of left file
                whitePawnAttack |= (1L << (square + 7));
        }
        return whitePawnAttack;
    }


    private static void generateWhitePawnEnPassants() {
        for (int i = 0; i < 64; i++) {
            whitePawnEnPassants[i] = generateWhitePawnEnPassant(i);
        }
    }

    private static long generateWhitePawnEnPassant(int square) {
        if (square / 8 == 5) {
            return generateWhitePawnAttack(square);
        } else {
            return 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
        }
    }

    private static void generateBlackPawnPushes() {
        for (int i = 0; i < 64; i++) {
            long blackPawnPush = generateBlackPawnPush(i);
            blackPawnPushes[i] = blackPawnPush;

            if (i >= 48) {
                blackPawnPushBlockerMask[i] = blackPawnPush & sixthRankMask;
            } else {
                blackPawnPushBlockerMask[i] = blackPawnPush;
            }
        }
    }

    private static long generateBlackPawnPush(int square) {
        long blackPawnPush = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
        if (square >= 8) // not on the bottom rank
            blackPawnPush |= (1L << (square - 8));
        if (square >= 48 && square < 56) { // on the 7th rank
            blackPawnPush |= (1L << (square - 16));
        }
        return blackPawnPush;
    }

    private static void generateBlackPawnAttacks() {
        for (int i = 0; i < 64; i++) {
            blackPawnAttacks[i] = generateBlackPawnAttack(i);
        }
    }

    private static long generateBlackPawnAttack(int square) {
        long blackPawnAttack = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
        if (square >= 8) { // not on the bottom rank
            if (square % 8 != 7) // not on the right file
                blackPawnAttack |= (1L << (square - 7));
            if (square % 8 != 0) // not on the left file
                blackPawnAttack |= (1L << (square - 9));
        }
        return blackPawnAttack;
    }

    private static void generateBlackPawnEnPassants() {
        for (int i = 0; i < 64; i++) {
            blackPawnEnPassants[i] = generateBlackPawnEnPassant(i);
        }
    }

    private static long generateBlackPawnEnPassant(int square) {
        if (square / 8 == 2) { // black pawns can perform en passant only on the 3rd rank
            return generateBlackPawnAttack(square);
        } else {
            return 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
        }
    }


}


/* Alternative Pawn Push generation code

https://www.chessprogramming.org/Pawn_Pushes_(Bitboards)
Generates pawn moves setwise

    private static long whiteSinglePushTargets(Position position) {
        return ((position.pieces[0] & position.pieceColors[0]) << 8) & ~position.occupancy;
    }

    private static long whiteDoublePushTargets(Position position) {
        long singlePushes = whiteSinglePushTargets(position);
        return (singlePushes << 8) & ~position.occupancy & fourthRankMask;
    }

    private static long blackSinglePushTargets(Position position) {
        return ((position.pieces[0] & position.pieceColors[1]) >> 8) & ~position.occupancy;
    }

    private static long blackDoublePushTargets(Position position) {
        long singlePushes = blackSinglePushTargets(position);
        return (singlePushes >> 8) & ~position.occupancy & sixthRankMask;
    }

    // Returns white pawns with an empty square in front of it
    private static long whitePawnsAbleToPush(Position position) {
        return (~position.occupancy >> 8) & position.pieceColors[0] & position.pieces[0];
    }

    private static long blackPawnsAbleToPush(Position position) {
        return (~position.occupancy << 8) & position.pieceColors[1] & position.pieces[0];
    }

    // Returns pawns that can double push and necessarily on the start square
    private static long whitePawnsAbleToDoublePush(Position position) {
        // ANDS empty squares (specifically ones one in front of pawns) with those two in front of pawns by shifting empty squares down one
        long emptyRank3 = ~position.occupancy & ((~position.occupancy & fourthRankMask) >> 8);

        // Return single push with modified empty
        return (emptyRank3 >> 8) & position.pieceColors[0] & position.pieces[0];
    }

    private static long blackPawnsAbleToDoublePush(Position position) {
        // ANDS empty squares (specifically ones one in front of pawns) with those two in front of pawns by shifting empty squares down one
        long emptyRank3 = ~position.occupancy & ((~position.occupancy & fourthRankMask) << 8);

        // Return single push with modified empty
        return (emptyRank3 << 8) & position.pieceColors[1] & position.pieces[0];
    }

    public static long getSinglePushPawns(Position position) {
        if (position.activePlayer == 0) {
            return whitePawnsAbleToPush(position);
        }
        return blackPawnsAbleToPush(position);
    }

    public static long getDoublePushPawns(Position position) {
        if (position.activePlayer == 0) {
            return whitePawnsAbleToDoublePush(position);
        }
        return blackPawnsAbleToDoublePush(position);
    }
*/

