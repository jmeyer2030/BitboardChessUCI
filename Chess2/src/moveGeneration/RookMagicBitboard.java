package moveGeneration;

import java.util.List;

public class RookMagicBitboard extends MagicBitboard{	
	/**
	 * 
	 * 
	 */
	
	
	/**
	 * This method returns a rook blocker-mask for a given square
	 * Meaning that it is every place that a piece could exist that would stop it from moving further
	 * @Param square an integer between 0 and 63
	 * @Return a rook blockerMask if the rook were on that square
	 */
	protected long generateBlockerMask(int square) {
		assert square >= 0;
		assert square <= 63;
		
		long result = 0b0L;
		
		int rankLoc = square / 8;
		int fileLoc = square % 8;
		
		for (int i = 0; i < 8; i++) {
			int sameRankBitLoc = rankLoc * 8 + i;//bit to flip within the same rank
			int sameFileBitLoc = i * 8 + fileLoc;//bit to flip within the same file
			result |= (1L << sameRankBitLoc);
			result |= (1L << sameFileBitLoc);
		}
		
		result &= ~(1L << rankLoc * 8);
		result &= ~(1L << rankLoc * 8 + 7);
		result &= ~(1L << fileLoc);
		result &= ~(1L << 7 * 8 + fileLoc);
		result &= ~(1L << square);
		
		return result;
	}
	
	/**
	 * Returns an array that maps a square to the number of bits required for its magic number
	 * @Param square
	 * @Return a int[] where arr[square] = numBits required for magic number.
	 */
	protected int[] generateNumBits() {
		int[] rookNumBits = new int[] {12, 11, 11, 11, 11, 11, 11, 12, 
									   11, 10, 10, 10, 10, 10, 10, 11, 
									   11, 10, 10, 10, 10, 10, 10, 11, 
									   11, 10, 10, 10, 10, 10, 10, 11, 
									   11, 10, 10, 10, 10, 10, 10, 11, 
									   11, 10, 10, 10, 10, 10, 10, 11, 
									   11, 10, 10, 10, 10, 10, 10, 11, 
									   12, 11, 11, 11, 11, 11, 11, 12};
		return rookNumBits;
	}
	

	
	/**
	 * Returns a move board for a rook blockerBoard
	 * @Param blockerBoard, a rook blockerBoard
	 * @Param square, the square associated with the blockerBoard
	 * @Return the moveBoard for that blockerBoard
	 */
	protected long generateMoveBoard(long blockerBoard, int square) {
		int rankLoc = square / 8;
		int fileLoc = square % 8;
		
		long moveBoard = 0;
		
		//iterate right
		for (int file = fileLoc + 1; file < 8; file++) {
			int currentLoc = 8 * rankLoc + file;
			moveBoard |= (1L << currentLoc);
			long bitMask = 1L << currentLoc;
			if ((blockerBoard & bitMask) != 0) {
				break;
			}
		}
		
		//iterate left
		for (int file = fileLoc - 1; file >= 0; file--) {
			int currentLoc = 8 * rankLoc + file;
			moveBoard |= (1L << currentLoc);
			long bitMask = 1L << currentLoc;
			if ((blockerBoard & bitMask) != 0) {
				break;
			}
		}
		//iterate up
		for (int rank = rankLoc + 1; rank < 8; rank++) {
			int currentLoc = 8 * rank + fileLoc;
			moveBoard |= (1L << currentLoc);
			long bitMask = 1L << currentLoc;
			if ((blockerBoard & bitMask) != 0) {
				break;
			}
		}
		//iterate down
		for (int rank = rankLoc - 1; rank >= 0; rank--) {
			int currentLoc = 8 * rank + fileLoc;
			moveBoard |= (1L << currentLoc);
			long bitMask = 1L << currentLoc;
			if ((blockerBoard & bitMask) != 0) {
				break;
			}
		}
		
		return moveBoard;
	}
	
//Testing help
	public static class TestHook {
		private RookMagicBitboard rmb;
		
		public TestHook(RookMagicBitboard rmb) {
			this.rmb = rmb;
		}
		
		public long testGenerateBlockerMask(int square) {
			return rmb.generateBlockerMask(square);
		}
		
		public long testGenerateMoveBoard(long blockerBoard, int square) {
			return rmb.generateMoveBoard(blockerBoard, square);
		}
		
		public List<Long> testGenerateBlockerBoards(long blockerBoard) {
			return rmb.generateBlockerBoards(blockerBoard);
		}
		
		public long generateMagicNumber(List<Long> blockerBoards) {
			return rmb.magicNumber(blockerBoards);
		}
	}
	
}

/*
public static void main(String args[]) {
	int square = 18;
	RookMagicBitboard rmb = new RookMagicBitboard();
	long blockerMask = rmb.generateBlockerMask(square);
	rmb.printBoard(blockerMask);
	//System.out.println(Long.bitCount(result));
	
	List<Long> blockerBoards = rmb.generateBlockerBoards(blockerMask);
	//System.out.println(blockerBoards.size());
	
	rmb.printBoard(blockerBoards.get(483));
	rmb.printBoard(rmb.generateMoveBoard(blockerBoards.get(483), square));
	
	long magicNumber = rmb.magicNumber(blockerBoards);
	System.out.println("Magic number found: " + magicNumber);
}
*/
