package moveGeneration;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import system.Logging;

public class RookLogic extends MagicBitboard{	
	
//Static fields
	private static final Logger LOGGER = Logging.getLogger(RookLogic.class);
	
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
	
	public static List<List<Long>> moveBoards;
	
	public static long[] magicNumbers =
		{144133057304076800L, 4701793197762887680L, -9151279258172126976L, 144124009940783169L, 5872698314287351808L,
		144132788852099073L, 288230930236575748L, 36047111917142144L, 290622915605434400L, 148759662735393409L,
		-8070168987475902208L, 3477060456026800161L, 2306124518567052304L, 2814819694805505L, -9217038776862113664L,
		9147941040177280L, 1807213936532013060L, 141837553647616L, 9007749555818496L, 4931441867922867200L,
		5332827107867762704L, 18155685820760576L, 1229483802079003136L, -9219956953197576063L, 585538322449858600L,
		2598664983503970310L, 4503670496428160L, 79171280699520L, 146371388084060288L, 216810533238611984L,
		283691180363777L, 10134207263178852L, 2323857958561120288L, 2310346746284212290L, 141020989755424L,
		4611827065178558464L, 4900479413352663200L, -9150610746776747008L, 37417822200144L, 4756927385616519300L,
		-8358644071538786288L, -9223301665946271712L, 40541193343344640L, 2958873751543906432L, 72066390198091904L,
		5188357945750454560L, 577041603716448257L, 39584704626697L, -9180306225826231808L, 2954396542078813312L,
		9008299036918016L, -3458623707612183936L, 11822533675320832L, 365354554215105024L, 4591569286005760L,
		216190934457583104L, 108158963120351874L, 18036811809030402L, 1143539341729793L, 281492425023497L,
		-9204794550683958270L, 72339086195032577L, 4629702650590470276L, 8071017881330266370L};

//Public methods
	/**
	 * Initializes all static fields
	 */
	public void initializeAll() {
		LOGGER.log(Level.INFO, "RookMagicBitboard field initialization has begun.");

		long start = System.currentTimeMillis();
		RookLogic.blockerMasks = generateBlockerMasks();
		RookLogic.blockerBoards = generateAllBlockerBoards();
		RookLogic.moveBoards = generateAllMoveBoards();
		RookLogic.moveBoards = sortAllMoveBoards();
		long end = System.currentTimeMillis();
		long elapsed = end - start;

		LOGGER.log(Level.INFO, "Rook initialization complete! Time taken: " + elapsed + " ms.");
	}

//Getter methods
	protected final int[] getNumBits() {
		return numBits;
	}
	
	protected final long[] getBlockerMasks() {
		return blockerMasks;
	}
	
	protected final List<List<Long>> getBlockerBoards() {
		assert blockerBoards != null;
		return blockerBoards;
	}
	
	protected final long[] getMagicNumbers() {
		return magicNumbers;
	}
	
	protected final List<List<Long>> getMoveBoards() {
		return moveBoards;
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
		private RookLogic rmb;
		
		public TestHook(RookLogic rmb) {
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
		
		public long generateMagicNumber(int square) {
			return rmb.generateMagicNumber(square);
		}
	}
}
