package moveGeneration;

import board.Color;
import board.Position;

public class PawnLogic {
	public static long[] whitePawnPushes;
	public static long[] whitePawnAttacks;
	public static long[] whitePawnEnPassants;
	public static long[] blackPawnPushes;
	public static long[] blackPawnAttacks;
	public static long[] blackPawnEnPassants;
	
	public static long[] whitePawnPushBlockerMask;
	public static long[] blackPawnPushBlockerMask;
	
	public static long thirdRankMask = 0b00000000_00000000_00000000_00000000_00000000_11111111_00000000_00000000L;
	public static long fourthRankMask = 0b00000000_00000000_00000000_00000000_11111111_00000000_00000000_00000000L;
	
	public static long sixthRankMask = 0b00000000_00000000_11111111_00000000_00000000_00000000_00000000_00000000L;
	public static long fifthRankMask = 0b00000000_00000000_00000000_11111111_00000000_00000000_00000000_00000000L;
	
	public static int[] numBitsWhite = new int[] 
			{0, 0, 0, 0, 0, 0, 0, 0, 
			 3, 4, 4, 4, 4, 4, 4, 3,
			 2, 3, 3, 3, 3, 3, 3, 2, 
			 2, 3, 3, 3, 3, 3, 3, 2,  
			 3, 5, 7, 9, 9, 7, 5, 5, 
			 2, 3, 3, 3, 3, 3, 3, 2, 
			 2, 3, 3, 3, 3, 3, 3, 2, 
			 0, 0, 0, 0, 0, 0, 0, 0};
	
	
	public void initializeAll() {
		whitePawnPushes = generateWhitePawnPushes();
		whitePawnAttacks = generateWhitePawnAttacks();
		whitePawnEnPassants = generateWhitePawnEnPassants();
		blackPawnPushes = generateBlackPawnPushes();
		blackPawnAttacks = generateBlackPawnAttacks();
		blackPawnEnPassants = generateBlackPawnEnPassants();
		whitePawnPushBlockerMask = generateWhitePawnPushes();
		blackPawnPushBlockerMask = generateBlackPawnPushes();
	}
	
	public long getAttackBoard(int square, Color attackColor) {
		if (attackColor == Color.WHITE) {
			return whitePawnAttacks[square];
		}
		return blackPawnAttacks[square];
	}
	
	public long getQuietMoves(int square, Position position) {
		if ((position.pieceColors[0] & (1L << square)) != 0) {
			return getWhitePawnPushes(square, position.occupancy);
		}
		return getBlackPawnPushes(square, position.occupancy);
	}
	
	public long getCaptures(int square, Position position) {
		if ((position.pieceColors[0] & (1L << square)) != 0) {
			return whitePawnAttacks[square] & position.pieceColors[1];
		}
		return blackPawnAttacks[square] & position.pieceColors[0];
	}
	
	public long getEnPassant(int square, Position position) {
		if (position.enPassant == 0 ||
		(((1L << position.enPassant) & (position.activePlayer == Color.WHITE ? whitePawnAttacks[square] :
		blackPawnAttacks[square])) == 0))// ||//If square doesn't attack the enPassant square//(position.enPassant + 1 != square && position.enPassant - 1 != square) ||
				//(square / 8 != 3 || square / 8 != 4)) // enpassant doesn't exist or isn't next to the piece
			return 0L;
		//if (position.whiteToPlay) {//if its a white pawn to be taken
		//	return (1L << (position.enPassant));
		//}
		return (1L << (position.enPassant));
	}
	
	
	private long getWhitePawnPushes(int square, long occupancyBoard) {
		if (square > 15) // if not on rank 2
			return whitePawnPushBlockerMask[square] & ~occupancyBoard;
		
		//true if there is a piece on rank 3 of the square's file
		boolean blockerAtRankThree = ((occupancyBoard & thirdRankMask & whitePawnPushBlockerMask[square]) != 0);
		
		if (blockerAtRankThree) // if its occupied we just return 0.
			return 0L;
		
		return  ~occupancyBoard & whitePawnPushBlockerMask[square];
	}
	
	private long getBlackPawnPushes(int square, long occupancyBoard) {
		if (square < 48) // if not on rank 2
			return blackPawnPushBlockerMask[square] & ~occupancyBoard;
		
		//true if there is a piece on rank 3 of the square's file
		boolean blockerAtRankSix = ((occupancyBoard & sixthRankMask & blackPawnPushBlockerMask[square]) != 0);
		
		if (blockerAtRankSix) // if its occupied we just return 0.
			return 0L;
		
		return  ~occupancyBoard & blackPawnPushBlockerMask[square];
	}
	
	private long[] generateWhitePawnPushes() {
		long[] whitePawnPushes = new long[64];
		for (int i = 0; i < 64; i++) {
			whitePawnPushes[i] = generateWhitePawnPush(i);
		}
		return whitePawnPushes;
	}
	
	private long generateWhitePawnPush(int square) {
		long whitePawnPush = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
		if (square < 56)
			whitePawnPush |= (1L << (square + 8));
		if (square < 16 && square > 7) {
			whitePawnPush |= (1L << (square + 16));
		}
		return whitePawnPush;
	}
	
	private long[] generateWhitePawnAttacks() {
		long[] whitePawnAttacks = new long[64];
		for (int i = 0; i < 64; i++) {
			whitePawnAttacks[i] = generateWhitePawnAttack(i);
		}
		return whitePawnAttacks;
	}
	
	private long generateWhitePawnAttack(int square) {
		long whitePawnAttack = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
		if (square < 56) { //below top rank
			if (square % 8 != 7) //left of right file
				whitePawnAttack |= (1L << (square + 9));
			if (square % 8 != 0) //right of left file
				whitePawnAttack |= (1L << (square + 7));
		}
		return whitePawnAttack;
	}
	
	
	private long[] generateWhitePawnEnPassants() {
		long[] whitePawnEnPassants = new long[64];
		for (int i = 0; i < 64; i++) {
			whitePawnEnPassants[i] = generateWhitePawnEnPassant(i);
		}
		return whitePawnEnPassants;
	}
	
	private long generateWhitePawnEnPassant(int square) {
		if (square / 8 == 5) {
			return generateWhitePawnAttack(square);
		} else {
			return 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
		}
	}
	
	private long[] generateBlackPawnPushes() {
	    long[] blackPawnPushes = new long[64];
	    for (int i = 0; i < 64; i++) {
	        blackPawnPushes[i] = generateBlackPawnPush(i);
	    }
	    return blackPawnPushes;
	}

	private long generateBlackPawnPush(int square) {
	    long blackPawnPush = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
	    if (square >= 8) // not on the bottom rank
	        blackPawnPush |= (1L << (square - 8));
	    if (square >= 48 && square < 56) { // on the 7th rank
	        blackPawnPush |= (1L << (square - 16));
	    }
	    return blackPawnPush;
	}

	private long[] generateBlackPawnAttacks() {
	    long[] blackPawnAttacks = new long[64];
	    for (int i = 0; i < 64; i++) {
	        blackPawnAttacks[i] = generateBlackPawnAttack(i);
	    }
	    return blackPawnAttacks;
	}

	private long generateBlackPawnAttack(int square) {
	    long blackPawnAttack = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
	    if (square >= 8) { // not on the bottom rank
	        if (square % 8 != 7) // not on the right file
	            blackPawnAttack |= (1L << (square - 7));
	        if (square % 8 != 0) // not on the left file
	            blackPawnAttack |= (1L << (square - 9));
	    }
	    return blackPawnAttack;
	}

	private long[] generateBlackPawnEnPassants() {
	    long[] blackPawnEnPassants = new long[64];
	    for (int i = 0; i < 64; i++) {
	        blackPawnEnPassants[i] = generateBlackPawnEnPassant(i);
	    }
	    return blackPawnEnPassants;
	}

	private long generateBlackPawnEnPassant(int square) {
	    if (square / 8 == 2) { // black pawns can perform en passant only on the 3rd rank
	        return generateBlackPawnAttack(square);
	    } else {
	        return 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
	    }
	}

}
