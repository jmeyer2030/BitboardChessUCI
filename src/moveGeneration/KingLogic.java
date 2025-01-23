package moveGeneration;

import board.Color;
import board.Position;

public class KingLogic {
	
	public static long[] moveBoards;

//Public Methods
	public void initializeAll() {
		KingLogic.moveBoards = generateMoveBoards();
	}
	
	public long getCaptures(int square, Position position) {
		long enemyPieces = position.pieceColors[Color.flipColor(position.activePlayer).ordinal()];
		return moveBoards[square] & enemyPieces;
	}
	
	public long getQuietMoves(int square, Position position) {
		return moveBoards[square] & ~position.occupancy;
	}
	
	public long getKingAttacks(int square) {
		return moveBoards[square];
	}
	
	public long generateCastles(int square, Position position) {
		return position.activePlayer == Color.WHITE ? generateWhiteCastles(position) : generateBlackCastles(position);
	}

//Private Helper Methods
	private long generateWhiteCastles(Position position) {
		long result = 0L;
		//queen side
		if (((position.castleRights & (1 << 3)) != 0) &&
				(((position.pieces[3] & position.pieceColors[0]) & (1L << 0)) != 0L) &&
				((position.occupancy & (1L << 1)) == 0L) &&
				((position.occupancy & (1L << 2)) == 0L) &&
				((position.occupancy & (1L << 3)) == 0L) &&
				((position.pieceColors[0] & (1L << 0)) != 0L))
			result |= (1L << 2);
		//king side
		if (((position.castleRights & (1 << 2)) != 0) &&
				(((position.pieces[3] & position.pieceColors[0]) & (1L << 7)) != 0L) &&
				((position.occupancy & (1L << 5)) == 0L) &&
				((position.occupancy & (1L << 6)) == 0L) &&
				((position.pieceColors[0] & (1L << 7)) != 0L))
			result |= (1L << 6);
		return result;
	}
	
	private long generateBlackCastles(Position position) {
		long result = 0L;
		//queenside
		if (((position.castleRights & (1 << 1)) != 0) &&
				(((position.pieces[3] & position.pieceColors[1]) & (1L << 56)) != 0L) &&
				((position.occupancy & (1L << 57)) == 0L) &&
				((position.occupancy & (1L << 58)) == 0L) &&
				((position.occupancy & (1L << 59)) == 0L) &&
				((position.pieceColors[1] & (1L << 56)) != 0L))
			result |= (1L << 58);
		//kingside
		if (((position.castleRights & (1 << 0)) != 0) &&
				(((position.pieces[3] & position.pieceColors[1]) & (1L << 63)) != 0L) &&
				((position.occupancy & (1L << 61)) == 0L) &&
				((position.occupancy & (1L << 62)) == 0L) &&
				((position.pieceColors[1] & (1L << 63)) != 0L))
			result |= (1L << 62);
		return result;
	}
	
	private long[] generateMoveBoards() {
		long[] moveBoards = new long[64];
		for (int i = 0; i < 64; i++) {
			moveBoards[i] = generateMoveBoard(i);
		}
		return moveBoards;
	}
	
	
	/**
	 * Returns a bitboard of squares that the king attacks at a given position
	 * @Param square an integer between 0 and 63
	 * @Return a bitboard of squares the king attacks
	 */
	private long generateMoveBoard(int square) {
		assert square < 64;
		assert square >= 0;
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
	
}