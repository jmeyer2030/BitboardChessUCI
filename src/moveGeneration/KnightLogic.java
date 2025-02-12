package moveGeneration;

import board.Position;

public class KnightLogic {
	private static long[] knightMoves = new long[64];
	
	static {
		generateAllKnightMoves();
	}

	public static long getMoveBoard(int square, Position position) {
		long activePlayerPieces = position.pieceColors[position.activePlayer];
		return knightMoves[square] & ~activePlayerPieces;
	}
	
	public static long getCaptures(int square, Position position) {
		long enemyPieces = position.activePlayer == 0 ? position.pieceColors[1] : position.pieceColors[0];
		return knightMoves[square] & enemyPieces;
	}
	
	public static long getQuietMoves(int square, Position position) {
		return knightMoves[square] & ~position.occupancy;
	}
	
	public static long getAttackBoard(int square, Position position) {
		return knightMoves[square];
	}
	
	
	
	private static void generateAllKnightMoves() {
		for (int i = 0; i < 64; i++) {
			knightMoves[i] = generateKnightMoves(i);
		}
	}
	
	private static long generateKnightMoves(int square) {
	    long knightMove = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
	    // Possible knight moves relative to the current square
	    int[] knightOffsets = {
	        17, 15, 10, 6, -17, -15, -10, -6
	    };
	    for (int offset : knightOffsets) {
	        int targetSquare = square + offset;
	        // Ensure the target square is valid
	        if (targetSquare >= 0 && targetSquare < 64) {
	            // Check if the move wraps around the board
	            int currentFile = square % 8;
	            int targetFile = targetSquare % 8;
	            // Valid knight moves must remain within 2 files of the original file
	            if (Math.abs(currentFile - targetFile) <= 2) {
	                knightMove |= (1L << targetSquare);
	            }
	        }
	    }
	    return knightMove;
	}
}
