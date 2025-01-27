package board;

import moveGeneration.MoveGenerator;
import testing.testMoveGeneration.Testing;
import customExceptions.InvalidPositionException;
import zobrist.Hashing;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/*
 * Represents a position with Bitboards
 */
public class Position {
	public long zobristHash;

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

		zobristHash = Hashing.computeZobrist(this);
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
		this.zobristHash = position.zobristHash;
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

	    this.zobristHash = Hashing.computeZobrist(this);
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

		// Occupancy remove start add destination
	    occupancy &= ~startMask; //remove start square
	    occupancy |= destinationMask; //add destination

		// Inactive color remove destination
		pieceColors[Color.flipColor(activePlayer).ordinal()] &= ~destinationMask;

		// Active color remove start add destination
		pieceColors[activePlayer.ordinal()] ^= swapMask;

		// Pieces remove capture
		if (move.captureType != null)
			pieces[move.captureType.ordinal()] &= ~destinationMask;

		// Pieces add destination
		pieces[move.movePiece.ordinal()] |= destinationMask;

		// Pieces remove start
		pieces[move.movePiece.ordinal()] &= ~startMask;

	    // Handle specific move types
	    switch (move.moveType) {
	        case QUIET:
	        	rule50 = move.movePiece == PieceType.PAWN ? 0 : rule50;
	        	break;
	        case CAPTURE:
	        	rule50 = 0;
	            break;
	        case ENPASSANT:
	        	rule50 = 0;
	            // Remove the pawn captured en passant
	            int enPassantCaptureSquare = activePlayer == Color.WHITE ? enPassant - 8 : enPassant + 8;
	            long enPassantCaptureMask = 1L << enPassantCaptureSquare;

	            occupancy &= ~enPassantCaptureMask; // remove capture from occupancy
				pieceColors[0] &= ~enPassantCaptureMask; // remove capture from colors
				pieceColors[1] &= ~enPassantCaptureMask;
                pieces[0] &= ~enPassantCaptureMask; // remove capture piece from pawns
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
	    enPassant = ((move.movePiece == PieceType.PAWN) && Math.abs(move.start - move.destination) == 16) ?
	    	activePlayer == Color.WHITE ? move.destination - 8 : move.destination + 8 : 0;

	    //increment moveCounter
	    fullMoveCount += activePlayer == Color.WHITE ? 0 : 1;

	    // Switch active player
	    activePlayer = activePlayer == Color.WHITE ? Color.BLACK : Color.WHITE;

	    whiteInCheck = move.resultWhiteInCheck;
	    blackInCheck = move.resultBlackInCheck;

		this.zobristHash = Hashing.computeZobrist(this);
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

				//add the pawn back
				occupancy ^= enPassantCaptureMask; //add the pawn back
				pieces[0] ^= enPassantCaptureMask;
				pieceColors[0] |= activePlayer == Color.WHITE ? 0L : enPassantCaptureMask;
				pieceColors[1] |= activePlayer == Color.WHITE ? enPassantCaptureMask : 0L;
				break;
			case PROMOTION:
				//remove promoted piece (if promotion type != capture type
				if (move.promotionType != move.captureType)
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

		this.zobristHash = Hashing.computeZobrist(this);
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

	/**
	* Returns if this position is equal to another
	* @param position position
	* @return true if they are equal
	*/
	public boolean equals(Position position) {
		boolean equal = true;
		equal &= compareValues(this.occupancy, position.occupancy, "Occupancy");
		equal &= compareValues(this.pieces[0], position.pieces[0], "Pawns");
		equal &= compareValues(this.pieces[1], position.pieces[1], "Knight");
		equal &= compareValues(this.pieces[2], position.pieces[2], "Bishop");
		equal &= compareValues(this.pieces[3], position.pieces[3], "Rook");
		equal &= compareValues(this.pieces[4], position.pieces[4], "Queen");
		equal &= compareValues(this.pieces[5], position.pieces[5], "King");
		equal &= compareValues(this.pieceColors[0], position.pieceColors[0], "White Pieces");
		equal &= compareValues(this.pieceColors[1], position.pieceColors[1], "Black Pieces");
		equal &= compareValues(this.castleRights, position.castleRights, "Castle Rights");
		equal &= compareValues(this.rule50, position.rule50, "Rule 50");
		equal &= compareValues(this.enPassant, position.enPassant, "En Passant");
		equal &= compareValues(this.activePlayer, position.activePlayer, "Active Player");
		equal &= compareValues(this.fullMoveCount, position.fullMoveCount, "Full Move Count");
		return equal;
	}
	/**
	* Returns true if values are equal, else false
	* @param value1 first value
	* @param value2 value to compare to
	* @param fieldName field name of these values
	* @return if they are equal
	*/
	private boolean compareValues(Object value1, Object value2, String fieldName) {
		if (!value1.equals(value2)) {
			System.out.println(fieldName + " different");
			return false;
		}
		return true;
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

	/**
	* Prints a board in a human-readable format
	*/
/*
    +---+---+---+---+---+---+---+---+
  8 | r | n | b | q | k | b | n | r |
    +---+---+---+---+---+---+---+---+
  7 | p | p | p | p | p | p | p | p |
    +---+---+---+---+---+---+---+---+
  6 |   |   |   |   |   |   |   |   |
    +---+---+---+---+---+---+---+---+
  5 |   |   |   |   |   |   |   |   |
    +---+---+---+---+---+---+---+---+
  4 |   |   |   |   |   |   |   |   |
    +---+---+---+---+---+---+---+---+
  3 |   |   |   |   |   |   |   |   |
    +---+---+---+---+---+---+---+---+
  2 | P | P | P | P | P | P | P | P |
    +---+---+---+---+---+---+---+---+
  1 | R | N | B | Q | K | B | N | R |
    +---+---+---+---+---+---+---+---+
      A   B   C   D   E   F   G   H
*/
	public String getDisplayBoard() {
		try {
			validPosition();
		} catch (InvalidPositionException ipe) {
			return "Position isn't valid!";
		}

		char[] board = new char[64];
		char[] pieceSymbols = {'p', 'n', 'b', 'r', 'q', 'k'};


		// fill board with correct piece symbol
		Arrays.fill(board, ' ');
		for (int i = 0; i < 6; i++) {
			long currentPieceType = pieces[i];
			while (currentPieceType != 0L) {
				int loc = Long.numberOfTrailingZeros(currentPieceType);
				currentPieceType &= (currentPieceType - 1);
				board[loc] = pieceSymbols[i];
			}
		}

		// Shift case of the board array
		long shiftCase = pieceColors[0];
		while (shiftCase != 0L) {
			int loc = Long.numberOfTrailingZeros(shiftCase);
			shiftCase &= (shiftCase - 1);
			board[loc] = Character.toUpperCase(board[loc]);
		}
		char[][] boardTemplate = new char[][]{
			"    +---+---+---+---+---+---+---+---+\n".toCharArray(),
			"  8 |   |   |   |   |   |   |   |   |\n".toCharArray(),
			"    +---+---+---+---+---+---+---+---+\n".toCharArray(),
			"  7 |   |   |   |   |   |   |   |   |\n".toCharArray(),
			"    +---+---+---+---+---+---+---+---+\n".toCharArray(),
			"  6 |   |   |   |   |   |   |   |   |\n".toCharArray(),
			"    +---+---+---+---+---+---+---+---+\n".toCharArray(),
			"  5 |   |   |   |   |   |   |   |   |\n".toCharArray(),
			"    +---+---+---+---+---+---+---+---+\n".toCharArray(),
			"  4 |   |   |   |   |   |   |   |   |\n".toCharArray(),
			"    +---+---+---+---+---+---+---+---+\n".toCharArray(),
			"  3 |   |   |   |   |   |   |   |   |\n".toCharArray(),
			"    +---+---+---+---+---+---+---+---+\n".toCharArray(),
			"  2 |   |   |   |   |   |   |   |   |\n".toCharArray(),
			"    +---+---+---+---+---+---+---+---+\n".toCharArray(),
			"  1 |   |   |   |   |   |   |   |   |\n".toCharArray(),
			"    +---+---+---+---+---+---+---+---+\n".toCharArray(),
			"	   A   B   C   D   E   F   G   H  \n".toCharArray()};

		int firstSquare = 6;
		int squareIncrement = 4;

		// Replace characters in template with their piece
		for (int i = 0; i < 64; i++) {
			int boardRow = 1 + (i / 8) * 2; // boardRow of boardTemplate
			int boardCol = firstSquare + ((i % 8) * squareIncrement);

			int row = 7 - i / 8;
			int col = i % 8;
			int index = row * 8 + col;

			boardTemplate[boardRow][boardCol] = board[index];
		}

		// Convert char[][] to a list
		List<Character> result = new LinkedList<>();
		for (char[] arr : boardTemplate) {
			for (char c : arr) {
				result.add(c);
			}
		}

		StringBuilder bldr = new StringBuilder();

		for (Character c : result) {
			bldr.append(c);
		}

		return bldr.toString();
	}

	/**
	* A position is "valid" iff:
	* - pieceColors[0-1] have no overlap AND
	* - pieces[0-5] have no overlap AND
	* - pieceColors[0-1] (with OR operator) is equivalent to occupancy
	* - pieces[0-5] (with OR operator) is equivalent to occupancy
	*/
	public void validPosition() throws InvalidPositionException {
		// Test pieceColors no overlap
		long pieceColorsAND = pieceColors[0] & pieceColors[1];
		if (pieceColorsAND != 0)
			throw new InvalidPositionException("Piece colors overlap");

		// Test pieces no overlap
		long piecesAND = pieces[0] & pieces[1] & pieces[2] & pieces[3] & pieces[4] & pieces[5];
		if (piecesAND != 0)
			throw new InvalidPositionException("Piece types overlap");

		// Test pieceColors OR == occupancy
		long pieceColorsOR = pieceColors[0] | pieceColors[1];
		if (occupancy != pieceColorsOR)
			throw new InvalidPositionException("Piece colors aren't consistent with occupancy");

		// Test pieces OR == occupancy
		long piecesOR = pieces[0] | pieces[1] | pieces[2] | pieces[3] | pieces[4] | pieces[5];
		if (occupancy != piecesOR)
			throw new InvalidPositionException("Piece types aren't consistent with occupancy");
	}
}
