package board;

/**
 * Represents a position with Bitboards
 */
public class Position {
	public enum PieceType {
		PAWN,
		ROOK,
		KNIGHT,
		BISHOP,
		QUEEN,
		KING,
	}
	
	
	public final long occupancy; //indicates if a square that is occupied
	public final long initialPos; //indicates pieces that haven't moved since the start
	public final long whitePieces;
	public final long blackPieces;
	
	public final long pawns;
	public final long rooks;
	public final long knights;
	public final long bishops;
	public final long queens;
	public final long kings;
	
	
	
	public final long whiteAttackMap;
	public final long blackAttackMap;
	
	public final Move priorMove;
	public final boolean whiteToPlay;
	
	//Creates a position equal to the starting position
	public Position() {
		occupancy = 0b11111111_11111111_00000000_00000000_00000000_00000000_11111111_11111111L;
		initialPos = 0b11111111_11111111_00000000_00000000_00000000_00000000_11111111_11111111L;
		whitePieces = 0b00000000_00000000_00000000_00000000_00000000_00000000_11111111_11111111L;
		blackPieces = 0b11111111_11111111_00000000_00000000_00000000_00000000_00000000_00000000L;
		
		
		pawns = 0b00000000_11111111_00000000_00000000_00000000_00000000_11111111_00000000L;
		rooks = 0b10000001_00000000_00000000_00000000_00000000_00000000_00000000_10000001L;
		knights = 0b01000010_00000000_00000000_00000000_00000000_00000000_00000000_01000010L;
		bishops = 0b00100100_00000000_00000000_00000000_00000000_00000000_00000000_00100100L;
		queens = 0b00001000_00000000_00000000_00000000_00000000_00000000_00000000_00001000L;
		kings = 0b00010000_00000000_00000000_00000000_00000000_00000000_00000000_00010000L;
		
		
		whiteAttackMap = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
		blackAttackMap = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
		
		priorMove = null;
		whiteToPlay = true;
	}
	
	//
	//public Position(Move move) {
	//	
	//}

}

