package moveGeneration;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import system.Logging;

public class BishopLogic extends MagicBitboard{
	
	private static final Logger LOGGER = Logging.getLogger(BishopLogic.class);

	// Number of potential blockers for each square
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
	
	public static final long[] magicNumbers = {4762574765071009120L, 3035709978020225024L, -8573683670269558392L,
		-9220943688114896895L, 1297336034757443584L, 2550944558485632L, 149208143149662272L, 1338112498205186L,
		439171341070125185L, 45222333252352L, 1873655792922535428L, 343439132422310928L, 353013042521088L,
		2305854009439158340L, -8069883179750587120L, 27049637468217344L, -8628333643761958654L, 1154082662173008656L,
		18577434498828544L, -9058946528189546096L, 1145837178716186L, 290622914533793800L, 3476858079382085825L,
		4630263437925057024L, 1161937778154948608L, 2320622644582946816L, -9223332452153949152L, 18155273571139616L,
		54324739509862405L, 83884490930262144L, 18165031879837696L, 9572382600626306L, 72693189070299136L,
		9289911334110208L, 577094414598930468L, 2201185943680L, 81065360228418576L, 22523500137418753L,
		288512959230576648L, -9222806887340420094L, 6922597914221085824L, 576610457717319936L, 864726390270869632L,
		4629709350871499012L, 4613973020062548224L, 602635484790816L, 9715301998433312L, 2308104996826120320L,
		4613094497927045643L, 81139568691150921L, 289357376730955792L, 576496075221893248L, 1134833477355536L,
		-9187236303706389498L, 4538955816263680L, 94909848055062536L, 2288102087338000L, 325109097941772320L,
		4971974126610155521L, 4904736654213906962L, 576531192185225748L, 4629700692083376256L, 1152957805956301832L,
		293299211259118089L};

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
		//BishopLogic.magicNumbers = generateAllMagicNumbers();
		//System.out.println(Arrays.toString(magicNumbers));
		BishopLogic.moveBoards = sortAllMoveBoards();
		long elapsed = System.currentTimeMillis() - start;
		
		LOGGER.log(Level.INFO, "Bishop initialization complete! Time taken: " + elapsed + " ms.");
	}
	
//Getter methods
	protected final int[] getNumBits() {
		return numBits;
	}
	
	protected final long[] getBlockerMasks() {
		return blockerMasks;
	}
	
	protected final List<List<Long>> getBlockerBoards() {
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
