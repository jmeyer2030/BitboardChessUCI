package moveGeneration;

public class KingBitboard {
	
	/**
	 * Returns a bitboard of squares that the king attacks at a given position
	 * @Param square an integer between 0 and 63
	 * @Return a bitboard of squares the king attacks
	 */
	private long generateMoveBoard(int square) {
		assert square < 64;
		assert square > 0;
		int rankLoc = square / 8;
		int fileLoc = square % 8;
		
		long moveBoard = 0;
		
		int testRank = rankLoc + 1;
		int testFile = fileLoc;
		if (testRank < 8)
			moveBoard |= 1L << (8 * testRank + testFile);
		
		testRank = rankLoc + 1;
		testFile = fileLoc + 1;
		if (testRank < 8 && testFile < 8)
			moveBoard |= 1L << (8 * testRank + testFile);
		
		testRank = rankLoc;
		testFile = fileLoc + 1;
		if (testFile < 8)
			moveBoard |= 1L << (8 * testRank + testFile);
		
		testRank = rankLoc - 1;
		testFile = fileLoc + 1;
		if (testRank >= 0 && testFile < 8)
			moveBoard |= 1L << (8 * testRank + testFile);
			
		testRank = rankLoc - 1;
		testFile = fileLoc;
		if (testRank >= 0)
			moveBoard |= 1L << (8 * testRank + testFile);

		testRank = rankLoc - 1;
		testFile = fileLoc - 1;
		if (testRank >= 0 && testFile >= 0)
			moveBoard |= 1L << (8 * testRank + testFile);
		
		testRank = rankLoc;
		testFile = fileLoc - 1;
		if (testFile >= 0)
			moveBoard |= 1L << (8 * testRank + testFile);
		
		testRank = rankLoc + 1;
		testFile = fileLoc - 1;
		if (testRank < 8 && testFile >= 0)
			moveBoard |= 1L << (8 * testRank + testFile);
		
		return moveBoard;
	}
	
	
	
//Testing help
	/**
	 *This method prints the board in little-endian rank-file form 
	 */
	protected void printBoard(long mask) {
		System.out.println("Little-endian rank-file board representation: ");
	    for (int rank = 7; rank >= 0; rank--) {
	        for (int file = 0; file < 8; file++) {
	            System.out.print(((mask & (1L << (rank * 8 + file))) != 0) ? "1 " : "0 ");
	        }
	        System.out.println();
	    }
	    //System.out.println("Raw binary string: ");
	    //String binaryString = String.format("%64s", Long.toBinaryString(mask)).replace(' ', '0');
	    //System.out.println(binaryString);
	}
}
/*
public static void main(String[] args) {
	KingBitboard kb = new KingBitboard();
	kb.printBoard(kb.generateMoveBoard(35));
	
}
*/