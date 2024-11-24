package moveGeneration;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import system.Logging;

public class RookMagicBitboard extends MagicBitboard{	
	
	private static final Logger LOGGER = Logging.getLogger(RookMagicBitboard.class);
	
	public static int[] numBits = new int[] 
			  {12, 11, 11, 11, 11, 11, 11, 12, 
			   11, 10, 10, 10, 10, 10, 10, 11, 
			   11, 10, 10, 10, 10, 10, 10, 11, 
			   11, 10, 10, 10, 10, 10, 10, 11, 
			   11, 10, 10, 10, 10, 10, 10, 11, 
			   11, 10, 10, 10, 10, 10, 10, 11, 
			   11, 10, 10, 10, 10, 10, 10, 11, 
			   12, 11, 11, 11, 11, 11, 11, 12};
	
	public static long[] blockerMasks;
	
	public static List<List<Long>> blockerBoards;
	
	public static long[] magicNumbers;
	
	public static List<List<Long>> moveBoards;
	
	
//Public methods
	
	/**
	 * Initializes all static fields
	 */
	public void initializeAll() {
		LOGGER.log(Level.INFO, "RookMagicBitboard field initialization has begun.");
		
		LOGGER.log(Level.FINE, "Generating blocker masks...");
		RookMagicBitboard.blockerMasks = generateBlockerMasks();
		LOGGER.log(Level.INFO, "Generating blocker masks complete!");
		
		LOGGER.log(Level.FINE, "Generating blocker boards...");
		RookMagicBitboard.blockerBoards = generateAllBlockerBoards();
		LOGGER.log(Level.INFO, "Generating blocker boards complete!");
		
		LOGGER.log(Level.FINE, "Generating magic numbers...");
		RookMagicBitboard.magicNumbers = generateAllMagicNumbers();
		LOGGER.log(Level.INFO, "Generating magic numbers complete!");
		
		LOGGER.log(Level.FINE, "Generating move boards...");
		RookMagicBitboard.moveBoards = generateAllMoveBoards();
		LOGGER.log(Level.INFO, "Generating move boards complete!");
	}
	
	/**
	 * Returns a move board given a square and occupancy board
	 * @Param square
	 * @Param occupancyBoard
	 * @Return moveBoard
	 */
	public long getMoveBoard(int square, long occupancyBoard) {
		//compute blockerBoard
		long blockerBoard = occupancyBoard & blockerMasks[square];
		
		//compute index of the associated moveBoard
		long index = (magicNumbers[square] * blockerBoard) >> (64 - numBits[square]);

		return moveBoards.get(square).get((int) index);
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
	
//Protected methods
	/**
	 * Generates all blockerMasks for rook moves
	 * @Return array of blockerMasks
	 */
	protected long[] generateBlockerMasks() {
		long[] blockerMasks = new long[64];
		for (int i = 0; i < 64; i++) {
			blockerMasks[i] = generateBlockerMask(i);
		}
		return blockerMasks;
	}
	
	/**
	 * Generates a rook blocker-mask for a given square
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
			return rmb.generateMagicNumber(blockerBoards);
		}
	}
	
}
