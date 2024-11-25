package board;

public class Move {
	public enum MoveType {
		QUIET,
		CAPTURE,
		ENPASSANT,
		PROMOTION,
		CHECK,
	}
	
	public final MoveType moveType;
	public final int start;
	public final int destination;
	
	public Move(MoveType moveType, int start, int destination) {
		this.moveType = moveType;
		this.start = start;
		this.destination = destination;
	}
}
