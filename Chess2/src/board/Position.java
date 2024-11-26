package board;

import moveGeneration.MoveGenerator;
import system.BBO;

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
	
	
	public final long[] attackArray;
	
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
		
		attackArray = MoveGenerator.generateAttackArray(this);
		
		priorMove = null;
		whiteToPlay = true;
		
		whiteAttackMap = generateWhiteAttackMap();
		blackAttackMap = generateBlackAttackMap();
	}
	
	/**
	 * Remove the bit at destination from all boards.
	 * Remove the bit at start, then if it was removed, replace it at destination.
	 * 
	 * this.bitboard = (bitboard remove destination) removeStart
	 */
	public Position(Position position, Move move) {
		if (move.moveType == Move.MoveType.CAPTURE || move.moveType == Move.MoveType.QUIET) {
			this.occupancy = position.occupancy & ~(1L << move.start) | (1L << move.destination);
			this.whitePieces = position.whitePieces & ~(1L << move.start) | (position.whiteToPlay ? (1L << move.destination) : 0L);
			this.blackPieces = position.blackPieces & ~(1L << move.start) | (!position.whiteToPlay ? (1L << move.destination) : 0L);
			
			this.pawns = position.pawns & ~(1L << move.start) | (BBO.squareHasPiece(position.pawns, move.start) ? (1L << move.destination) : 0L);
			this.rooks = position.rooks & ~(1L << move.start) | (BBO.squareHasPiece(position.rooks, move.start) ? (1L << move.destination) : 0L);
			this.knights = position.knights & ~(1L << move.start) | (BBO.squareHasPiece(position.knights, move.start) ? (1L << move.destination) : 0L);
			this.bishops = position.bishops & ~(1L << move.start) | (BBO.squareHasPiece(position.bishops, move.start) ? (1L << move.destination) : 0L);
			this.queens = position.queens & ~(1L << move.start) | (BBO.squareHasPiece(position.queens, move.start) ? (1L << move.destination) : 0L);
			this.kings = position.kings & ~(1L << move.start) | (BBO.squareHasPiece(position.kings, move.start) ? (1L << move.destination) : 0L);
	
			this.initialPos = position.initialPos & ~(1L << move.start);
		} else if (move.moveType == Move.MoveType.CASTLE) {
			long removeStart = ~(1L << move.start);
			long addDestination = (1L << move.destination);
			int rookLoc = (move.destination / 8 == 0) ? (move.destination == 2 ? 0 : 7) : (move.destination == 58 ? 56 : 63);
			int rookDestination = (move.destination / 8 == 0) ? (move.destination == 2 ? 3 : 5) : (move.destination == 58 ? 59 : 61);
			long removeRook = ~(1L << rookLoc);
			long addRook = (1L << rookDestination);
			
			this.whitePieces = position.whitePieces & removeStart & removeRook | (BBO.squareHasPiece(position.whitePieces, move.start) ? addDestination | rookDestination: 0L);
			this.blackPieces = position.blackPieces & removeStart & removeRook | (BBO.squareHasPiece(position.blackPieces, move.start) ? addDestination | rookDestination: 0L);
			this.rooks = position.rooks & removeRook | addRook;
			this.kings = position.kings & removeStart | addDestination;
			this.occupancy = position.occupancy & ~(1L << move.start) | (1L << move.destination);
			
			this.pawns = position.pawns;
			this.knights = position.knights;
			this.bishops = position.bishops;
			this.queens = position.queens;
			
			
			this.initialPos = position.initialPos & ~(1L << move.start) & ~(1L << rookLoc);
		} else {
			this.occupancy = position.occupancy & ~(1L << move.start) | (1L << move.destination);
			//first remove the start, then if it contained the start we move it to destination.
			this.whitePieces = position.whitePieces & ~(1L << move.start) | (BBO.squareHasPiece(position.whitePieces, move.start) ? (1L << move.destination) : 0L);
			this.blackPieces = position.blackPieces & ~(1L << move.start) | (BBO.squareHasPiece(position.blackPieces, move.start) ? (1L << move.destination) : 0L);
			
			this.pawns = position.pawns & ~(1L << move.start) | (BBO.squareHasPiece(position.pawns, move.start) ? (1L << move.destination) : 0L);
			this.rooks = position.rooks & ~(1L << move.start) | (BBO.squareHasPiece(position.rooks, move.start) ? (1L << move.destination) : 0L);
			this.knights = position.knights & ~(1L << move.start) | (BBO.squareHasPiece(position.knights, move.start) ? (1L << move.destination) : 0L);
			this.bishops = position.bishops & ~(1L << move.start) | (BBO.squareHasPiece(position.bishops, move.start) ? (1L << move.destination) : 0L);
			this.queens = position.queens & ~(1L << move.start) | (BBO.squareHasPiece(position.queens, move.start) ? (1L << move.destination) : 0L);
			this.kings = position.kings & ~(1L << move.start) | (BBO.squareHasPiece(position.kings, move.start) ? (1L << move.destination) : 0L);
	
			this.initialPos = position.initialPos & ~(1L << move.start);
		}
		
		
		this.whiteToPlay = !position.whiteToPlay;
		this.priorMove = move;
		
		this.attackArray = position.attackArray;
		
		whiteAttackMap = MoveGenerator.generateWhiteAttacks(this);
		blackAttackMap = MoveGenerator.generateBlackAttacks(this);
		
	}
	
	public Position applyMove(Move move) {
		Position result = new Position(this, move);
		if (result.selfInCheck()) {
			//throw new IllegalStateException("Put self into check");
		}
		return result;
	}
	
	//assumes asking if the square is attacked by the non-active player
	public boolean squareAttacked(int square) {
		if (whiteToPlay) {
			return (blackAttackMap & (1L << square)) != 0;
		} else {
			return (whiteAttackMap & (1L << square)) != 0;
		}
	}
	
	public boolean selfInCheck() {
		if (whiteToPlay) {
			return ((blackAttackMap & kings & whitePieces) != 0L);
		} else {
			return ((whiteAttackMap & kings & blackPieces) != 0L);
		}
		
	}
	
	public long generateWhiteAttackMap() {
		long result = 0L;
		for (int square : BBO.getSquares(whitePieces)) {
			result |= attackArray[square];
		}
		return result;
	}
	
	public long generateBlackAttackMap() {
		long result = 0L;
		for (int square : BBO.getSquares(blackPieces)) {
			result |= attackArray[square];
		}
		return result;
	}

}

