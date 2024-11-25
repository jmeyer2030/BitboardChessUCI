package moveGeneration;

public class PawnBitboard {
	public static long[] whitePawnPushes;
	public static long[] whitePawnAttacks;
	public static long[] whitePawnEnPassants;
	public static long[] blackPawnPushes;
	public static long[] blackPawnAttacks;
	public static long[] blackPawnEnPassants;
	
	public void initializeAll() {
		whitePawnPushes = generateWhitePawnPushes();
		whitePawnAttacks = generateWhitePawnAttacks();
		whitePawnEnPassants = generateWhitePawnEnPassants();
		blackPawnPushes = generateBlackPawnPushes();
		blackPawnAttacks = generateBlackPawnAttacks();
		blackPawnEnPassants = generateBlackPawnEnPassants();
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
		if (square < 15 && square > 7) {
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
			if (square / 8 != 7) //left of right file
				whitePawnAttack |= (1L << (square + 9));
			if (square / 8 != 0) //right of left file
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
