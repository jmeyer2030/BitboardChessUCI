package main.java.moveGeneration;

public class AbsolutePins {
	public static final long[][] inBetween = new long[64][];
	
	static {
		generateInBetween();
	}

	/**
	* inBetween[i][j] is a bitboard representing all the squares in between 'i' and 'j'.
	*/
	private static void generateInBetween() {
		for (int i = 0; i < 64; i++) {
			inBetween[i] = new long[64];
			for (int j = 0; j < 64; j++) {
				inBetween[i][j] = generateBetween(i, j);
			}
		}
	}

	/**
	* Generates a bitboard representing the squares in between the two input squares
	* @param i one square
	* @param j another square
	*/
	private static long generateBetween(int i, int j) {
	    // If `i` and `j` are the same, there's no path.
	    if (i == j) return 0L;

	    // Extract rank and file of both squares
	    int rankI = i / 8, fileI = i % 8;
	    int rankJ = j / 8, fileJ = j % 8;

	    // Check if `i` and `j` are on the same rank
	    if (rankI == rankJ) {
	        long between = 0L;
	        int start = Math.min(fileI, fileJ) + 1;
	        int end = Math.max(fileI, fileJ);
	        for (int k = start; k < end; k++) {
	            between |= 1L << (rankI * 8 + k);
	        }
	        return between;
	    }

	    // Check if `i` and `j` are on the same file
	    if (fileI == fileJ) {
	        long between = 0L;
	        int start = Math.min(rankI, rankJ) + 1;
	        int end = Math.max(rankI, rankJ);
	        for (int k = start; k < end; k++) {
	            between |= 1L << (k * 8 + fileI);
	        }
	        return between;
	    }

	    // Check if `i` and `j` are on the same diagonal (major diagonal)
	    if (rankI - fileI == rankJ - fileJ) {
	        long between = 0L;
	        int start = Math.min(rankI, rankJ) + 1;
	        int end = Math.max(rankI, rankJ);
	        for (int k = 1; k < end - start + 1; k++) {
	            between |= 1L << ((Math.min(rankI, rankJ) + k) * 8 + Math.min(fileI, fileJ) + k);
	        }
	        return between;
	    }

	    // Check if `i` and `j` are on the same diagonal (minor diagonal)
	    if (rankI + fileI == rankJ + fileJ) {
	        long between = 0L;
	        int start = Math.min(rankI, rankJ) + 1;
	        int end = Math.max(rankI, rankJ);
	        for (int k = 1; k < end - start + 1; k++) {
	            between |= 1L << ((Math.min(rankI, rankJ) + k) * 8 + Math.max(fileI, fileJ) - k);
	        }
	        return between;
	    }

	    // If `i` and `j` are not on the same rank, file, or diagonal, return 0
	    return 0L;
	}
}
