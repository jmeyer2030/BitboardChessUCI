package board;

import moveGeneration.MoveGenerator;
import moveGeneration.Testing;

import java.util.Arrays;

/*
 * Represents a position with Bitboards
 */
public class Position {
	//Piece Locations
	public long occupancy;
	public long[] pieceColors;
	public long[] pieces; // This stores all piece BBs at the PieceType.ordinal() position

	//State:
	public Color activePlayer;
	public byte castleRights; //Castle Rights: 0b0000(whiteQueen)(whiteKing)(blackQueen)(blackKing)
	public int enPassant; //Same as fen, is the location where the pawn would be if it advanced one square.
	public int rule50;
	public int fullMoveCount;
	public boolean whiteInCheck;
	public boolean blackInCheck;

/*
* Constructors
*/
	/**
	* Build starting position
	*/
	public Position() {
		//Piece Locations:
		occupancy = 0b11111111_11111111_00000000_00000000_00000000_00000000_11111111_11111111L;

		pieceColors = new long[2];
		pieceColors[0] = 0b00000000_00000000_00000000_00000000_00000000_00000000_11111111_11111111L;
		pieceColors[1] = 0b11111111_11111111_00000000_00000000_00000000_00000000_00000000_00000000L;

		pieces = new long[6];
		pieces[0] = 0b00000000_11111111_00000000_00000000_00000000_00000000_11111111_00000000L;
		pieces[1] = 0b01000010_00000000_00000000_00000000_00000000_00000000_00000000_01000010L;
		pieces[2] = 0b00100100_00000000_00000000_00000000_00000000_00000000_00000000_00100100L;
		pieces[3] = 0b10000001_00000000_00000000_00000000_00000000_00000000_00000000_10000001L;
		pieces[4] = 0b00001000_00000000_00000000_00000000_00000000_00000000_00000000_00001000L;
		pieces[5] = 0b00010000_00000000_00000000_00000000_00000000_00000000_00000000_00010000L;

		//State:
		activePlayer = Color.WHITE;
		castleRights = 0b00001111;
		enPassant = 0;
		rule50 = 0;
		fullMoveCount = 1;
		whiteInCheck = false;
		blackInCheck = false;
	}
	/**
	* Copy a position
	*/
	public Position(Position position) {
		this.occupancy = position.occupancy;
		this.pieceColors = Arrays.copyOf(position.pieceColors, 2);
		this.pieces = Arrays.copyOf(position.pieces, 6);
		this.activePlayer = position.activePlayer;
		this.castleRights = position.castleRights;
		this.enPassant = position.enPassant;
		this.rule50 = position.rule50;
		this.fullMoveCount = position.fullMoveCount;
		this.whiteInCheck = position.whiteInCheck;
		this.blackInCheck = position.blackInCheck;
	}
	/**
	* Build from FEN
	*/
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
	    for (int rank = 7; rank >= 0; rank--) { // Iterate over ranks from 8th to 1st
	        String currentRank = ranks[rank];
	        int file = 0;

	        for (char c : currentRank.toCharArray()) {
	            if (Character.isDigit(c)) {
	                // Empty squares
	                file += c - '0';
	            } else {
	                // Piece on the square
	                int square = (7 - rank) * 8 + file; // Convert rank and file to square index
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
	    Color activePlayer = fen.activeColor == 'w' ? Color.WHITE : Color.BLACK;

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
	    
	    //Game Status
		fullMoveCount = fen.fullMoves;
		
	    //Piece Locations:
	    this.occupancy = occupancy;
	    this.pieceColors = new long[] {whitePieces, blackPieces};
		this.pieces = new long[] {pawns, knights, bishops, rooks, queens, kings};

	    //State:
	    this.activePlayer = activePlayer;
	    this.castleRights = castleRights;
	    this.enPassant = enPassant;

	    this.whiteInCheck = MoveGenerator.kingInCheck(this, Color.WHITE);
	    this.blackInCheck = MoveGenerator.kingInCheck(this, Color.BLACK);
	}
/*
* Make and unMake
*/
	/**
	* Applies a move
	*/
	public void makeMove(Move move) {
		// Create bitmasks
	    long startMask = 1L << move.start;
	    long destinationMask = 1L << move.destination;
	    long swapMask = (startMask | destinationMask);

		// Increment rule50, reset handled later
		rule50++;

		// Change occupancy of moved piece
	    occupancy &= ~startMask; //remove start square
	    occupancy |= destinationMask; //add destination

		// Change color pieces of moved piece
		pieceColors[Color.flipColor(activePlayer).ordinal()] &= ~destinationMask;
		pieceColors[activePlayer.ordinal()] ^= swapMask;

		// Remove piece on destination square (case of capture)
		if (move.captureType != null)
			pieces[move.captureType.ordinal()] &= ~destinationMask;

		// Add piece on destination square
		pieces[move.movePiece.ordinal()] |= destinationMask;

		//  Remove piece on start square
		pieces[move.movePiece.ordinal()] &= ~startMask;

	    // Handle specific move types
	    switch (move.moveType) {
	        case QUIET:
	        	rule50 = (pieces[0] & destinationMask) != 0 ? 0 : rule50;
	        	break;
	        case CAPTURE:
	        	rule50 = 0;
	            break;
	        case ENPASSANT:
	        	rule50 = 0;
	            // Remove the pawn captured en passant
	            int enPassantCaptureSquare = activePlayer == Color.WHITE ? enPassant - 8 : enPassant + 8;
	            long enPassantCaptureMask = 1L << enPassantCaptureSquare;

	            occupancy &= ~enPassantCaptureMask;
				pieceColors[0] &= ~enPassantCaptureMask;
				pieceColors[1] &= ~enPassantCaptureMask;
                pieces[0] &= ~enPassantCaptureMask;
	            break;
	        case PROMOTION:
	        	rule50 = 0;
	            // Remove promotion square pawn and add promotion piece
	            pieces[0] &= ~destinationMask;
				pieces[move.promotionType.ordinal()] |= destinationMask;
	            break;
	        case CASTLE:
				// Move the rook
				int rookStart = (move.destination == 6 || move.destination == 62)
						? (activePlayer == Color.WHITE ? 7 : 63) //king side
						: (activePlayer == Color.WHITE ? 0 : 56); //queen side
				int rookEnd = (move.destination == 6 || move.destination == 62)
						? rookStart - 2 //king side
						: rookStart + 3; //queen side
				long rookMoveMask = (1L << rookStart) | (1L << rookEnd);
				pieces[3] ^= rookMoveMask;
				occupancy ^= rookMoveMask;
				pieceColors[activePlayer.ordinal()] ^= rookMoveMask;
	            break;
	    }

	    // Update castle rights for king move
	    castleRights &= ((destinationMask & pieces[5]) != 0) ?
                 (activePlayer == Color.WHITE ? (byte) 0b0000_0011 : (byte) 0b0000_1100) :
				(byte) 0b0000_1111;

	    // Update castle rights for rook move
	    castleRights &= (move.movePiece == PieceType.ROOK) ? //If a rook move, we change rights depending on start
                (byte) (move.start == 0 ? 0b0000_0111 :
                        move.start == 7 ? 0b0000_1011 :
                        move.start == 56 ? 0b0000_1101 :
						move.start == 63 ? 0b0000_1110 : 0b0000_1111)
						: 0b0000_1111;

		// Update castle rights for rook square move

	    // Set en passant square
	    enPassant = ((pieces[0] & destinationMask) != 0 && Math.abs(move.start - move.destination) == 16) ?
	    	activePlayer == Color.WHITE ? move.destination - 8 : move.destination + 8 : 0;


	    //increment moveCounter
	    fullMoveCount += activePlayer == Color.WHITE ? 0 : 1;

	    // Switch active player
	    activePlayer = activePlayer == Color.WHITE ? Color.BLACK : Color.WHITE;

	    whiteInCheck = move.resultWhiteInCheck;
	    blackInCheck = move.resultBlackInCheck;

	    looseValidatePosition(move);
	}

	/**
	 * Unmakes a move
	 */
	public void unMakeMove(Move move) {
		// Create bitmaps
		long startMask = (1L << move.start);
		long destinationMask = (1L << move.destination);
		long swapMask = (startMask | destinationMask);

		// Roughly validate the move
		assert (destinationMask & this.occupancy) != 0;
		assert (startMask & this.occupancy) == 0;

		// Change active player
		this.activePlayer = activePlayer == Color.WHITE ? Color.BLACK : Color.WHITE;

		// Change occupancy of moved piece (special moves/captures handled later)
		occupancy &= ~destinationMask; //remove destination
		occupancy |= startMask; //add start

		// Change color pieces of moved piece
		pieceColors[activePlayer.ordinal()] ^= swapMask;

		// Add piece on start square
		pieces[move.movePiece.ordinal()] |= startMask;

		// Remove piece on destination square
		pieces[move.movePiece.ordinal()] &= ~destinationMask;

		// Add precious occupant of the destination square (this should handle promotion captures
		if (move.captureType != null) {
			occupancy |= destinationMask;
			pieceColors[0] |= activePlayer == Color.WHITE ? 0L : destinationMask;
			pieceColors[1] |= activePlayer == Color.WHITE ? destinationMask : 0L;
			pieces[move.captureType.ordinal()] |= destinationMask;
		}

		// Handle specific piece types
		switch (move.moveType) {
			case QUIET:
			case CAPTURE:
				break;
			case ENPASSANT:
				// Replace pawn
				int enPassantCaptureSquare = activePlayer == Color.WHITE ? move.destination - 8 : move.destination + 8;
				long enPassantCaptureMask = 1L << enPassantCaptureSquare;

				this.enPassant = move.destination;

				//add the pawn back
				occupancy ^= enPassantCaptureMask; //add the pawn back
				pieces[0] ^= enPassantCaptureMask;
				pieceColors[0] |= activePlayer == Color.WHITE ? 0L : enPassantCaptureMask;
				pieceColors[1] |= activePlayer == Color.WHITE ? enPassantCaptureMask : 0L;
				break;
			case PROMOTION:
				//remove promoted piece
				pieces[move.promotionType.ordinal()] &= ~destinationMask;
				//at this point, the promotion piece (QKRB) is back on the start square and the occupancy/color is correct
				//just need to remove the piece and add a pawn.
				pieces[move.promotionType.ordinal()] &= ~startMask;
				pieces[0] |= startMask;
				break;
			case CASTLE:
				//king is back on start and it's occupancy/color is updated.
				//just need to move the rook back and update occupancy/color for it
				// Move the rook based on which side castled
				// Move the rook
				int rookStart = (move.destination == 6 || move.destination == 62)
						? (activePlayer == Color.WHITE ? 7 : 63) //king side
						: (activePlayer == Color.WHITE ? 0 : 56); //queen side
				int rookEnd = (move.destination == 6 || move.destination == 62)
						? rookStart - 2 //king side
						: rookStart + 3; //queen side
				long rookMoveMask = (1L << rookStart) | (1L << rookEnd);
				pieces[3] ^= rookMoveMask;
				occupancy ^= rookMoveMask;
				pieceColors[activePlayer.ordinal()] ^= rookMoveMask;
	            break;
		}


		// Update castling rights
		this.castleRights = move.castleRights;

		// Set en passant square
		this.enPassant = move.enPassant;

		// Update 50 move rule
		this.rule50 = move.halfMoveCount;

		//increment moveCounter
		if (activePlayer == Color.BLACK)
			fullMoveCount--;

		this.whiteInCheck = move.prevWhiteInCheck;
		this.blackInCheck = move.prevBlackInCheck;
	}

/*
* Helper Methods:
*/
	/**
	* Returns the PieceType on a square
	* @param square square
	* @return pieceType
	*/
	public PieceType getPieceType(int square) {
		long squareMask = (1L << square);
		PieceType pieceType = null;
		if ((this.pieces[0] & squareMask) != 0) {
			pieceType = PieceType.PAWN;
		} else if ((this.pieces[3] & squareMask) != 0) {
			pieceType = PieceType.ROOK;
		} else if ((this.pieces[1] & squareMask) != 0) {
			pieceType = PieceType.KNIGHT;
		} else if ((this.pieces[2] & squareMask) != 0) {
			pieceType = PieceType.BISHOP;
		} else if ((this.pieces[4] & squareMask) != 0) {
			pieceType = PieceType.QUEEN;
		} else if ((this.pieces[5] & squareMask) != 0) {
			pieceType = PieceType.KING;
		}

		return pieceType;
	}

	public boolean equals(Position position) {
		boolean equal = true;
		if (this.occupancy != position.occupancy) {
			System.out.println("occupancy diff");
			equal = false;
		}
		if (this.pieces[0] != position.pieces[0]) {
			System.out.println("pawn diff");
			equal = false;
		}
		if (this.pieces[1] != position.pieces[1]) {
			System.out.println("knight diff");
			equal = false;
		}
		if (this.pieces[2] != position.pieces[2]) {
			System.out.println("bishop diff");
			equal = false;
		}
		if (this.pieces[3] != position.pieces[3]) {
			System.out.println("rook diff");
			equal = false;
		}
		if (this.pieces[4] != position.pieces[4]) {
			System.out.println("queen diff");
			equal = false;
		}
		if (this.pieces[5] != position.pieces[5]) {
			System.out.println("king diff");
			equal = false;
		}
		if (this.pieceColors[0] != position.pieceColors[0]) {
			System.out.println("white diff");
			equal = false;
		}
		if (this.pieceColors[1] != position.pieceColors[1]) {
			System.out.println("black diff");
			equal = false;
		}
		if (this.castleRights != position.castleRights) {
			System.out.println("castle diff");
			equal = false;
		}
		if (this.rule50 != position.rule50) {
			System.out.println("rule50 diff");
			equal = false;
		}
		if (this.enPassant != position.enPassant) {
			System.out.println("en passant diff");
			equal = false;
		}
		if (this.activePlayer != position.activePlayer) {
			System.out.println("activePlayer diff");
			equal = false;
		}
		if (this.fullMoveCount != position.fullMoveCount) {
			System.out.println("full move diff");
			equal = false;
		}
		return equal;
	}

/*
* Visualization Method:
*/
	/**
	* Prints all bitboards
	*/
	public void printBoard() {
		System.out.println("Occupancy: ");
		Testing.printBoard(occupancy);

		System.out.println("White Pieces: ");
		Testing.printBoard(pieceColors[0]);

		System.out.println("Black Pieces: ");
		Testing.printBoard(pieceColors[1]);

		System.out.println("Pawns: ");
		Testing.printBoard(pieces[0]);

		System.out.println("Rooks: ");
		Testing.printBoard(pieces[3]);

		System.out.println("Bishops: ");
		Testing.printBoard(pieces[2]);

		System.out.println("Queens: ");
		Testing.printBoard(pieces[4]);

		System.out.println("Kings: ");
		Testing.printBoard(pieces[5]);

		System.out.println("Knights: ");
		Testing.printBoard(pieces[1]);

		System.out.println("Castle Rights" + Integer.toBinaryString((castleRights+256)%256));
		System.out.println("En Passant: " + enPassant);
		System.out.println("HalfMoveCount: " + rule50);
		System.out.println("FullMoveCount: " + fullMoveCount);
	}


	protected void looseValidatePosition(Move move) {
		long generatedOccupancy = 0L | pieces[0] | pieces[1] | pieces[2] | pieces[3] | pieces[4] | pieces[5];
		if (occupancy != generatedOccupancy) {
			System.out.println("Occupancy doesn't match!!!");
			printBoard();
			System.out.println(move.toString());
		}


	}
}
