package moveGeneration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import board.Position;
import system.Logging;

public abstract class MagicBitboard implements LogicInterface{
	
	/*
	 Little-Endian Rank-File Mapping:
		56, 57, 58, 59, 60, 61, 62, 63
		48, 49, 50, 51, 52, 53, 54, 55
		40, 41, 42, 43, 44, 45, 46, 47
		32, 33, 34, 35, 36, 37, 38, 39
		24, 25, 26, 27, 28, 29, 30, 31
		16, 17, 18, 19, 20, 21, 22, 23
		8,  9,  10, 11, 12, 13, 14, 15
		0,  1,  2,  3,  4,  5,  6,  7
	*/
	
	private static final Logger LOGGER = Logging.getLogger(MagicBitboard.class);

//Abstract Methods:
	public abstract void initializeAll();
	protected abstract long generateBlockerMask(int square);
	protected abstract long generateMoveBoard(long blockerBoard, int square);
	protected abstract int[] getNumBits();
	protected abstract long[] getBlockerMasks();
	protected abstract List<List<Long>> getBlockerBoards();
	protected abstract long[] getMagicNumbers();
	protected abstract List<List<Long>> getMoveBoards();
	
	
//Public Methods:

	
	/**
	 * Returns a move board given a square and position
	 * @param square square
	 * @param position position
	 * @return moveBoard of all moves
	 */
	public long getMoveBoard(int square, Position position) {
		long activePlayerPieces = position.pieceColors[position.activePlayer.ordinal()];
		long blockerBoard = position.occupancy & getBlockerMasks()[square];
		int index = getIndexForBlocker(blockerBoard, square);
		return getMoveBoards().get(square).get(index) & ~activePlayerPieces;
	}
	
	public long getCaptures(int square, Position position) {
		return getMoveBoard(square, position) & position.occupancy;
	}
	
	public long getQuietMoves(int square, Position position) {
		return getMoveBoard(square, position) & ~position.occupancy;
	}
	
	/**
	 * Returns an attack board given a square and occupancy board
	 * @param square
	 * @param position
	 * @return attackBoard of all attacks
	 */
	public long getAttackBoard(int square, Position position) {
		long blockerBoard = position.occupancy & getBlockerMasks()[square];
		int index = getIndexForBlocker(blockerBoard, square);
		return getMoveBoards().get(square).get(index);
	}
	
	public long getAttackBoard(int square, long occupancy) {
		long blockerBoard = occupancy & getBlockerMasks()[square];
		int index = getIndexForBlocker(blockerBoard, square);
		return getMoveBoards().get(square).get(index);
	}

	
//Implemented Methods:
	/**
	 * Generates an array of the blockerMasks
	 * @return array of blockerMasks
	 */
	protected long[] generateBlockerMasks() {
		long[] blockerMasks = new long[64];
		for (int i = 0; i < 64; i++) {
			blockerMasks[i] = generateBlockerMask(i);
		}
		return blockerMasks;
	}	
	
	/**
	 *This method returns all blockerBoards for a given blockerMask 
	 * @param blockerMask an arbitrary blocker-mask
	 * @return list of all blockerBoards for a blockerMask
	 */
	protected List<Long> generateBlockerBoards(long blockerMask) {
		int numBlocks = Long.bitCount(blockerMask); 
		assert numBlocks <= 12;
		
		String binaryRep = Long.toBinaryString(blockerMask);
		int length = binaryRep.length();
		
        // Find positions of '1's
        List<Integer> onesPositions = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            if (binaryRep.charAt(i) == '1') {
                onesPositions.add(i);
            }
        }
        
        List<Long> permutations = new ArrayList<>();
        // Generate all subsets of onesPositions using bitmasking
        for (int mask = 0; mask < (1 << onesPositions.size()); mask++) {
            long perm = blockerMask;
            for (int i = 0; i < onesPositions.size(); i++) {
                if ((mask & (1 << i)) != 0) {
                    // Flip the corresponding bit to 0
                    perm &= ~(1L << (length - onesPositions.get(i) - 1));
                }
            }
            permutations.add(perm);
        }
        
        return permutations;
	}
	
	/**
	 * This method searches for and returns a magic number for all the blockerBoards of a square
	 * @param square that we want to generate the magic number for
	 * @return a long that serves as an surjective mapping from a blockerBoard to the index associated with its moveBoard
	 */
	protected long generateMagicNumber(int square) {
		//Retrieve pre-initialized data
		List<Long> blockerBoards = getBlockerBoards().get(square);
		List<Long> moveBoards = getMoveBoards().get(square);
		long mask = getBlockerMasks()[square];
		int reqNumBits = getNumBits()[square];
		
		//init a new random
		Random random = new Random();

		int indexMask = ((1 << reqNumBits) - 1);
		int rightShift = 64 - reqNumBits;
		while(true) {
			boolean failed = false;
			Map<Integer, Long> indexToMoveBoard = new HashMap<Integer, Long>();
			long magicCandidate = random.nextLong() & random.nextLong() & random.nextLong();
			long topByte = (((mask * magicCandidate) & 0xFF00000000000000L) >>> 56);
			if (count1s(topByte) < 6)
				continue;
			for (int i = 0; i < blockerBoards.size(); i++) {//Long blockerBoard : blockerBoards
				long product = blockerBoards.get(i) * magicCandidate;
				int index = (int) (product >> rightShift) & indexMask; //
				if (indexToMoveBoard.containsKey(index) && !indexToMoveBoard.get(index).equals(moveBoards.get(i))){
					failed = true;
					break;
				}
				indexToMoveBoard.put(index, moveBoards.get(i));
			}
			if (!failed) {
				return magicCandidate;
			}
		}
	} 
	

	//Get 
    protected int getIndexForBlocker(long blockerBoard, int square) {
        // Function to get the index
        return (int) ((getMagicNumbers()[square] * blockerBoard) >> (64 - getNumBits()[square]) 
                & ((1 << getNumBits()[square]) - 1));
    }

    protected List<Long> sortMoveBoards(int square) {
    	List<Long> moveBoards = getMoveBoards().get(square);
    	List<Long> blockerBoards = getBlockerBoards().get(square);
    	
    	
        // Combine moveBoards and blockerBoards into pairs
        List<Pair> combinedList = new ArrayList<>();
        for (int i = 0; i < moveBoards.size(); i++) {
            long moveBoard = moveBoards.get(i);
            long blockerBoard = blockerBoards.get(i);
            int index = getIndexForBlocker(blockerBoard, square);  // Get the corresponding index
            combinedList.add(new Pair(moveBoard, index));
        }

        // Sort the combined list by the index (second element of the pair)
        Collections.sort(combinedList, (a, b) -> Integer.compare(a.index, b.index));

        // Extract the sorted moveBoards
        List<Long> sortedMoveBoards = new ArrayList<>();
        for (Pair pair : combinedList) {
            sortedMoveBoards.add(pair.moveBoard);
        }

        return sortedMoveBoards;
    }

    // Pair class to hold the moveBoard and its associated index
    private static class Pair {
        long moveBoard;
        int index;

        Pair(long moveBoard, int index) {
            this.moveBoard = moveBoard;
            this.index = index;
        }
    }
    
    protected List<List<Long>> sortAllMoveBoards() {
    	List<List<Long>> moveBoards = new ArrayList<List<Long>>();
    	for (int i = 0; i < 64; i++) {
    		moveBoards.add(sortMoveBoards(i));
    	}
    	return moveBoards;
    }
	
	
	/**
	 * Returns the number of 1s in the binary form of a long
	 * @param value long
	 * @return the number of 1s in its binary form
	 */
    private static int count1s(long value) {
        int count = 0;
        while (value != 0) {
            count += (value & 1); // Add 1 if the least significant bit is set
            value >>>= 1;        // Unsigned right shift to process the next bit
        }
        return count;
    }
    

	
    /**
     * Generates the corresponding list of moveBoards
     * @param square
     * @return moveBoardList
     */
	protected List<Long> generateMoveBoards(int square) {
		List<Long> blockerBoards = getBlockerBoards().get(square);
		List<Long> moveBoardList = new ArrayList<Long>();
		for (Long blockerBoard : blockerBoards) {
			moveBoardList.add(generateMoveBoard(blockerBoard, square));
		}
		
		return moveBoardList;
	}
	
	
	/**
	 * Returns an array of all blockerboards on each square
	 * @return List<List<Long>> blockerBoards, every blocker configuration for each square
	 */
	protected List<List<Long>> generateAllBlockerBoards() {
		assert getBlockerMasks() != null;
		long[] blockerMasks = getBlockerMasks();
		List<List<Long>> blockerBoards = new ArrayList<List<Long>>();
		for (int i = 0; i < 64; i++) {
			blockerBoards.add(generateBlockerBoards(blockerMasks[i]));
		}
		return blockerBoards;
	}
	
	
	
	/**
	 * generates an array of the magic numbers
	 * @return a long array of the magic numbers
	 */
	protected long[] generateAllMagicNumbers() {
		long[] magicNumbers = new long[64];
		for (int i = 0; i < 64; i++) {
			magicNumbers[i] = generateMagicNumber(i);
			LOGGER.log(Level.FINER, "Found a magic number for position: " + i);//should be finer
		}
		return magicNumbers;
	}
	
	
	/**
	 * Returns a List<List<Long>> of all move boards
	 * @return List<List<Long>> moveBoards
	 */
	protected List<List<Long>> generateAllMoveBoards() {
		List<List<Long>> moveBoards = new ArrayList<List<Long>>();
		for (int i = 0; i < 64; i++) {
			moveBoards.add(i, generateMoveBoards(i));
		}
		
		return moveBoards;
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
	}
}
/*
*
* public long xrayAttacks(int square, Position position) {
		long attacks = getAttackBoard(square, position);
		long removeFirstHit = (position.whiteToPlay ? position.whitePieces : position.blackPieces) & attacks;//first hit pieces
		return attacks ^ getAttackBoard(square, position.occupancy ^ removeFirstHit);
	}
*
*
*
* */