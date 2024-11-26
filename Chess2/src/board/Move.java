package board;

public class Move {
	public enum MoveType {
		QUIET,
		CAPTURE,
		ENPASSANT,
		PROMOTION,
		CHECK,
		CASTLE,
	}
	public enum PieceType {
		ROOK,
		BISHOP,
		KNIGHT,
		QUEEN,
	}
	
	public final MoveType moveType;
	public final int start;
	public final int destination;
	public final long captureMask; //bitboard to and and remove a piece;
	public final PieceType pieceType;
	
	public Move(MoveType moveType, int start, int destination) {
		this.moveType = moveType;
		this.start = start;
		this.destination = destination;
		
		if (moveType == MoveType.CAPTURE) {
			captureMask = ~0L & (1L << destination);
		} else if (moveType == MoveType.ENPASSANT) {
			if (destination > start) {//if white enPassant
				this.captureMask = ((destination - start) == 7) ? ~0L ^ (1L << (start - 1)) : ~0L ^ (1L << (start + 1)); // if left enPassant
			} else {//if black enPassant
				this.captureMask = ((start - destination) == 9) ? ~0L ^ (1L << (start - 1)) : ~0L ^ (1L << (start + 1));
			}	
		} else {
			this.captureMask = ~0L;
		}
		
		this.pieceType = null;
	}
	
	public Move(MoveType moveType, int start, int destination, PieceType pieceType) {
		this.moveType = moveType;
		this.start = start;
		this.destination = destination;
		this.pieceType = pieceType;
		this.captureMask = ~0L & (1L << destination);
	}
	
	public void printMove() {
		System.out.println("Move start: " + this.start + "  Destination: " + this.destination + "  Type: " + this.moveType);
	}
}
