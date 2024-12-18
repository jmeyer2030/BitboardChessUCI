package moveGeneration;

import board.Color;
import board.Position;

public class KnightLogic implements LogicInterface{
	public static long[] knightMoves;
	
	public void initializeAll() {
		knightMoves = generateAllKnightMoves();
	}
	
	public long getMoveBoard(int square, Position position) {
		long activePlayerPieces = position.pieceColors[position.activePlayer.ordinal()];
		return knightMoves[square] & ~activePlayerPieces;
	}
	
	public long getCaptures(int square, Position position) {
		long enemyPieces = position.activePlayer == Color.WHITE ? position.pieceColors[1] : position.pieceColors[0];
		return knightMoves[square] & enemyPieces;
	}
	
	public long getQuietMoves(int square, Position position) {
		return knightMoves[square] & ~position.occupancy;
	}
	
	public long getAttackBoard(int square, Position position) {
		return knightMoves[square];
	}
	
	
	
	private long[] generateAllKnightMoves() {
		long[] allKnightMoves = new long[64];
		for (int i = 0; i < 64; i++) {
			allKnightMoves[i] = generateKnightMoves(i);
		}
		return allKnightMoves;
	}
	
	private long generateKnightMoves(int square) {
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
