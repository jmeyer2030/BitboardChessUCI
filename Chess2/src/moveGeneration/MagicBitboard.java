package moveGeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class MagicBitboard {
	
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
		enum BoardSquare {
		    a8, b8, c8, d8, e8, f8, g8, h8,
		    a7, b7, c7, d7, e7, f7, g7, h7,
		    a6, b6, c6, d6, e6, f6, g6, h6,
		    a5, b5, c5, d5, e5, f5, g5, h5,
		    a4, b4, c4, d4, e4, f4, g4, h4,
		    a3, b3, c3, d3, e3, f3, g3, h3,
		    a2, b2, c2, d2, e2, f2, g2, h2,
		    a1, b1, c1, d1, e1, f1, g1, h1
		}
		
//Abstract Methods:
	/**
	 * Returns for its piece a blocker-mask for a given square
	 * @Param square an integer between 0 and 63
	 * @Return a blockerMask if the piece were on that square
	 */
	protected abstract long generateBlockerMask(int square);
	
	
	/**
	 * Returns for its piece a move board given a blockerBoard
	 * @Param blockerBoard, a piece blockerBoard
	 * @Param square, the square associated with the blockerBoard
	 * @Return a blockerMask if the piece were on that square
	 */
	protected abstract long generateMoveBoard(long blockerBoard, int square);
	
	
	/**
	 * Retrieves the already existing array of the number potential blocker squares
	 * @Return a int[] where arr[square] = numBits required for magic number.
	 */
	protected abstract int[] getNumBits();
	
	
	/**
	 * Retrieves the blockerMasks
	 * @Return already generated blockerMasks
	 */
	protected abstract long[] getBlockerMasks();
	
	/**
	 * Retrieves the already created list of blockerboards
	 * @Return the list of blockerboards
	 */
	protected abstract List<List<Long>> getBlockerBoards();
	
	
	/**
	 * Retrieces the list of magic numbers
	 * @Return the list of magic numbers
	 * 
	 */
	protected abstract long[] getMagicNumbers();

	
//Implemented Methods:
	
	/**
	 * Generates an array of the blockerMasks
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
	 *This method returns all blockerBoards for a given blockerMask 
	 * @Param blockerMask an arbitrary blocker-mask
	 * @Return list of all blockerBoards for a blockerMask
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
	 * This method searches for and returns a magic number for a bitboard
	 * @Param blockerBoards a complete list of blockerBoards
	 * @Return a long that serves as an injective mapping from a blockerBoard to the numbers 0-blockerBoards.size
	 */
	protected long generateMagicNumber(List<Long> blockerBoardList) {
		Random random = new Random();
		int numBoards = blockerBoardList.size();
		
		int reqNumBits = (int) (Math.log(numBoards)/Math.log(2));
		
		
		while(true) {
			boolean[] foundNum = new boolean[numBoards]; 
			long magicCandidate = random.nextLong();
			for (Long blockerBoard : blockerBoardList) {
				long product = blockerBoard * magicCandidate;
				int index = (int) product >> (64 - reqNumBits);
				if (foundNum[index]){
					break;
				}
				foundNum[index] = true;
			}
			return magicCandidate;
		}
	} 
	
	/**
	 * This method returns a list of moveBoards from a list of blockerBoards sorted 
	 * s.t. the index corresponds with the magic number calculated index
	 * @Param square the square the piece is on
	 * @Return the List of moves for each blockerBoard
	 */
	protected List<Long> generateMoveBoards(int square) {
		//Retrieve necessary data
		List<Long> blockerBoardList = getBlockerBoards().get(square);
		long magicNumber = getMagicNumbers()[square];
		int numBits = getNumBits()[square];
		//init our result list
		ArrayList<Long> moveBoards= new ArrayList<Long>();
		
		//process to generate the moveBoardList
		for (Long blockerBoard : blockerBoardList) {
			long index = (magicNumber * blockerBoard) >> (64 - numBits);
			moveBoards.add((int) index, generateMoveBoard(blockerBoard, square));
		}		
		return moveBoards;
	}
	
	
	/**
	 * Returns an array of all blockerboards on each square
	 * @Return List<List<Long>> blockerBoards, every blocker configuration for each square
	 */
	protected List<List<Long>> generateAllBlockerBoards() {
		long[] blockerMasks = getBlockerMasks();
		List<List<Long>> blockerBoards = new ArrayList<List<Long>>();
		for (int i = 0; i < 64; i++) {
			blockerBoards.add(generateBlockerBoards(blockerMasks[i]));
		}
		return blockerBoards;
	}
	
	
	
	/**
	 * generates an array of the magic numbers
	 * @Return a long array of the magic numbers
	 */
	protected long[] generateAllMagicNumbers() {
		List<List<Long>> blockerBoards = getBlockerBoards();
		long[] magicNumbers = new long[64];
		for (int i = 0; i < 64; i++) {
			magicNumbers[i] = generateMagicNumber(blockerBoards.get(i));
		}
		return magicNumbers;
	}
	
	
	/**
	 * 
	 * 
	 * 
	 */
	protected List<List<Long>> generateAllMoveBoards() {
		List<List<Long>> moveBoards = new ArrayList<List<Long>>();
		
		for (int i = 0; i < 64; i++) {
			moveBoards.add(i, generateMoveBoards(i));
		}
		
		return moveBoards;
	}
	
	/*
	protected int magicNumToMagicIndex(long magicNumber, long blockerBoard) {
		long product = blockerBoard * magicNumber;
		int index = (int) product
	}
	*/
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
