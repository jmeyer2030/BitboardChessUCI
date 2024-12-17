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
	public long whitePieces; 
	public long blackPieces;
	public long[] pieces; // This stores all piece BBs at the PieceType.ordinal() position

	//State:
	public boolean whiteToPlay;
	public byte castleRights;//Castle Rights: 0b0000(whiteQueen)(whiteKing)(blackQueen)(blackKing)
	public int enPassant;//Same as fen, is the location where the pawn would be if it advanced one square.
	//public int gameStatus;//-1 if black win, 0, if staleMate, 1 if white win. 2 if ongoing
	public int rule50;
	public int fullMoveCount;

	//Attack Maps:
/*
* Constructors
*/
	/**
	* Build starting position
	*/
	public Position() {

		pieces = new long[6];
		//Piece Locations:
		occupancy = 0b11111111_11111111_00000000_00000000_00000000_00000000_11111111_11111111L;
		whitePieces = 0b00000000_00000000_00000000_00000000_00000000_00000000_11111111_11111111L;
		blackPieces = 0b11111111_11111111_00000000_00000000_00000000_00000000_00000000_00000000L;

		pieces[0] = 0b00000000_11111111_00000000_00000000_00000000_00000000_11111111_00000000L;
		pieces[1] = 0b01000010_00000000_00000000_00000000_00000000_00000000_00000000_01000010L;
		pieces[2] = 0b00100100_00000000_00000000_00000000_00000000_00000000_00000000_00100100L;
		pieces[3] = 0b10000001_00000000_00000000_00000000_00000000_00000000_00000000_10000001L;
		pieces[4] = 0b00001000_00000000_00000000_00000000_00000000_00000000_00000000_00001000L;
		pieces[5] = 0b00010000_00000000_00000000_00000000_00000000_00000000_00000000_00010000L;

		//State:
		whiteToPlay = true;
		castleRights = 0b00001111;
		enPassant = 0;
		rule50 = 0;
		fullMoveCount = 0;
	}
	/**
	* Copy a position
	*/
	public Position(Position position) {
		this.occupancy = position.occupancy;
		this.whitePieces = position.whitePieces;
		this.blackPieces = position.blackPieces;
		this.pieces = Arrays.copyOf(position.pieces, 6);
		this.whiteToPlay = position.whiteToPlay;
		this.castleRights = position.castleRights;
		this.enPassant = position.enPassant;
		this.rule50 = position.rule50;
		this.fullMoveCount = position.fullMoveCount;
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
	    
	    //Game Status
		fullMoveCount = fen.fullMoves;
		
	    // Assign values to fields

	    //Piece Locations:
	    this.occupancy = occupancy;
	    this.whitePieces = whitePieces;
	    this.blackPieces = blackPieces;
		this.pieces = new long[] {pawns, knights, bishops, rooks, queens, kings};

	    //State:
	    this.whiteToPlay = whiteToPlay;
	    this.castleRights = castleRights;
	    this.enPassant = enPassant;

	    //Attack Maps:
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
		whitePieces ^= whiteToPlay ? swapMask : 0L;
		blackPieces ^= whiteToPlay ? 0L : swapMask;

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
	            int enPassantCaptureSquare = whiteToPlay ? enPassant - 8 : enPassant + 8;
	            long enPassantCaptureMask = 1L << enPassantCaptureSquare;

	            occupancy &= ~enPassantCaptureMask;
				blackPieces &= ~enPassantCaptureMask;
				whitePieces &= ~enPassantCaptureMask;
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
						? (whiteToPlay ? 7 : 63) //king side
						: (whiteToPlay ? 0 : 56); //queen side
				int rookEnd = (move.destination == 6 || move.destination == 62)
						? rookStart - 2 //king side
						: rookStart + 3; //queen side
				long rookMoveMask = (1L << rookStart) | (1L << rookEnd);
				pieces[3] ^= rookMoveMask;
				occupancy ^= rookMoveMask;
				whitePieces ^= whiteToPlay ? rookMoveMask : 0;
				blackPieces ^= whiteToPlay ? 0 : rookMoveMask;
	            break;
	    }

	    // Update castle rights for king move
	    castleRights &= (byte) (destinationMask & pieces[5]) != 0 ?
                (byte) (whiteToPlay ? 0b1100 : 0b0011) :
	    	0b1111;

	    // Update castle rights for rook move
	    castleRights &= (move.movePiece == PieceType.ROOK) ? //If a rook move, we change rights depending on start
                (byte) (move.start == 0 ? 0b1000 :
                        move.start == 7 ? 0b0100 :
                        move.start == 56 ? 0b0010 :
						move.start == 63 ? 0b0001 : 0b1111)
						: 0b1111;

	    // Set en passant square
	    enPassant = ((pieces[0] & destinationMask) != 0 && Math.abs(move.start - move.destination) == 16) ?
	    	whiteToPlay ? move.destination - 8 : move.destination + 8 : 0;


	    //increment moveCounter
	    fullMoveCount += whiteToPlay ? 0 : 1;

	    // Switch active player
	    whiteToPlay = !whiteToPlay;
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
		this.whiteToPlay = !whiteToPlay;

		// Change occupancy of moved piece (special moves/captures handled later)
		occupancy &= ~destinationMask; //remove destination
		occupancy |= startMask; //add start

		// Change color pieces of moved piece
		whitePieces ^= whiteToPlay ? swapMask : 0L; //just brings moved piece to start and removes destination
		blackPieces ^= whiteToPlay ? 0L : swapMask;

		// Add piece on start square
		pieces[move.movePiece.ordinal()] |= startMask;

		// Remove piece on destination square
		pieces[move.movePiece.ordinal()] &= ~destinationMask;

		// Add precious occupant of the destination square (this should handle promotion captures
		if (move.captureType != null) {
			occupancy |= destinationMask;
			whitePieces |= whiteToPlay ? 0L : destinationMask;
			blackPieces |= whiteToPlay ? destinationMask : 0L;
			pieces[move.captureType.ordinal()] |= destinationMask;
		}

		// Handle specific piece types
		switch (move.moveType) {
			case QUIET:
			case CAPTURE:
				break;
			case ENPASSANT:
				// Replace pawn
				int enPassantCaptureSquare = whiteToPlay ? move.destination - 8 : move.destination + 8;
				long enPassantCaptureMask = 1L << enPassantCaptureSquare;

				this.enPassant = move.destination;

				//add the pawn back
				occupancy ^= enPassantCaptureMask; //add the pawn back
				pieces[0] ^= enPassantCaptureMask;
				whitePieces |= whiteToPlay ? 0L : enPassantCaptureMask;
				blackPieces |= whiteToPlay ? enPassantCaptureMask : 0L;
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
						? (whiteToPlay ? 7 : 63) //king side
						: (whiteToPlay ? 0 : 56); //queen side
				int rookEnd = (move.destination == 6 || move.destination == 62)
						? rookStart - 2 //king side
						: rookStart + 3; //queen side
				long rookMoveMask = (1L << rookStart) | (1L << rookEnd);
				pieces[3] ^= rookMoveMask;
				occupancy ^= rookMoveMask;
				whitePieces ^= whiteToPlay ? rookMoveMask : 0L;
				blackPieces ^= whiteToPlay ? 0L : rookMoveMask;
	            break;
		}


		// Update castling rights
		this.castleRights = move.castleRights;

		// Set en passant square if applicable
		if ((pieces[0] & destinationMask) != 0 && Math.abs(move.start - move.destination) == 16) {
			enPassant = whiteToPlay ? move.destination - 8 : move.destination + 8;
		}

		// Update 50 move rule
		this.rule50 = move.halfMoveCount;

		//increment moveCounter
		if (!whiteToPlay)
			fullMoveCount--;
	}

/*
* Helper Methods:
*/

	/**
	* Checks and returns if the non-active player is in check
	* @return if non-active player is in check
	*/
	public boolean selfInCheck() {
		return false;
		//return MoveGenerator.kingInCheck(this);
	}

	/**
	* generates and returns white's attack map
	* @return white attack bitboard
	*/
	public long generateWhiteAttackMap() {
		long result = 0L;
		long bitboard = this.whitePieces;
		while (bitboard != 0) {
			// Find the position of the least significant 1 bit
			int square = Long.numberOfTrailingZeros(bitboard);
			// Add the position to the list
			result |= MoveGenerator.getAttacks(this, square);
			// Remove the least significant 1 bit
			bitboard &= (bitboard - 1);
		}
		return result;
	}

	/**
	 * generates and returns black's attack map
	 * @return black attack bitboard
	 */
	public long generateBlackAttackMap() {
		long result = 0L;
		long bitboard = this.blackPieces;
		while (bitboard != 0) {
			// Find the position of the least significant 1 bit
			int square = Long.numberOfTrailingZeros(bitboard);
			// Add the position to the list
			result |= MoveGenerator.getAttacks(this, square);
			// Remove the least significant 1 bit
			bitboard &= (bitboard - 1);
		}
		return result;
	}




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
		Testing.printBoard(whitePieces);

		System.out.println("Black Pieces: ");
		Testing.printBoard(blackPieces);

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

		System.out.println("HalfMoveCount: " + rule50);
		System.out.println("FullMoveCount: " + fullMoveCount);
	}
}

/*
LEGACY CODE:
	public Position(Position position, Move move) {
	    // init copies
	    long occupancy = position.occupancy;
	    long whitePieces = position.whitePieces;
	    long blackPieces = position.blackPieces;
	    long pawns = position.pieces[0];
	    long rooks = position.pieces[3];
	    long knights = position.pieces[1];
	    long bishops = position.pieces[2];
	    long queens = position.pieces[4];
	    long kings = position.pieces[5];

	    boolean whiteToPlay = position.whiteToPlay;
	    byte castleRights = position.castleRights;
	    int enPassant = 0; // Reset unless updated by an en passant move

	    int halfMoveCount = position.halfMoveCount;
	    int fullMoveCount = position.fullMoveCount;

	    long checkers = 0L;

	    long[] attackArray = Arrays.copyOf(position.attackArray, 64);
	    List<MoveDetails> moveDetails = position.moveDetails.stream().collect(Collectors.toCollection(LinkedList::new));


	    // Extract move details
	    int startSquare = move.start;
	    int destinationSquare = move.destination;
	    long startMask = 1L << startSquare;
	    long destinationMask = 1L << destinationSquare;

	    moveDetails.add(new MoveDetails(move, this.castleRights, null, 0));

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
	        case CHECK:
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
	        	occupancy ^= destinationMask;

	            // Remove the captured piece from its color's bitboard
	            if (whiteToPlay) {
	                blackPieces ^= destinationMask;
	            } else {
	                whitePieces ^= destinationMask;
	            }

	            // Remove captured piece from its piece's bitboard
	            if ((pawns & destinationMask) != 0) {
	            	moveDetails.getLast().captureType = PieceType.PAWN;
	                pawns ^= destinationMask;
	            } else if ((rooks & destinationMask) != 0) {
	            	moveDetails.getLast().captureType = PieceType.ROOK;
	                rooks ^= destinationMask;
	            } else if ((knights & destinationMask) != 0) {
	            	moveDetails.getLast().captureType = PieceType.KNIGHT;
	                knights ^= destinationMask;
	            } else if ((bishops & destinationMask) != 0) {
	            	moveDetails.getLast().captureType = PieceType.BISHOP;
	                bishops ^= destinationMask;
	            } else if ((queens & destinationMask) != 0) {
	            	moveDetails.getLast().captureType = PieceType.QUEEN;
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
	            } else if ((kings & startMask) != 0) {
	            	kings ^= (startMask | destinationMask);
	            }
	            break;

	        case ENPASSANT:
	            // Remove the pawn captured en passant
	            int enPassantCaptureSquare = position.whiteToPlay ? position.enPassant - 8: position.enPassant + 8;
	            long enPassantCaptureMask = 1L << enPassantCaptureSquare;

	            //update attackArray
	            attackArray[enPassantCaptureSquare] = 0L;

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
	            switch (move.promotionType) {
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
	                attackArray[rookStart] = 0L;
	                int rookEnd = whiteToPlay ? 5 : 61;
	                long rookStartMask = 1L << rookStart;
	                long rookEndMask = 1L << rookEnd;
	                rooks ^= (rookStartMask | rookEndMask);
	                occupancy ^= (rookStartMask | rookEndMask);
	            } else if (destinationSquare == 2 || destinationSquare == 58) { // Queen-side castle
	                int rookStart = whiteToPlay ? 0 : 56;
	                attackArray[rookStart] = 0L;
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
	        enPassant = whiteToPlay ? destinationSquare - 8 : destinationSquare + 8;
	    }



	    //Set HalfMoveCount
	    if ((pawns & destinationMask) != 0)
	    	halfMoveCount = 0;

	    // Switch active player
	    whiteToPlay = !whiteToPlay;

	    //increment moveCounter
	    if (!position.whiteToPlay)
	    	fullMoveCount++;




	    //whiteAttackMap = this.generateWhiteAttackMap();
	    //blackAttackMap = this.generateBlackAttackMap();

	    // Assign updated values to fields
	    this.occupancy = occupancy;
	    this.whitePieces = whitePieces;
	    this.blackPieces = blackPieces;
	    this.pieces[0] = pawns;
	    this.pieces[3] = rooks;
	    this.pieces[1] = knights;
	    this.pieces[2] = bishops;
	    this.pieces[4] = queens;
	    this.pieces[5] = kings;
	    this.whiteToPlay = whiteToPlay;
	    this.castleRights = castleRights;
	    this.enPassant = enPassant;
	    this.fullMoveCount = fullMoveCount;
	    this.halfMoveCount = halfMoveCount;
	    this.moveDetails = moveDetails;
	    //update attack array
	    attackArray[move.destination] = MoveGenerator.getAttacks(this, move.destination);//update at destination
	    attackArray[move.start] = 0L;

	    BBO.getSquares(rooks | bishops | queens).stream().forEach(square -> {
	    	attackArray[square] = MoveGenerator.getAttacks(this, square);

	    });


	    //updateCheckers
		long kingMask = this.pieces[5] & (this.whiteToPlay ? this.whitePieces : this.blackPieces);
		long enemyPieces = this.whiteToPlay ? this.blackPieces : this.whitePieces;

		//change checkers if attacked by the destination
		if ((attackArray[move.destination] & kingMask) != 0)
			checkers |= (1L << move.destination);

		//update sliding pieces
	    for (int square : BBO.getSquares(enemyPieces & (rooks | bishops | queens))) {//covers discovered checks
	    	if ((attackArray[square] & kingMask) != 0) {
	    		checkers |= (1L << square);
	    	}
	    }

	    this.attackArray = attackArray;
	    this.whiteAttackMap = this.generateWhiteAttackMap();
	    this.blackAttackMap = this.generateBlackAttackMap();
	    this.gameStatus = generateGameStatus();

	    this.moveScope = generateMoveScope();
	    this.checkers = checkers;
	}


	public long[] generateMoveScope() {
		//init moveScope assuming no pins
		long[] moveScope = new long[64];
		for (int i = 0; i < 64; i++) {
			moveScope[i] = -1L;
		}
		long enemyMask = this.whiteToPlay ? this.blackPieces : this.whitePieces;
		for (int pinner : BBO.getSquares(enemyMask & (bishops | queens | rooks))) {//for each possible pinner
			long kingMask = this.pieces[5] & (this.whiteToPlay ? this.whitePieces : this.blackPieces);
			long xrayAttacks = MoveGenerator.getXrayAttacks(this, pinner);
			if ((xrayAttacks & kingMask) == 0L)//if xray doesn't hit king
				continue;
			int kingLoc = BBO.getSquares(kingMask).get(0);

			long possibleBlockerMask = AbsolutePins.inBetween[pinner][kingLoc];
			long blockers = this.whiteToPlay ? this.whitePieces : this.blackPieces;
			long blockerMask = blockers & possibleBlockerMask;

			int pinnedPieceLoc = BBO.getSquares(blockerMask).get(0);
			moveScope[pinnedPieceLoc] = AbsolutePins.inBetween[pinner][kingLoc] | (1L << pinner);
		}
		return moveScope;
	}

		public Position applyMove(Move move) {
		Position result = new Position(this, move);
		return result;
	}
	* Generates and returns checkers
	* @return BB of piece locations attacking active player's king
public long generateCheckers() {
	long checkers = 0L;
	long pieceMask = this.whiteToPlay ? this.blackPieces : this.whitePieces;
	long attackMask = this.whiteToPlay ? this.blackAttackMap : this.whiteAttackMap;
	long kingMask = this.pieces[5] & (this.whiteToPlay ? this.whitePieces : this.blackPieces);
	if ((attackMask & kingMask) == 0) //if not in check
		return checkers;

	for (int square : BBO.getSquares(pieceMask)) { //for each potential checker
		if ((MoveGenerator.getAttacks(this, square) & kingMask) != 0)
			checkers |= (1L << square);
	}
	return checkers;
}

	* Returns if a square is attacked by the non-active player
	* @param square square to check attacked
	* @return if square is attacked by non-active player
public boolean squareAttacked(int square) {
	if (whiteToPlay) {
		return (blackAttackMap & (1L << square)) != 0;
	} else {
		return (whiteAttackMap & (1L << square)) != 0;
	}
}

* checks for checkmate and draw
	public int generateGameStatus() {
		if (rule50 >= 50)
			return 0;
		if (selfInCheck())
			return whiteToPlay ? -1 : 1;
		return 2;
	}
	*/