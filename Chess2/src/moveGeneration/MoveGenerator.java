package moveGeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import board.Move;
import board.MoveType;
import board.PieceType;
import board.Position;
import system.BBO;


public class MoveGenerator{

/**
* Fields
*/
	private static PawnLogic pl;
	private static KingLogic kl;
	private static RookLogic rl;
	private static BishopLogic bl;
	private static KnightLogic nl;
	private static AbsolutePins ap;
/**
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
		ap = new AbsolutePins();

		pl.initializeAll();
		rl.initializeAll();
		bl.initializeAll();
		kl.initializeAll();
		nl.initializeAll();
		ap.initializeAll();
	}
	/**
	 * Generates a list of all legal moves in a position
	 * @param position
	 * @return Move list
	 */
	public static List<Move> generateStrictlyLegal(Position position) {
		List<Move> allMoves = generateAllMoves(position);
		List<Move> legalMoves = allMoves.stream().filter(move -> {
			position.makeMove(move);
			boolean invalidMove = position.selfInCheck();
			position.unMakeMove(move);
			if (invalidMove)
				return false;
			return true;
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
/**
* Private methods
*/
	/**
	* Generates and returns all pawn moves
	* @param position
	* @return Move list
	*/

	private static List<Move> generatePawnMoves(Position position) {
		List<Move> generatedMoves = new ArrayList<Move>();
		long pawnList = position.pawns & (position.whiteToPlay ? position.whitePieces : position.blackPieces);

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
					generatedMoves.add(Move.captureMove(square, destination, position));
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
					generatedMoves.add(Move.quietMove(square, destination, position));
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
		long rookList = (position.whiteToPlay ? position.whitePieces : position.blackPieces) & position.rooks;

		// Iterate over the rook positions using bitwise manipulation
		while (rookList != 0L) {
			int square = Long.numberOfTrailingZeros(rookList);
			rookList &= (rookList - 1);

			// Process capture moves
			long captureDestinations = rl.getCaptures(square, position);
			while (captureDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(captureDestinations);
				captureDestinations &= (captureDestinations - 1);
				generatedMoves.add(Move.captureMove(square, destination, position));
			}

			// Process quiet moves
			long quietDestinations = rl.getQuietMoves(square, position);
			while (quietDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(quietDestinations);
				quietDestinations &= (quietDestinations - 1);
				generatedMoves.add(Move.quietMove(square, destination, position));
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
		long bishopList = (position.whiteToPlay ? position.whitePieces : position.blackPieces) & position.bishops;

		// Iterate over bishop positions using bitwise manipulation
		while (bishopList != 0L) {
			int square = Long.numberOfTrailingZeros(bishopList);
			bishopList &= (bishopList - 1);

			// Process capture moves
			long captureDestinations = bl.getCaptures(square, position);
			while (captureDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(captureDestinations);
				captureDestinations &= (captureDestinations - 1);
				generatedMoves.add(Move.captureMove(square, destination, position));
			}

			// Process quiet moves
			long quietDestinations = bl.getQuietMoves(square, position);
			while (quietDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(quietDestinations);
				quietDestinations &= (quietDestinations - 1);
				generatedMoves.add(Move.quietMove(square, destination, position));
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
		long knightList = (position.whiteToPlay ? position.whitePieces : position.blackPieces) & position.knights;

		// Iterate over knight positions using bitwise manipulation
		while (knightList != 0L) {
			int square = Long.numberOfTrailingZeros(knightList);
			knightList &= (knightList - 1);

			// Process capture moves
			long captureDestinations = nl.getCaptures(square, position);
			while (captureDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(captureDestinations);
				captureDestinations &= (captureDestinations - 1);
				generatedMoves.add(Move.captureMove(square, destination, position));
			}

			// Process quiet moves
			long quietDestinations = nl.getQuietMoves(square, position);
			while (quietDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(quietDestinations);
				quietDestinations &= (quietDestinations - 1);
				generatedMoves.add(Move.quietMove(square, destination, position));
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
		long kingList = (position.whiteToPlay ? position.whitePieces : position.blackPieces) & position.kings;

		// Iterate over king positions using bitwise manipulation
		while (kingList != 0L) {
			int square = Long.numberOfTrailingZeros(kingList);
			kingList &= (kingList - 1);

			// Process capture moves
			long captureDestinations = kl.getCaptures(square, position);
			while (captureDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(captureDestinations);
				captureDestinations &= (captureDestinations - 1);
				generatedMoves.add(Move.captureMove(square, destination, position));
			}

			// Process quiet moves
			long quietDestinations = kl.getQuietMoves(square, position);
			while (quietDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(quietDestinations);
				quietDestinations &= (quietDestinations - 1);
				generatedMoves.add(Move.quietMove(square, destination, position));
			}

			// Process castling moves
			long castleDestinations = kl.generateCastles(square, position);
			while (castleDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(castleDestinations);
				castleDestinations &= (castleDestinations - 1);
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
		long queenList = (position.whiteToPlay ? position.whitePieces : position.blackPieces) & position.queens;

		// Iterate over queen positions using bitwise manipulation
		while (queenList != 0L) {
			int square = Long.numberOfTrailingZeros(queenList);
			queenList &= (queenList - 1);

			// Process rook-like capture and quiet moves (same as rooks)
			long rookCaptureDestinations = rl.getCaptures(square, position);
			while (rookCaptureDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(rookCaptureDestinations);
				rookCaptureDestinations &= (rookCaptureDestinations - 1);
				generatedMoves.add(Move.captureMove(square, destination, position));
			}

			long rookQuietDestinations = rl.getQuietMoves(square, position);
			while (rookQuietDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(rookQuietDestinations);
				rookQuietDestinations &= (rookQuietDestinations - 1);
				generatedMoves.add(Move.quietMove(square, destination, position));
			}

			// Process bishop-like capture and quiet moves (same as bishops)
			long bishopCaptureDestinations = bl.getCaptures(square, position);
			while (bishopCaptureDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(bishopCaptureDestinations);
				bishopCaptureDestinations &= (bishopCaptureDestinations - 1);
				generatedMoves.add(Move.captureMove(square, destination, position));
			}

			long bishopQuietDestinations = bl.getQuietMoves(square, position);
			while (bishopQuietDestinations != 0) {
				int destination = Long.numberOfTrailingZeros(bishopQuietDestinations);
				bishopQuietDestinations &= (bishopQuietDestinations - 1);
				generatedMoves.add(Move.quietMove(square, destination, position));
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

	/**
	* generates and returns white's attack map
	* @param position
	* @return attackBB
	*/
	public static long generateWhiteAttacks(Position position) {
		long attacks = 0L;
		for(int square : BBO.getSquares(position.whitePieces)) {
			if (BBO.squareHasPiece(position.pawns, square)) {
				attacks |= pl.getAttackBoard(square, position);
			} else if (BBO.squareHasPiece(position.rooks, square)) {
				attacks |= rl.getAttackBoard(square, position);
			} else if (BBO.squareHasPiece(position.bishops, square)) {
				attacks |= bl.getAttackBoard(square, position);
			} else if (BBO.squareHasPiece(position.queens, square)) {
				attacks |= rl.getAttackBoard(square, position);
				attacks |= bl.getAttackBoard(square, position);
			} else if (BBO.squareHasPiece(position.kings, square)) {
				attacks |= kl.getKingAttacks(square);
			} else if (BBO.squareHasPiece(position.knights, square)) {
				attacks |= nl.getAttackBoard(square, position);
			}
		}
		return attacks;
	}
	/**
	 * generates and returns black's attack map
	 * @param position
	 * @return attackBB
	 */
	public static long generateBlackAttacks(Position position) {
		long attacks = 0L;
		for(int square : BBO.getSquares(position.blackPieces)) {
			if (BBO.squareHasPiece(position.pawns, square)) {
				attacks |= pl.getAttackBoard(square, position);
			} else if (BBO.squareHasPiece(position.rooks, square)) {
				attacks |= rl.getAttackBoard(square, position);
			} else if (BBO.squareHasPiece(position.bishops, square)) {
				attacks |= bl.getAttackBoard(square, position);
			} else if (BBO.squareHasPiece(position.queens, square)) {
				attacks |= rl.getAttackBoard(square, position);
				attacks |= bl.getAttackBoard(square, position);
			} else if (BBO.squareHasPiece(position.kings, square)) {
				attacks |= kl.getKingAttacks(square);
			} else if (BBO.squareHasPiece(position.knights, square)) {
				attacks |= nl.getAttackBoard(square, position);
			}
		}
		return attacks;
	}

	/**
	* generates and retursn the attacks of a square in a position
	* @param position
	* @param square
	* @return attackBB
	*/
	public static long getAttacks(Position position, int square) {
		long pieceMask = (1L << square);
		if ((position.pawns & pieceMask) != 0) {
			if ((position.whitePieces & pieceMask) != 0)
				return PawnLogic.whitePawnAttacks[square];
			return PawnLogic.blackPawnAttacks[square];
		} else if ((position.rooks & pieceMask) != 0) {
			return rl.getAttackBoard(square, position);
		} else if ((position.bishops & pieceMask) != 0) {
			return bl.getAttackBoard(square, position);
		} else if ((position.queens & pieceMask) != 0) {
			return rl.getAttackBoard(square, position) | bl.getAttackBoard(square, position);
		} else if ((position.kings & pieceMask) != 0) {
			return KingLogic.moveBoards[square];
		} else if ((position.knights & pieceMask) != 0) {
			return KnightLogic.knightMoves[square];
		}
		return 0L;
	}




}

/*LEGACY:


 	public static long getXrayAttacks(Position position, int square) {
		if ((position.bishops & (1L << square)) != 0) {
			return bl.xrayAttacks(square, position);
		} else if ((position.rooks & (1L << square)) != 0) {
			return rl.xrayAttacks(square, position);
		} else if ((position.queens & (1L << square)) != 0) {
			return bl.xrayAttacks(square, position) | rl.xrayAttacks(square, position);
		} else {
			return 0L;
		}
	}

	public static long[] generateAttackArray(Position position) {
		long[] attackArray = new long[64];
		for(int square : BBO.getSquares(position.whitePieces)) {
			if (BBO.squareHasPiece(position.pawns, square)) {
				attackArray[square] = pl.getAttackBoard(square, position);
			} else if (BBO.squareHasPiece(position.rooks, square)) {
				attackArray[square] = rl.getAttackBoard(square, position);
			} else if (BBO.squareHasPiece(position.bishops, square)) {
				attackArray[square] = bl.getAttackBoard(square, position);
			} else if (BBO.squareHasPiece(position.queens, square)) {
				attackArray[square] = rl.getAttackBoard(square, position);
				attackArray[square] = bl.getAttackBoard(square, position);
			} else if (BBO.squareHasPiece(position.kings, square)) {
				attackArray[square] = kl.getKingAttacks(square);
			} else if (BBO.squareHasPiece(position.knights, square)) {
				attackArray[square] = nl.getAttackBoard(square, position);
			}
		}

		for(int square : BBO.getSquares(position.blackPieces)) {
			if (BBO.squareHasPiece(position.pawns, square)) {
				attackArray[square] = pl.getAttackBoard(square, position);
			} else if (BBO.squareHasPiece(position.rooks, square)) {
				attackArray[square] = rl.getAttackBoard(square, position);
			} else if (BBO.squareHasPiece(position.bishops, square)) {
				attackArray[square] = bl.getAttackBoard(square, position);
			} else if (BBO.squareHasPiece(position.queens, square)) {
				attackArray[square] = rl.getAttackBoard(square, position);
				attackArray[square] = bl.getAttackBoard(square, position);
			} else if (BBO.squareHasPiece(position.kings, square)) {
				attackArray[square] = kl.getKingAttacks(square);
			} else if (BBO.squareHasPiece(position.knights, square)) {
				attackArray[square] = nl.getAttackBoard(square, position);
			}
		}

		return attackArray;
	}

	public static List<Move> generateSingleCheckMoves(Position position) {
		List<Move> generatedMoves = new ArrayList<Move>();
		int kingLoc = BBO.getSquares(position.kings  & (position.whiteToPlay ? position.whitePieces : position.blackPieces)).get(0);
		int checkerLoc = BBO.getSquares(position.checkers).get(0);
		long legalMoveMask = AbsolutePins.inBetween[checkerLoc][kingLoc] | (1L << checkerLoc);
		generatedMoves.addAll(generatePawnMoves(position, legalMoveMask));
		generatedMoves.addAll(generateRookMoves(position, legalMoveMask));
		generatedMoves.addAll(generateBishopMoves(position, legalMoveMask));
		generatedMoves.addAll(generateKnightMoves(position, legalMoveMask));
		generatedMoves.addAll(generateQueenMoves(position, legalMoveMask));
		generatedMoves.addAll(generateKingMoves(position, legalMoveMask));
		return generatedMoves;
	}

	public static List<Move> generateDoubleCheckMoves(Position position) {
		List<Move> generatedMoves = new ArrayList<Move>();
		long kingList = (position.whiteToPlay ? position.whitePieces : position.blackPieces) & position.kings;
		List<Integer> kingLocations = BBO.getSquares(kingList);
		for (int square : kingLocations) {
			long legalMoves = position.moveScope[square];
			BBO.getSquares(legalMoves & kl.getCaptures(square, position)).stream().forEach(destination ->
					generatedMoves.add(new Move(Move.MoveType.CAPTURE, square, destination)));
			BBO.getSquares(legalMoves & kl.getQuietMoves(square, position)).stream().forEach(destination ->
					generatedMoves.add(new Move(Move.MoveType.QUIET, square, destination)));
		}
		return generatedMoves;
	}

	public static List<Move> generateMoves(Position position) {
		if (position.checkers == 0L)
			return generateAllMoves(position);
		if (BBO.getSquares(position.checkers).size() == 1) {
			return generateSingleCheckMoves(position);
		}
		return generateDoubleCheckMoves(position);
	}
 * */