package moveGeneration;

import board.Position;
import system.BBO;

public class KingLogic {
	//public static long[] moveBoards;
	public static long[] whiteMoveBoards;
	public static long[] blackMoveBoards;
	public static long[] attackBoards;
	
	public void initializeAll() {
		KingLogic.whiteMoveBoards = generateWhiteMoveBoards();
		KingLogic.blackMoveBoards = generateBlackMoveBoards();
		KingLogic.attackBoards = generateAttackBoards();
	}
	

	
	public long getMoveBoard(int square, Position position) {
		return position.whiteToPlay ? getWhiteKingMoves(square) : getBlackKingMoves(square);
	}
	
	public long getCaptures(int square, Position position) {
		long capturablePieces = position.whiteToPlay ? position.blackPieces : position.whitePieces;
		return attackBoards[square] & capturablePieces;
	}
	
	public long getQuietMoves(int square, Position position) {
		return attackBoards[square] & ~position.occupancy;
	}
	
	public long getKingAttacks(int square) {
		return attackBoards[square];
	}
	
	public long getBlackKingMoves(int square) {
		return blackMoveBoards[square];
	}
	
	public long getWhiteKingMoves(int square) {
		return whiteMoveBoards[square];
	}
	
	public long generateCastles(int square, Position position) {
		return position.whiteToPlay ? generateWhiteCastles(square, position) : generateBlackCastles(square, position);
	}
	
	private long generateWhiteCastles(int square, Position position) {
		if ((position.initialPos & (1L << square)) == 0L)
			return 0L;
		long result = 0L;
		//queenside
		if (((position.initialPos & (1L << 0)) != 0L) &&
				!BBO.squareHasPiece(position.occupancy, 1) &&
				!BBO.squareHasPiece(position.occupancy, 2) &&
				!BBO.squareHasPiece(position.occupancy, 3) &&
				!BBO.squareHasPiece(position.blackAttackMap, 2) &&
				!BBO.squareHasPiece(position.blackAttackMap, 3) &&
				!BBO.squareHasPiece(position.blackAttackMap, 4))
			result |= (1L << 2);
		//kingside
		if (((position.initialPos & (1L << 7)) != 0L) &&
				!BBO.squareHasPiece(position.occupancy, 5) &&
				!BBO.squareHasPiece(position.occupancy, 6) &&
				!BBO.squareHasPiece(position.blackAttackMap, 4) &&
				!BBO.squareHasPiece(position.blackAttackMap, 5) &&
				!BBO.squareHasPiece(position.blackAttackMap, 6))
			result |= (1L << 6);
		return result;
	}
	
	private long generateBlackCastles(int square, Position position) {
		if ((position.initialPos & (1L << square)) == 0L)
			return 0L;
		long result = 0L;
		//queenside
		if (((position.initialPos & (1L << 56)) != 0L) &&
				!BBO.squareHasPiece(position.occupancy, 57) &&
				!BBO.squareHasPiece(position.occupancy, 58) &&
				!BBO.squareHasPiece(position.occupancy, 59) &&
				!BBO.squareHasPiece(position.blackAttackMap, 58) &&
				!BBO.squareHasPiece(position.blackAttackMap, 59) &&
				!BBO.squareHasPiece(position.blackAttackMap, 60))
			result |= (1L << 58);
		//kingside
		if (((position.initialPos & (1L << 63)) != 0L) &&
				!BBO.squareHasPiece(position.occupancy, 61) &&
				!BBO.squareHasPiece(position.occupancy, 62) &&
				!BBO.squareHasPiece(position.blackAttackMap, 60) &&
				!BBO.squareHasPiece(position.blackAttackMap, 61) &&
				!BBO.squareHasPiece(position.blackAttackMap, 62))
			result |= (1L << 62);
		return result;
	}
	
	private long[] generateBlackMoveBoards() {
		long[] blackMoveBoards = new long[64];
		for (int i = 0; i < 64; i++) {
			blackMoveBoards[i] = generateMoveBoard(i);
		}
		//add both king/queen side castling
		blackMoveBoards[59] |= 0b01000100_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
		
		return blackMoveBoards;
	}
	
	private long[] generateWhiteMoveBoards() {
		long[] whiteMoveBoards = new long[64];
		for (int i = 0; i < 64; i++) {
			whiteMoveBoards[i] = generateMoveBoard(i);
		}
		//add both king/queen side castling
		whiteMoveBoards[4] |= 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00100010L;
		
		return whiteMoveBoards;
	}
	
	private long[] generateAttackBoards() {
		long[] attackBoards = new long[64];
		for (int i = 0; i < 64; i++) {
			attackBoards[i] = generateMoveBoard(i);
		}
		return attackBoards;
	}
	
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
	 *@Param long bitboard 
	 */
	protected void printBoard(long mask) {
		System.out.println("Little-endian rank-file board representation: ");
	    for (int rank = 7; rank >= 0; rank--) {
	        for (int file = 0; file < 8; file++) {
	            System.out.print(((mask & (1L << (rank * 8 + file))) != 0) ? "1 " : "0 ");
	        }
	        System.out.println();
	    }
	}
}
/*
public static void main(String[] args) {
	KingBitboard kb = new KingBitboard();
	kb.printBoard(kb.generateMoveBoard(35));
	
}

	private long[] generateMoveBoards() {
		long[] moveBoards = new long[64];
		for (int i = 0; i < 64; i++) {
			moveBoards[i] = generateMoveBoard(i);
		}
		return moveBoards;
	}
*/