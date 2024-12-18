package moveGeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import board.Color;
import board.Move;
import board.PieceType;
import board.Position;
import system.BBO;


public class MoveGenerator{

/*
* Fields
*/
	private static PawnLogic pl;
	private static KingLogic kl;
	private static RookLogic rl;
	private static BishopLogic bl;
	private static KnightLogic nl;
    /*
* Constructor(s)
*/
	/**
	* initializes static fields
	*/
	public MoveGenerator() {
		pl = new PawnLogic();
		kl = new KingLogic();
		rl = new RookLogic();
		bl = new BishopLogic();
		nl = new KnightLogic();
        AbsolutePins ap = new AbsolutePins();

		pl.initializeAll();
		rl.initializeAll();
		bl.initializeAll();
		kl.initializeAll();
		nl.initializeAll();
		ap.initializeAll();
	}
	/**
	 * Generates a list of all legal moves in a position
	 * @param position Position
	 * @return Move list List<Move>
	 */
	public static List<Move> generateStrictlyLegal(Position position) {
		List<Move> allMoves = generateAllMoves(position);
		List<Move> legalMoves = allMoves.stream().filter(move -> {
			position.makeMove(move);
			boolean invalidMove = kingInCheck(position, position.activePlayer == Color.WHITE ? Color.BLACK :: Color.WHITE);
			position.unMakeMove(move);
            return !invalidMove;
        }).collect(Collectors.toList());
		return legalMoves;
	}

	/**
	* returns all pseudo-legal moves
	* @param position
	* @return Move list
	*/
	public static List<Move> generateAllMoves(Position position) {
		List<Move> generatedMoves = new ArrayList<Move>();
		generatedMoves.addAll(generatePawnMoves(position));
		generatedMoves.addAll(generateRookMoves(position));
		generatedMoves.addAll(generateBishopMoves(position));
		generatedMoves.addAll(generateKnightMoves(position));
		generatedMoves.addAll(generateQueenMoves(position));
		generatedMoves.addAll(generateKingMoves(position));
		return generatedMoves;
	}
/*
* Private methods
*/
	/**
	* Generates and returns all pawn moves
	* @param position
	* @return Move list
	*/

	private static List<Move> generatePawnMoves(Position position) {
		List<Move> generatedMoves = new ArrayList<Move>();
		long pawnList = position.pieces[0] & (position.whiteToPlay ? position.whitePieces : position.blackPieces);

		while (pawnList != 0L) {
			int square = Long.numberOfTrailingZeros(pawnList);
			pawnList &= (pawnList - 1);

			// Handle captures
			long destinations = pl.getCaptures(square, position);
			while (destinations != 0) {
				int destination = Long.numberOfTrailingZeros(destinations);
				destinations &= (destinations - 1);
				if (destination / 8 == 0 || destination / 8 == 7) {
					generatedMoves.addAll(generatePromotions(square, destination, position));
				} else {
					generatedMoves.add(Move.captureMove(square, destination, position, PieceType.PAWN));
				}
			}

			// Process quiet moves using bitwise manipulation
			long quietMoves = pl.getQuietMoves(square, position);
			while (quietMoves != 0) {
				int destination = Long.numberOfTrailingZeros(quietMoves);
				quietMoves &= (quietMoves - 1);
				if (destination / 8 == 0 || destination / 8 == 7) {
					generatedMoves.addAll(generatePromotions(square, destination, position));
				} else {
					generatedMoves.add(Move.quietMove(square, destination, position, PieceType.PAWN));
				}
			}

			// Process en passant moves using bitwise manipulation
			long enPassantMoves = pl.getEnPassant(square, position);
			while (enPassantMoves != 0) {
				int destination = Long.numberOfTrailingZeros(enPassantMoves);
				enPassantMoves &= (enPassantMoves - 1);
				generatedMoves.add(Move.enPassantMove(square, destination, position));
			}
		}
		return generatedMoves;
	}

	/**
	 * Generates and returns all rook moves
	 * @param position
	 * @return Move list
	 */

	private static List<Move> generateRookMoves(Position position) {
		List<Move> generatedMoves = new ArrayList<Move>();
		long rookList = (position.whiteToPlay ? position.whitePieces : position.blackPieces) & position.pieces[3];

		// Iterate over the rook positions using bitwise manipulation
		while (rookList != 0L) {
			int square = Long.numberOfTrailingZeros(rookList);
			rookList &= (rookList - 1);

			// Process capture moves
			long captureDestinations = rl.getCaptures(square, position);
			while (captureDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(captureDestinations);
				captureDestinations &= (captureDestinations - 1);
				generatedMoves.add(Move.captureMove(square, destination, position, PieceType.ROOK));
			}

			// Process quiet moves
			long quietDestinations = rl.getQuietMoves(square, position);
			while (quietDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(quietDestinations);
				quietDestinations &= (quietDestinations - 1);
				generatedMoves.add(Move.quietMove(square, destination, position, PieceType.ROOK));
			}
		}
		return generatedMoves;
	}

	/**
	 * Generates and returns all bishop moves
	 * @param position
	 * @return Move list
	 */

	private static List<Move> generateBishopMoves(Position position) {
		List<Move> generatedMoves = new ArrayList<Move>();
		long bishopList = (position.whiteToPlay ? position.whitePieces : position.blackPieces) & position.pieces[2];

		// Iterate over bishop positions using bitwise manipulation
		while (bishopList != 0L) {
			int square = Long.numberOfTrailingZeros(bishopList);
			bishopList &= (bishopList - 1);

			// Process capture moves
			long captureDestinations = bl.getCaptures(square, position);
			while (captureDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(captureDestinations);
				captureDestinations &= (captureDestinations - 1);
				generatedMoves.add(Move.captureMove(square, destination, position, PieceType.BISHOP));
			}

			// Process quiet moves
			long quietDestinations = bl.getQuietMoves(square, position);
			while (quietDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(quietDestinations);
				quietDestinations &= (quietDestinations - 1);
				generatedMoves.add(Move.quietMove(square, destination, position, PieceType.BISHOP));
			}
		}

		return generatedMoves;
	}

	/**
	 * Generates and returns all knight moves
	 * @param position
	 * @return Move list
	 */

	private static List<Move> generateKnightMoves(Position position) {
		List<Move> generatedMoves = new ArrayList<Move>();
		long knightList = (position.whiteToPlay ? position.whitePieces : position.blackPieces) & position.pieces[1];

		// Iterate over knight positions using bitwise manipulation
		while (knightList != 0L) {
			int square = Long.numberOfTrailingZeros(knightList);
			knightList &= (knightList - 1);

			// Process capture moves
			long captureDestinations = nl.getCaptures(square, position);
			while (captureDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(captureDestinations);
				captureDestinations &= (captureDestinations - 1);
				generatedMoves.add(Move.captureMove(square, destination, position, PieceType.KNIGHT));
			}

			// Process quiet moves
			long quietDestinations = nl.getQuietMoves(square, position);
			while (quietDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(quietDestinations);
				quietDestinations &= (quietDestinations - 1);
				generatedMoves.add(Move.quietMove(square, destination, position, PieceType.KNIGHT));
			}
		}

		return generatedMoves;
	}

	/**
	 * Generates and returns all king moves
	 * @param position
	 * @return Move list
	 */

	private static List<Move> generateKingMoves(Position position) {
		List<Move> generatedMoves = new ArrayList<Move>();
		long kingList = (position.whiteToPlay ? position.whitePieces : position.blackPieces) & position.pieces[5];

		// Iterate over king positions using bitwise manipulation
		while (kingList != 0L) {
			int square = Long.numberOfTrailingZeros(kingList);
			kingList &= (kingList - 1);

			// Process capture moves
			long captureDestinations = kl.getCaptures(square, position);
			while (captureDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(captureDestinations);
				captureDestinations &= (captureDestinations - 1);
				generatedMoves.add(Move.captureMove(square, destination, position, PieceType.KING));
			}

			// Process quiet moves
			long quietDestinations = kl.getQuietMoves(square, position);
			while (quietDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(quietDestinations);
				quietDestinations &= (quietDestinations - 1);
				generatedMoves.add(Move.quietMove(square, destination, position, PieceType.KING));
			}

			// Process castling moves
			long castleDestinations = kl.generateCastles(square, position);
			while (castleDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(castleDestinations);
				castleDestinations &= (castleDestinations - 1);
				// Check if they move through or are in check
				if (castleSquaresAttacked(position, destination))
					continue;
				generatedMoves.add(Move.castleMove(square, destination, position));
			}
		}

		return generatedMoves;
	}

	/**
	 * Generates and returns all queen moves
	 * @param position
	 * @return Move list
	 */

	public static List<Move> generateQueenMoves(Position position) {
		List<Move> generatedMoves = new ArrayList<Move>();
		long queenList = (position.whiteToPlay ? position.whitePieces : position.blackPieces) & position.pieces[4];

		// Iterate over queen positions using bitwise manipulation
		while (queenList != 0L) {
			int square = Long.numberOfTrailingZeros(queenList);
			queenList &= (queenList - 1);

			// Process rook-like capture and quiet moves (same as.pieces[3])
			long rookCaptureDestinations = rl.getCaptures(square, position);
			while (rookCaptureDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(rookCaptureDestinations);
				rookCaptureDestinations &= (rookCaptureDestinations - 1);
				generatedMoves.add(Move.captureMove(square, destination, position, PieceType.QUEEN));
			}

			long rookQuietDestinations = rl.getQuietMoves(square, position);
			while (rookQuietDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(rookQuietDestinations);
				rookQuietDestinations &= (rookQuietDestinations - 1);
				generatedMoves.add(Move.quietMove(square, destination, position, PieceType.QUEEN));
			}

			// Process bishop-like capture and quiet moves (same as.pieces[2])
			long bishopCaptureDestinations = bl.getCaptures(square, position);
			while (bishopCaptureDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(bishopCaptureDestinations);
				bishopCaptureDestinations &= (bishopCaptureDestinations - 1);
				generatedMoves.add(Move.captureMove(square, destination, position, PieceType.QUEEN));
			}

			long bishopQuietDestinations = bl.getQuietMoves(square, position);
			while (bishopQuietDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(bishopQuietDestinations);
				bishopQuietDestinations &= (bishopQuietDestinations - 1);
				generatedMoves.add(Move.quietMove(square, destination, position, PieceType.QUEEN));
			}
		}

		return generatedMoves;
	}


	/**
	* generates and returns a list of promotions for each promotable type
	* @param start
	* @param destination
	* @return list of moves
	*/
	private static List<Move> generatePromotions(int start, int destination, Position position) {
		List<Move> promotions = new ArrayList<Move>();
		promotions.add(Move.promotionMove(start, destination, position, PieceType.ROOK));
		promotions.add(Move.promotionMove(start, destination, position, PieceType.BISHOP));
		promotions.add(Move.promotionMove(start, destination, position, PieceType.KNIGHT));
		promotions.add(Move.promotionMove(start, destination, position, PieceType.QUEEN));
		return promotions;
	}




	public static long getPawnAttacks(Position position, int square, Color attackColor)  {
		return pl.getAttackBoard(square, position, attackColor);
	}

	public static long getKnightAttacks(Position position, int square) {
		return nl.getAttackBoard(square, position);
	}
	public static long getBishopAttacks(Position position, int square) {
		return bl.getAttackBoard(square, position);
	}
	public static long getRookAttacks(Position position, int square) {
		return rl.getAttackBoard(square, position);
	}
	public static long getQueenAttacks(Position position, int square) {
		return rl.getAttackBoard(square, position) | bl.getAttackBoard(square, position);
	}
	public static long getKingAttacks(Position position, int square) {
		return kl.getKingAttacks(square);
	}

	/**
	* Returns if castle squares are attacked
	* Only checks starting square and move-through square
	* @param position position to check
	* @param destination destination of the king
	* @return if the castle move is valid
	*/
	private static boolean castleSquaresAttacked(Position position, int destination) {
		boolean validCastle = true;
		if (destination == 2) {
			validCastle &= squareAttackedBy(position, 4, Color.BLACK);
			validCastle &= squareAttackedBy(position, 3, Color.BLACK);
		} else if (destination == 6) {
			validCastle &= squareAttackedBy(position, 4, Color.BLACK);
			validCastle &= squareAttackedBy(position, 5, Color.BLACK);
		} else if (destination == 58) {
			validCastle &= squareAttackedBy(position, 60, Color.WHITE);
			validCastle &= squareAttackedBy(position, 59, Color.WHITE);
		} else if (destination == 62) {
			validCastle &= squareAttackedBy(position, 60, Color.WHITE);
			validCastle &= squareAttackedBy(position, 61, Color.WHITE);
		}
		return validCastle;
	}

	/**
	* Returns if the king of the specified color is in check
	* @param position position to check
	* @param kingColor color of king
	* @return if the specified king is in check
	*/
	public static boolean kingInCheck(Position position, Color kingColor) {
		int kingLoc = Long.numberOfTrailingZeros(position.pieces[5] & position.pieceColors[kingColor.ordinal()]);
		return squareAttackedBy(position, kingLoc, kingColor == Color.WHITE ? Color.BLACK : Color.WHITE);
	}

	/**
	* Returns if a square is attacked by the specified color
	* @param position position to check
	* @param square square to check
	* @param attackColor color of attacking pieces
	* @return squareAttacked if square is attacked by specified color
	*/
	public static boolean squareAttackedBy(Position position, int square, Color attackColor) {
		boolean squareAttacked = false;
		long potentialAttackers = position.pieceColors[attackColor.ordinal()];
		squareAttacked |= ((pl.getAttackBoard(square, position, attackColor == Color.WHITE ? Color.BLACK : Color.WHITE) & potentialAttackers & position.pieces[0]) != 0);
		squareAttacked |= ((bl.getAttackBoard(square, position) & potentialAttackers & position.pieces[2] | position.pieces[5]) != 0);
		squareAttacked |= ((rl.getAttackBoard(square, position) & potentialAttackers & (position.pieces[3] | position.pieces[5])) != 0);
		squareAttacked |= ((nl.getAttackBoard(square, position) & potentialAttackers & position.pieces[1]) != 0);
		squareAttacked |= ((kl.getKingAttacks(square) & potentialAttackers & position.pieces[5]) != 0);
		return squareAttacked;
	}
}

/*
Legacy Code
	* generates and returns the attacks of a square in a position
	* @param position
	* @param square
	* @return attackBB
public static long getAttacks(Position position, int square) {
	long pieceMask = (1L << square);
	long attacks = 0L;
	attacks |= ((position.pieces[0] & pieceMask) != 0) ?
			((position.whitePieces & pieceMask) != 0) ? PawnLogic.blackPawnAttacks[square] :
					PawnLogic.whitePawnAttacks[square] : 0L;
	attacks |= ((position.pieces[3] & pieceMask) != 0) ? rl.getAttackBoard(square, position) : 0L;
	attacks |= ((position.pieces[2] & pieceMask) != 0) ? bl.getAttackBoard(square, position) : 0L;
	attacks |= ((position.pieces[4] & pieceMask) != 0) ?
			(rl.getAttackBoard(square, position) | bl.getAttackBoard(square, position)) : 0L;
	attacks |= ((position.pieces[5] & pieceMask) != 0) ? KingLogic.moveBoards[square] : 0L;
	attacks |= ((position.pieces[1] & pieceMask) != 0) ? KnightLogic.knightMoves[square] : 0L;

	return attacks;
}
	 * generates and returns black's attack map
	 * @param position
	 * @return attackBB
public static long generateBlackAttacks(Position position) {
	long attacks = 0L;
	for(int square : BBO.getSquares(position.blackPieces)) {
		if (BBO.squareHasPiece(position.pieces[0], square)) {
			attacks |= pl.getAttackBoard(square, position);
		} else if (BBO.squareHasPiece(position.pieces[3], square)) {
			attacks |= rl.getAttackBoard(square, position);
		} else if (BBO.squareHasPiece(position.pieces[2], square)) {
			attacks |= bl.getAttackBoard(square, position);
		} else if (BBO.squareHasPiece(position.pieces[4], square)) {
			attacks |= rl.getAttackBoard(square, position);
			attacks |= bl.getAttackBoard(square, position);
		} else if (BBO.squareHasPiece(position.pieces[5], square)) {
			attacks |= kl.getKingAttacks(square);
		} else if (BBO.squareHasPiece(position.pieces[1], square)) {
			attacks |= nl.getAttackBoard(square, position);
		}
	}
	return attacks;
}
* generates and returns white's attack map
	* @param position
	* @return attackBB
	public static long generateWhiteAttacks(Position position) {
		long attacks = 0L;
		for(int square : BBO.getSquares(position.whitePieces)) {
			if (BBO.squareHasPiece(position.pieces[0], square)) {
				attacks |= pl.getAttackBoard(square, position);
			} else if (BBO.squareHasPiece(position.pieces[3], square)) {
				attacks |= rl.getAttackBoard(square, position);
			} else if (BBO.squareHasPiece(position.pieces[2], square)) {
				attacks |= bl.getAttackBoard(square, position);
			} else if (BBO.squareHasPiece(position.pieces[4], square)) {
				attacks |= rl.getAttackBoard(square, position);
				attacks |= bl.getAttackBoard(square, position);
			} else if (BBO.squareHasPiece(position.pieces[5], square)) {
				attacks |= kl.getKingAttacks(square);
			} else if (BBO.squareHasPiece(position.pieces[1], square)) {
				attacks |= nl.getAttackBoard(square, position);
			}
		}
		return attacks;
	}* generates and returns white's attack map
	* @param position
	* @return attackBB
	public static long generateWhiteAttacks(Position position) {
		long attacks = 0L;
		for(int square : BBO.getSquares(position.whitePieces)) {
			if (BBO.squareHasPiece(position.pieces[0], square)) {
				attacks |= pl.getAttackBoard(square, position);
			} else if (BBO.squareHasPiece(position.pieces[3], square)) {
				attacks |= rl.getAttackBoard(square, position);
			} else if (BBO.squareHasPiece(position.pieces[2], square)) {
				attacks |= bl.getAttackBoard(square, position);
			} else if (BBO.squareHasPiece(position.pieces[4], square)) {
				attacks |= rl.getAttackBoard(square, position);
				attacks |= bl.getAttackBoard(square, position);
			} else if (BBO.squareHasPiece(position.pieces[5], square)) {
				attacks |= kl.getKingAttacks(square);
			} else if (BBO.squareHasPiece(position.pieces[1], square)) {
				attacks |= nl.getAttackBoard(square, position);
			}
		}
		return attacks;
	}
*/