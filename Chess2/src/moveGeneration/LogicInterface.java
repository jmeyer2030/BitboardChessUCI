package moveGeneration;

import board.Position;

/**
 * This interface defines core behavior that we should expect of pieces
 */ 
public interface LogicInterface {
	/**
	 * Returns a bitboard of the squares that a piece on that square attacks.
	 * @Param square
	 * @Return attacks bitboard
	 */
	public abstract long getMoveBoard(int square, Position position);
	
	public abstract long getCaptures(int square, Position position);
	
	public abstract long getQuietMoves(int square, Position position);
	
	/**
	 * Returns a list of moves that a piece type can move to in a pseudo legal way
	 * Includes enPassant and castles regardless of other factors.
	 * @Param square
	 * @Param position
	 */
	public abstract long getAttackBoard(int square, Position position);
	
	public abstract void initializeAll();
}
