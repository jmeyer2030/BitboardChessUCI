package moveGeneration;

import java.util.List;

public class BishopMagicBitboard extends MagicBitboard{
	public static void main(String[] args) {
		BishopMagicBitboard bmb = new BishopMagicBitboard();
		long blockerMask1 = bmb.generateBlockerMask(0);
		long blockerMask2 = bmb.generateBlockerMask(4);
		long blockerMask3 = bmb.generateBlockerMask(28);
		bmb.printBoard(blockerMask1);
		bmb.printBoard(blockerMask2);
		bmb.printBoard(blockerMask3);
	}
	
	/**
	 * This method returns a rook blocker-mask for a given square
	 * Meaning that it is every place that a piece could exist that would stop it from moving further
	 * @Param square an integer between 0 and 63
	 * @Return a bishop blockerMask if the bishop were on that square
	 */
	protected long generateBlockerMask(int square) {
	    assert square >= 0;
	    assert square <= 63;

	    long result = 0b0L;

	    int rankLoc = square / 8;
	    int fileLoc = square % 8;

	    // Diagonal (/ direction)
	    for (int rank = rankLoc + 1, file = fileLoc + 1; rank < 7 && file < 7; rank++, file++) {
	        result |= (1L << (rank * 8 + file));
	    }
	    for (int rank = rankLoc - 1, file = fileLoc - 1; rank >= 1 && file >= 1; rank--, file--) {
	        result |= (1L << (rank * 8 + file));
	    }

	    // Anti-diagonal (\ direction)
	    for (int rank = rankLoc - 1, file = fileLoc + 1; rank >= 1 && file < 7; rank--, file++) {
	        result |= (1L << (rank * 8 + file));
	    }
	    for (int rank = rankLoc + 1, file = fileLoc - 1; rank < 7 && file >= 1; rank++, file--) {
	        result |= (1L << (rank * 8 + file));
	    }

	    return result;
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
		
	    // Diagonal (/ direction)
	    for (int rank = rankLoc + 1, file = fileLoc + 1; rank < 7 && file < 7; rank++, file++) {
	    	int currentLoc = 8 * rankLoc + file;
	    	moveBoard |= (1L << currentLoc);
			long bitMask = 1L << currentLoc;
			if ((blockerBoard & bitMask) != 0) {
				break;
			}
	    }
	    for (int rank = rankLoc - 1, file = fileLoc - 1; rank >= 1 && file >= 1; rank--, file--) {
	    	int currentLoc = 8 * rankLoc + file;
	    	moveBoard |= (1L << currentLoc);
			long bitMask = 1L << currentLoc;
			if ((blockerBoard & bitMask) != 0) {
				break;
			}
	    }

	    // Anti-diagonal (\ direction)
	    for (int rank = rankLoc - 1, file = fileLoc + 1; rank >= 1 && file < 7; rank--, file++) {
	    	int currentLoc = 8 * rankLoc + file;
	    	moveBoard |= (1L << currentLoc);
			long bitMask = 1L << currentLoc;
			if ((blockerBoard & bitMask) != 0) {
				break;
			}
	    }
	    for (int rank = rankLoc + 1, file = fileLoc - 1; rank < 7 && file >= 1; rank++, file--) {
	    	int currentLoc = 8 * rankLoc + file;
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
		private BishopMagicBitboard bmb;
		
		public TestHook(BishopMagicBitboard bmb) {
			this.bmb = bmb;
		}
		
		public long testGenerateBlockerMask(int square) {
			return bmb.generateBlockerMask(square);
		}
		
		public long testGenerateMoveBoard(long blockerBoard, int square) {
			return bmb.generateMoveBoard(blockerBoard, square);
		}
		
		public List<Long> testGenerateBlockerBoards(long blockerBoard) {
			return bmb.generateBlockerBoards(blockerBoard);
		}
		
		public long generateMagicNumber(List<Long> blockerBoards) {
			return bmb.magicNumber(blockerBoards);
		}
	}

	/**
	 * Returns an array that maps a square to the number of bits required for its magic number
	 * @Param square
	 * @Return a int[] where arr[square] = numBits required for magic number.
	 */
	protected int[] generateNumBits() {
		int[] bishopNumBits = new int[] {6, 5, 5, 5, 5, 5, 5, 6, 
										 5, 5, 5, 5, 5, 5, 5, 5,
										 5, 5, 7, 7, 7, 7, 5, 5, 
										 5, 5, 7, 9, 9, 7, 5, 5, 
										 5, 5, 7, 9, 9, 7, 5, 5, 
										 5, 5, 7, 7, 7, 7, 5, 5, 
										 5, 5, 5, 5, 5, 5, 5, 5,
										 6, 5, 5, 5, 5, 5, 5, 6};
		return bishopNumBits;
	}
}
