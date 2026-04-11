package com.jmeyer2030.driftwood.movegeneration;

public class AbsolutePins {
	public static final long[][] inBetween = new long[64][];

	/**
	 * pinRay[king][pinnedSq] is the ray from king through pinnedSq to the board
	 * edge, excluding the king square itself. For non-aligned squares the value is 0.
	 *
	 * <p>This is a superset of {@code inBetween[king][pinner] | (1L << pinner)}.
	 * The extra squares beyond the pinner are harmless because the piece's move
	 * board (magic bitboards) already stops at the first blocker (the pinner).</p>
	 */
	public static final long[][] pinRay = new long[64][];

	static {
		generateInBetween();
		generatePinRays();
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
	 * Populates pinRay[from][through] for all 64×64 pairs.
	 * For aligned squares, the ray starts one step from 'from' in the direction
	 * of 'through' and extends to the board edge.
	 */
	private static void generatePinRays() {
		for (int from = 0; from < 64; from++) {
			pinRay[from] = new long[64];
			for (int through = 0; through < 64; through++) {
				pinRay[from][through] = generateRay(from, through);
			}
		}
	}

	/**
	 * Generates a ray starting at 'from', going through 'through', to the board edge.
	 * Excludes 'from' itself. Returns 0 if the squares are not aligned on a rank,
	 * file, or diagonal, or if they are the same square.
	 */
	private static long generateRay(int from, int through) {
		if (from == through) return 0L;

		int rankFrom = from / 8, fileFrom = from % 8;
		int rankThrough = through / 8, fileThrough = through % 8;

		int dRank = Integer.signum(rankThrough - rankFrom);
		int dFile = Integer.signum(fileThrough - fileFrom);

		// Must be aligned on rank, file, or diagonal
		if (dRank != 0 && dFile != 0
				&& Math.abs(rankThrough - rankFrom) != Math.abs(fileThrough - fileFrom)) {
			return 0L;
		}

		long ray = 0L;
		int r = rankFrom + dRank;
		int f = fileFrom + dFile;
		while (r >= 0 && r < 8 && f >= 0 && f < 8) {
			ray |= 1L << (r * 8 + f);
			r += dRank;
			f += dFile;
		}
		return ray;
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
