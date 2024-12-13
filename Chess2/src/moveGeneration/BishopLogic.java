package moveGeneration;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import system.Logging;

public class BishopLogic extends MagicBitboard{
	
	private static final Logger LOGGER = Logging.getLogger(BishopLogic.class);
	
	public static int[] numBits = new int[]
					{6, 5, 5, 5, 5, 5, 5, 6, 
					 5, 5, 5, 5, 5, 5, 5, 5,
					 5, 5, 7, 7, 7, 7, 5, 5, 
					 5, 5, 7, 9, 9, 7, 5, 5, 
					 5, 5, 7, 9, 9, 7, 5, 5, 
					 5, 5, 7, 7, 7, 7, 5, 5, 
					 5, 5, 5, 5, 5, 5, 5, 5,
					 6, 5, 5, 5, 5, 5, 5, 6};
	
	public static long[] blockerMasks;
	
	public static List<List<Long>> blockerBoards;
	
	public static long[] magicNumbers;
	
	public static List<List<Long>> moveBoards;
	
	
//Public methods

	/**
	 * Initializes all static fields
	 */
	public void initializeAll() {
		LOGGER.log(Level.INFO, "BishopMagicBitboard field initialization has begun.");
		
		long start = System.currentTimeMillis();
		BishopLogic.blockerMasks = generateBlockerMasks();
		BishopLogic.blockerBoards = generateAllBlockerBoards();
		BishopLogic.moveBoards = generateAllMoveBoards();
		BishopLogic.magicNumbers = generateAllMagicNumbers();
		BishopLogic.moveBoards = sortAllMoveBoards();
		long elapsed = System.currentTimeMillis() - start;
		
		LOGGER.log(Level.INFO, "Bishop initialization complete! Time taken: " + elapsed + " ms.");
	}
	
//Getter methods
	protected int[] getNumBits() {
		return numBits;
	}
	
	protected long[] getBlockerMasks() {
		return blockerMasks;
	}
	
	protected List<List<Long>> getBlockerBoards() {
		return blockerBoards;
	}
	
	protected long[] getMagicNumbers() {
		return magicNumbers;
	}
	
	protected List<List<Long>> getMoveBoards() {
		return moveBoards;
	}
	
//Protected methods

	/**
	 * generates all potential locations of blockers for each square
	 * @return blocker mask array
	 */
	protected long[] generateBlockerMasks() {
		long[] blockerMasks = new long[64];
		for (int i = 0; i < 64; i++) {
			blockerMasks[i] = generateBlockerMask(i);
		}
		return blockerMasks;
	}
	
	/**
	 * This method returns a rook blocker-mask for a given square
	 * Blocker Mask: BB of all potential blocker locations
	 * @param square an integer between 0 and 63
	 * @return a bishop blockerMask if the bishop were on that square
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
	 * Returns a move board for a bishop blockerBoard
	 * @param blockerBoard, a bishop blockerBoard
	 * @param square, the square associated with the blockerBoard
	 * @return the moveBoard for that blockerBoard
	 */
	protected long generateMoveBoard(long blockerBoard, int square) {
		int rankLoc = square / 8;
		int fileLoc = square % 8;
		
		long moveBoard = 0;
		
	    // Diagonal (/ direction)
	    for (int rank = rankLoc + 1, file = fileLoc + 1; rank < 8 && file < 8; rank++, file++) {
	    	int currentLoc = 8 * rank + file;
	    	moveBoard |= (1L << currentLoc);
			long bitMask = 1L << currentLoc;
			if ((blockerBoard & bitMask) != 0) {
				break;
			}
	    }
	    for (int rank = rankLoc - 1, file = fileLoc - 1; rank >= 0 && file >= 0; rank--, file--) {
	    	int currentLoc = 8 * rank + file;
	    	moveBoard |= (1L << currentLoc);
			long bitMask = 1L << currentLoc;
			if ((blockerBoard & bitMask) != 0) {
				break;
			}
	    }

	    // Anti-diagonal (\ direction)
	    for (int rank = rankLoc - 1, file = fileLoc + 1; rank >= 0 && file < 8; rank--, file++) {
	    	int currentLoc = 8 * rank + file;
	    	moveBoard |= (1L << currentLoc);
			long bitMask = 1L << currentLoc;
			if ((blockerBoard & bitMask) != 0) {
				break;
			}
	    }
	    for (int rank = rankLoc + 1, file = fileLoc - 1; rank < 8 && file >= 0; rank++, file--) {
	    	int currentLoc = 8 * rank + file;
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
		private final BishopLogic bmb;
		
		public TestHook(BishopLogic bmb) {
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
		
		public long generateMagicNumber(int square, boolean isBishop) {
			return bmb.generateMagicNumber(square);
		}
	}
}
