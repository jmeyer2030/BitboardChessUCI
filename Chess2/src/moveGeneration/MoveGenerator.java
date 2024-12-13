package moveGeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import board.Move;
import board.Position;
import system.BBO;

/**
 * This class generates lists of moves for pieces
 */
public class MoveGenerator{
	
	private static PawnLogic pl;
	private static KingLogic kl;
	private static RookLogic rl;
	private static BishopLogic bl;
	private static KnightLogic nl;
	private static AbsolutePins ap;
	
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
	 * @
	 * 
	 */
	//long moveableSquares = position.whiteToPlay ? ~position.blackAttackMap : ~position.whiteAttackMap;
	public static List<Move> generateStrictlyLegal(Position position) {
		List<Move> allMoves = generateAllMoves(position);
		List<Move> legalMoves = allMoves.stream().filter(move -> {
			if (position.applyMove(move).selfInCheck())
				return false;
			return true;
		}).collect(Collectors.toList());
		return legalMoves;
	}
	
	public static List<Move> generateMoves(Position position) {
		if (position.checkers == 0L)
			return generateAllMoves(position);
		if (BBO.getSquares(position.checkers).size() == 1) {
			return generateSingleCheckMoves(position);
		}
		return generateDoubleCheckMoves(position);
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
	
	public static List<Move> generateAllMoves(Position position) {
		List<Move> generatedMoves = new ArrayList<Move>();
		generatedMoves.addAll(generatePawnMoves(position, -1L));
		generatedMoves.addAll(generateRookMoves(position, -1L));
		generatedMoves.addAll(generateBishopMoves(position, -1L));
		generatedMoves.addAll(generateKnightMoves(position, -1L));
		generatedMoves.addAll(generateQueenMoves(position, -1L));
		generatedMoves.addAll(generateKingMoves(position, -1L));
		return generatedMoves;
	}
	
	private static List<Move> generatePawnMoves(Position position, long checkFilter) {
		List<Move> generatedMoves = new ArrayList<Move>();
		long pawnList = (position.whiteToPlay ? position.whitePieces : position.blackPieces) & position.pawns;
		List<Integer> pawnLocations = BBO.getSquares(pawnList);
		for (int square : pawnLocations) {
			long legalMoves = position.moveScope[square];
			BBO.getSquares(checkFilter & legalMoves & pl.getCaptures(square, position)).stream().forEach(destination ->  {
				if (destination / 8 == 0 || destination / 8 == 7) {
					generatedMoves.addAll(generatePromotions(square, destination));
				} else {
					generatedMoves.add(new Move(Move.MoveType.CAPTURE, square, destination));
				}
			});
			BBO.getSquares(checkFilter & legalMoves & pl.getQuietMoves(square, position)).stream().forEach(destination ->  {
				if (destination / 8 == 0 || destination / 8 == 7) {
					generatedMoves.addAll(generatePromotions(square, destination));
				} else {
					generatedMoves.add(new Move(Move.MoveType.QUIET, square, destination));
				}
			});
			BBO.getSquares(checkFilter & legalMoves & pl.getEnPassant(square, position)).stream().forEach(destination -> 
				generatedMoves.add(new Move(Move.MoveType.ENPASSANT, square, destination)));
		}
		return generatedMoves;
	}
	
	private static List<Move> generateRookMoves(Position position, long checkFilter) {
		List<Move> generatedMoves = new ArrayList<Move>();
		long rookList = (position.whiteToPlay ? position.whitePieces : position.blackPieces) & position.rooks;
		List<Integer> rookLocations = BBO.getSquares(rookList);
		for (int square : rookLocations) {
			long legalMoves = position.moveScope[square];
			BBO.getSquares(checkFilter & legalMoves & rl.getCaptures(square, position)).stream().forEach(destination ->  
					generatedMoves.add(new Move(Move.MoveType.CAPTURE, square, destination)));
			BBO.getSquares(checkFilter & legalMoves & rl.getQuietMoves(square, position)).stream().forEach(destination ->  
					generatedMoves.add(new Move(Move.MoveType.QUIET, square, destination)));
		}
		return generatedMoves;
	}
	
	private static List<Move> generateBishopMoves(Position position, long checkFilter) {
		List<Move> generatedMoves = new ArrayList<Move>();
		long bishopList = (position.whiteToPlay ? position.whitePieces : position.blackPieces) & position.bishops;
		List<Integer> bishopLocations = BBO.getSquares(bishopList);
		for (int square : bishopLocations) {
			long legalMoves = position.moveScope[square];
			BBO.getSquares(checkFilter & legalMoves & bl.getCaptures(square, position)).stream().forEach(destination ->  
					generatedMoves.add(new Move(Move.MoveType.CAPTURE, square, destination)));
			BBO.getSquares(checkFilter & legalMoves & bl.getQuietMoves(square, position)).stream().forEach(destination ->  
					generatedMoves.add(new Move(Move.MoveType.QUIET, square, destination)));
		}
		return generatedMoves;
	}
	
	private static List<Move> generateKnightMoves(Position position, long checkFilter) {
		List<Move> generatedMoves = new ArrayList<Move>();
		long knightList = (position.whiteToPlay ? position.whitePieces : position.blackPieces) & position.knights;
		List<Integer> knightLocations = BBO.getSquares(knightList);
		for (int square : knightLocations) {
			long legalMoves = position.moveScope[square];
			BBO.getSquares(checkFilter & legalMoves & nl.getCaptures(square, position)).stream().forEach(destination ->  
					generatedMoves.add(new Move(Move.MoveType.CAPTURE, square, destination)));
			BBO.getSquares(checkFilter & legalMoves & nl.getQuietMoves(square, position)).stream().forEach(destination ->  
					generatedMoves.add(new Move(Move.MoveType.QUIET, square, destination)));
		}
		return generatedMoves;
	}
	
	private static List<Move> generateKingMoves(Position position, long checkFilter) {
		List<Move> generatedMoves = new ArrayList<Move>();
		long kingList = (position.whiteToPlay ? position.whitePieces : position.blackPieces) & position.kings;
		List<Integer> kingLocations = BBO.getSquares(kingList);
		for (int square : kingLocations) {
			long legalMoves = position.moveScope[square];
			BBO.getSquares(checkFilter & legalMoves & kl.getCaptures(square, position)).stream().forEach(destination ->  
					generatedMoves.add(new Move(Move.MoveType.CAPTURE, square, destination)));
			BBO.getSquares(checkFilter & legalMoves & kl.getQuietMoves(square, position)).stream().forEach(destination ->  
					generatedMoves.add(new Move(Move.MoveType.QUIET, square, destination)));
			BBO.getSquares(checkFilter & legalMoves & kl.generateCastles(square, position)).stream().forEach(destination ->
					generatedMoves.add(new Move(Move.MoveType.CASTLE, square, destination)));
		}
		return generatedMoves;
	}
	
	public static List<Move> generateQueenMoves(Position position, long checkFilter) {
		List<Move> generatedMoves = new ArrayList<Move>();
		long queenList = (position.whiteToPlay ? position.whitePieces : position.blackPieces) & position.queens;
		List<Integer> queenLocations = BBO.getSquares(queenList);
		for (int square : queenLocations) {
			long legalMoves = position.moveScope[square];
			BBO.getSquares(checkFilter & legalMoves & bl.getCaptures(square, position)).stream().forEach(destination ->  
					generatedMoves.add(new Move(Move.MoveType.CAPTURE, square, destination)));
			BBO.getSquares(checkFilter & legalMoves & bl.getQuietMoves(square, position)).stream().forEach(destination ->  
					generatedMoves.add(new Move(Move.MoveType.QUIET, square, destination)));
			BBO.getSquares(checkFilter & legalMoves & rl.getCaptures(square, position)).stream().forEach(destination ->  
				generatedMoves.add(new Move(Move.MoveType.CAPTURE, square, destination)));
			BBO.getSquares(checkFilter & legalMoves & rl.getQuietMoves(square, position)).stream().forEach(destination ->  
				generatedMoves.add(new Move(Move.MoveType.QUIET, square, destination)));
		}
		return generatedMoves;
	}
	
	private static List<Move> generatePromotions(int start, int destination) {
		List<Move> promotions = new ArrayList<Move>();
		promotions.add(new Move(Move.MoveType.PROMOTION, Move.PieceType.ROOK, start, destination));
		promotions.add(new Move(Move.MoveType.PROMOTION, Move.PieceType.BISHOP, start, destination));
		promotions.add(new Move(Move.MoveType.PROMOTION, Move.PieceType.KNIGHT, start, destination));
		promotions.add(new Move(Move.MoveType.PROMOTION, Move.PieceType.QUEEN, start, destination));
		return promotions;
	}
	
	
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

	
}

/*LEGACY:
 
 * */