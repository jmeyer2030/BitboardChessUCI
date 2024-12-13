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
	
	//Board Locations
	public final long occupancy;
	public final long whitePieces; 
	public final long blackPieces;
	
	//Pieces
	public final long pawns;
	public final long rooks;
	public final long knights;
	public final long bishops;
	public final long queens;
	public final long kings;
	
	//Attacks
	public final long[] attackArray;
	public final long whiteAttackMap;
	public final long blackAttackMap;
	
	//Details
	public final boolean whiteToPlay;
	public final byte castleRights;//Castle Rights: 0b0000(whiteQueen)(whiteKing)(blackQueen)(blackKing)
	public final int enPassant;//Is non-zero if that square can be captured enPassant
	
	//Creates a position equal to the starting position
	public Position() {
		//Board Locations
		occupancy = 0b11111111_11111111_00000000_00000000_00000000_00000000_11111111_11111111L;
		whitePieces = 0b00000000_00000000_00000000_00000000_00000000_00000000_11111111_11111111L;
		blackPieces = 0b11111111_11111111_00000000_00000000_00000000_00000000_00000000_00000000L;
		
		//Pieces
		pawns = 0b00000000_11111111_00000000_00000000_00000000_00000000_11111111_00000000L;
		rooks = 0b10000001_00000000_00000000_00000000_00000000_00000000_00000000_10000001L;
		knights = 0b01000010_00000000_00000000_00000000_00000000_00000000_00000000_01000010L;
		bishops = 0b00100100_00000000_00000000_00000000_00000000_00000000_00000000_00100100L;
		queens = 0b00001000_00000000_00000000_00000000_00000000_00000000_00000000_00001000L;
		kings = 0b00010000_00000000_00000000_00000000_00000000_00000000_00000000_00010000L;
		
		//Details
		whiteToPlay = true;
		castleRights = 0b00001111;
		enPassant = 0;
		
		//Attacks
		attackArray = MoveGenerator.generateAttackArray(this);
		whiteAttackMap = generateWhiteAttackMap();
		blackAttackMap = generateBlackAttackMap();
	}
	
	public Position(FEN fen) {
	    // Initialize bitboards for occupancy and individual pieces
	    long occupancy = 0L;
	    long whitePieces = 0L;
	    long blackPieces = 0L;
	    long pawns = 0L;
	    long rooks = 0L;
	    long knights = 0L;
	    long bishops = 0L;
	    long queens = 0L;
	    long kings = 0L;

	    // Parse piece placement
	    String[] ranks = fen.piecePlacement.split("/");
	    for (int rank = 0; rank < 8; rank++) { // Iterate over ranks from 8th to 1st
	        String currentRank = ranks[rank];
	        int file = 0;

	        for (char c : currentRank.toCharArray()) {
	            if (Character.isDigit(c)) {
	                // Empty squares
	                file += c - '0';
	            } else {
	                // Piece on the square
	                int square = rank * 8 + file; // Convert rank and file to square index
	                long squareBit = 1L << square;
	                occupancy |= squareBit;

	                switch (c) {
	                    case 'P':
	                        pawns |= squareBit;
	                        whitePieces |= squareBit;
	                        break;
	                    case 'p':
	                        pawns |= squareBit;
	                        blackPieces |= squareBit;
	                        break;
	                    case 'R':
	                        rooks |= squareBit;
	                        whitePieces |= squareBit;
	                        break;
	                    case 'r':
	                        rooks |= squareBit;
	                        blackPieces |= squareBit;
	                        break;
	                    case 'N':
	                        knights |= squareBit;
	                        whitePieces |= squareBit;
	                        break;
	                    case 'n':
	                        knights |= squareBit;
	                        blackPieces |= squareBit;
	                        break;
	                    case 'B':
	                        bishops |= squareBit;
	                        whitePieces |= squareBit;
	                        break;
	                    case 'b':
	                        bishops |= squareBit;
	                        blackPieces |= squareBit;
	                        break;
	                    case 'Q':
	                        queens |= squareBit;
	                        whitePieces |= squareBit;
	                        break;
	                    case 'q':
	                        queens |= squareBit;
	                        blackPieces |= squareBit;
	                        break;
	                    case 'K':
	                        kings |= squareBit;
	                        whitePieces |= squareBit;
	                        break;
	                    case 'k':
	                        kings |= squareBit;
	                        blackPieces |= squareBit;
	                        break;
	                }
	                file++;
	            }
	        }
	    }

	    // Parse active color
	    boolean whiteToPlay = (fen.activeColor == 'w');

	    // Parse castling rights
	    byte castleRights = 0;
	    if (fen.castlingAvailible.contains("K")) castleRights |= 0b0100;
	    if (fen.castlingAvailible.contains("Q")) castleRights |= 0b1000;
	    if (fen.castlingAvailible.contains("k")) castleRights |= 0b0001;
	    if (fen.castlingAvailible.contains("q")) castleRights |= 0b0010;

	    // Parse en passant square
	    int enPassant = 0;
	    if (!fen.enPassant.equals("-")) {
	        char fileChar = fen.enPassant.charAt(0);
	        char rankChar = fen.enPassant.charAt(1);
	        int file = fileChar - 'a';
	        int rank = rankChar - '1';
	        enPassant = rank * 8 + file;
	    }

	    // Assign values to fields
	    this.occupancy = occupancy;
	    this.whitePieces = whitePieces;
	    this.blackPieces = blackPieces;
	    this.pawns = pawns;
	    this.rooks = rooks;
	    this.knights = knights;
	    this.bishops = bishops;
	    this.queens = queens;
	    this.kings = kings;
	    this.whiteToPlay = whiteToPlay;
	    this.castleRights = castleRights;
	    this.enPassant = enPassant;
	    this.attackArray = MoveGenerator.generateAttackArray(this);
	    this.whiteAttackMap = MoveGenerator.generateWhiteAttacks(this);
	    this.blackAttackMap = MoveGenerator.generateBlackAttacks(this);
	}
	
	
	public Position(Position position, Move move) {
	    // Copy current state
	    long occupancy = position.occupancy;
	    long whitePieces = position.whitePieces;
	    long blackPieces = position.blackPieces;
	    long pawns = position.pawns;
	    long rooks = position.rooks;
	    long knights = position.knights;
	    long bishops = position.bishops;
	    long queens = position.queens;
	    long kings = position.kings;

	    boolean whiteToPlay = position.whiteToPlay;
	    byte castleRights = position.castleRights;
	    int enPassant = 0; // Reset unless updated by an en passant move

	    // Extract move details
	    int startSquare = move.start;
	    int destinationSquare = move.destination;
	    long startMask = 1L << startSquare;
	    long destinationMask = 1L << destinationSquare;

	    // Toggle the moving piece's start and destination
	    occupancy ^= (startMask | destinationMask);
	    if (whiteToPlay) {
	        whitePieces ^= (startMask | destinationMask);
	    } else {
	        blackPieces ^= (startMask | destinationMask);
	    }

	    // Handle specific piece types
	    switch (move.moveType) {
	        case QUIET:
	            // Update the relevant piece bitboard
	            if ((pawns & startMask) != 0) {
	                pawns ^= (startMask | destinationMask);
	            } else if ((rooks & startMask) != 0) {
	                rooks ^= (startMask | destinationMask);
	            } else if ((knights & startMask) != 0) {
	                knights ^= (startMask | destinationMask);
	            } else if ((bishops & startMask) != 0) {
	                bishops ^= (startMask | destinationMask);
	            } else if ((queens & startMask) != 0) {
	                queens ^= (startMask | destinationMask);
	            } else if ((kings & startMask) != 0) {
	                kings ^= (startMask | destinationMask);
	            }
	            break;

	        case CAPTURE:
	            // Remove the captured piece from its color's bitboard
	            if (whiteToPlay) {
	                blackPieces ^= destinationMask;
	            } else {
	                whitePieces ^= destinationMask;
	            }

	            // Remove captured piece from its piece's bitboard
	            if ((pawns & destinationMask) != 0) {
	                pawns ^= destinationMask;
	            } else if ((rooks & destinationMask) != 0) {
	                rooks ^= destinationMask;
	            } else if ((knights & destinationMask) != 0) {
	                knights ^= destinationMask;
	            } else if ((bishops & destinationMask) != 0) {
	                bishops ^= destinationMask;
	            } else if ((queens & destinationMask) != 0) {
	                queens ^= destinationMask;
	            }
	            
	            // Toggle movement on its piece's bitboard
	            if ((pawns & startMask) != 0) {
	                pawns ^= (startMask | destinationMask);
	            } else if ((rooks & startMask) != 0) {
	                rooks ^= (startMask | destinationMask);
	            } else if ((knights & startMask) != 0) {
	                knights ^= (startMask | destinationMask);
	            } else if ((bishops & startMask) != 0) {
	                bishops ^= (startMask | destinationMask);
	            } else if ((queens & startMask) != 0) {
	                queens ^= (startMask | destinationMask);
	            }
	            break;

	        case ENPASSANT:
	            // Remove the pawn captured en passant
	            int enPassantCaptureSquare = position.enPassant;
	            long enPassantCaptureMask = 1L << enPassantCaptureSquare;

	            occupancy ^= enPassantCaptureMask;
	            if (whiteToPlay) {
	                blackPieces ^= enPassantCaptureMask;
	                pawns ^= enPassantCaptureMask;
	            } else {
	                whitePieces ^= enPassantCaptureMask;
	                pawns ^= enPassantCaptureMask;
	            }

	            // Move the pawn
	            pawns ^= (startMask | destinationMask);
	            break;

	        case PROMOTION:
	            // Remove the pawn and add the promoted piece
	            pawns ^= startMask;
	            switch (move.pieceType) {
	                case ROOK: rooks ^= destinationMask; break;
	                case KNIGHT: knights ^= destinationMask; break;
	                case BISHOP: bishops ^= destinationMask; break;
	                case QUEEN: queens ^= destinationMask; break;
	            }

	            // Update occupancy
	            occupancy ^= destinationMask;
	            break;

	        case CASTLE:
	            // Move the king
	            kings ^= (startMask | destinationMask);

	            // Move the rook based on which side castled
	            if (destinationSquare == 6 || destinationSquare == 62) { // King-side castle
	                int rookStart = whiteToPlay ? 7 : 63;
	                int rookEnd = whiteToPlay ? 5 : 61;
	                long rookStartMask = 1L << rookStart;
	                long rookEndMask = 1L << rookEnd;
	                rooks ^= (rookStartMask | rookEndMask);
	                occupancy ^= (rookStartMask | rookEndMask);
	            } else if (destinationSquare == 2 || destinationSquare == 58) { // Queen-side castle
	                int rookStart = whiteToPlay ? 0 : 56;
	                int rookEnd = whiteToPlay ? 3 : 59;
	                long rookStartMask = 1L << rookStart;
	                long rookEndMask = 1L << rookEnd;
	                rooks ^= (rookStartMask | rookEndMask);
	                occupancy ^= (rookStartMask | rookEndMask);
	            }
	            break;
	    }

	    // Update castling rights if necessary
	    if ((startMask & kings) != 0) {
	        if (whiteToPlay) {
	            castleRights &= 0b1100; // Remove white's castling rights
	        } else {
	            castleRights &= 0b0011; // Remove black's castling rights
	        }
	    }
	    if ((startMask & rooks) != 0) {
	        if (startSquare == 0) castleRights &= ~0b1000; // Remove white queen-side
	        if (startSquare == 7) castleRights &= ~0b0100; // Remove white king-side
	        if (startSquare == 56) castleRights &= ~0b0010; // Remove black queen-side
	        if (startSquare == 63) castleRights &= ~0b0001; // Remove black king-side
	    }
	    if ((destinationMask & rooks) != 0) {
	        if (destinationSquare == 0) castleRights &= ~0b1000; // Remove white queen-side
	        if (destinationSquare == 7) castleRights &= ~0b0100; // Remove white king-side
	        if (destinationSquare == 56) castleRights &= ~0b0010; // Remove black queen-side
	        if (destinationSquare == 63) castleRights &= ~0b0001; // Remove black king-side
	    }

	    // Set en passant square if applicable
	    if ((pawns & destinationMask) != 0 && Math.abs(startSquare - destinationSquare) == 16) {
	        enPassant = destinationSquare; //whiteToPlay ? destinationSquare - 8 : destinationSquare + 8;
	    }

	    // Switch active player
	    whiteToPlay = !whiteToPlay;

	    // Assign updated values to fields
	    this.occupancy = occupancy;
	    this.whitePieces = whitePieces;
	    this.blackPieces = blackPieces;
	    this.pawns = pawns;
	    this.rooks = rooks;
	    this.knights = knights;
	    this.bishops = bishops;
	    this.queens = queens;
	    this.kings = kings;
	    this.whiteToPlay = whiteToPlay;
	    this.castleRights = castleRights;
	    this.enPassant = enPassant;
	    this.attackArray = MoveGenerator.generateAttackArray(this);
	    this.whiteAttackMap = MoveGenerator.generateWhiteAttacks(this);
	    this.blackAttackMap = MoveGenerator.generateBlackAttacks(this);
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
		if (whiteToPlay) { //return if black is already in check
			return ((whiteAttackMap & kings & blackPieces) != 0L);
		} else {
			return ((blackAttackMap & kings & whitePieces) != 0L);
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

