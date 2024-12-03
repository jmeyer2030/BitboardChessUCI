package board;

public class MoveDetails {
	public Move move;
	public byte castleRights;
	public PieceType captureType;
	public int halfMoveCount;
	
	public MoveDetails(Move move, byte castleRights, PieceType captureType, int halfMoveCount) {
		this.move = move;
		this.castleRights = castleRights;
		this.captureType = captureType;
		this.halfMoveCount = halfMoveCount;
	}
}
