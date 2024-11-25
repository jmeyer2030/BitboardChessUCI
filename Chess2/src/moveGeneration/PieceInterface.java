package moveGeneration;

import java.util.List;

import board.Move;
import board.Position;

/**
 * This interface defines core behavior that we should expect of pieces
 */ 
public interface PieceInterface {
	/**
	 * Returns a bitboard of the squares that a piece on that square attacks.
	 * @Param square
	 * @Return attacks bitboard
	 */
	public abstract long getAttacks(int square);
	
	/**
	 * Returns a list of moves that a piece type can move to in a pseudo legal way
	 * Includes enPassant and castles regardless of other factors.
	 * @Param square
	 * @Param position
	 */
	public abstract List<Move> generateMoves(int square, Position position);
}
