package system;

import java.util.ArrayList;
import java.util.List;

/**
 * Used for multiPurpose BitBoard Operations
 * All Static methods
 */
public class BBO {
	public static long[] fileClearMask;
	public static long[] rankClearMask;
	
	public void initializeAll() {
		fileClearMask = generateFileClearMask();
		rankClearMask = generateRankClearMask();
	}
	
	/**
	 * Returns a list of all locations of ones in the binary representation of the long in little-Endian rank-file form.
	 * @Param bitboard
	 * @Return squareList
	 */
	public static List<Integer> getSquares(long bitboard) {
		List<Integer> squareList = new ArrayList<Integer>();
		
        while (bitboard != 0) {
            // Find the position of the least significant 1 bit
            int square = Long.numberOfTrailingZeros(bitboard);
            // Add the position to the list
            squareList.add(square);
            // Remove the least significant 1 bit
            bitboard &= (bitboard - 1);
        }
		
		return squareList;
	}
	
	public static long removeSquare(long input, int square) {
		return input & ~(1L << square);
	}
	
	public static long addSquare(long input, int square) {
		return input | (1L << square);
	}
	
	public static boolean squareHasPiece(long bitboard, int square) {
		return (bitboard & (1L << square)) != 0;
	}
	
	public static int getFile(int square) {
		return square % 8;
	}
	
	public static int getRank(int square) {
		return square / 8;
	}
	
    private long[] generateFileClearMask() {
        long[] fileClearMask = new long[8];
        
        for (int file = 0; file < 8; file++) {
            long mask = 0xFFFFFFFFFFFFFFFFL; // Start with all bits set
            for (int rank = 0; rank < 8; rank++) {
                // Clear the bit corresponding to this file in all ranks
                mask &= ~(1L << (rank * 8 + file));
            }
            fileClearMask[file] = mask;
        }
        
        return fileClearMask;
    }
    
    

    private long[] generateRankClearMask() {
        long[] rankClearMask = new long[8];
        
        for (int rank = 0; rank < 8; rank++) {
            long mask = 0xFFFFFFFFFFFFFFFFL; // Start with all bits set
            for (int file = 0; file < 8; file++) {
                // Clear the bit corresponding to this rank in all files
                mask &= ~(1L << (rank * 8 + file));
            }
            rankClearMask[rank] = mask;
        }
        
        return rankClearMask;
    }
    
    public static long clearFile(long bitBoard, int file) {
    	return bitBoard & fileClearMask[file];
    }
    
    public static long clearRank(long bitBoard, int rank) {
    	return bitBoard & rankClearMask[rank];
    }
}
