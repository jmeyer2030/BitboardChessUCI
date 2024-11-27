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
	
	public MoveGenerator() {
		pl = new PawnLogic();
		kl = new KingLogic();
		rl = new RookLogic();
		bl = new BishopLogic();
		nl = new KnightLogic();
		
		pl.initializeAll();
		rl.initializeAll();
		bl.initializeAll();
		kl.initializeAll();
		nl.initializeAll();
		
	}
	/**
	 * Generates a list of all legal moves in a position
	 * @
	 * 
	 */

	public static List<Move> generateStrictlyLegal(Position position) {
		List<Move> allMoves = generateAllMoves(position);
		List<Move> legalMoves = allMoves.stream().filter(move -> {
			if (position.applyMove(move).selfInCheck())
				return false;
			return true;
		}).collect(Collectors.toList());
		return legalMoves;
	}
	
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
	
	private static List<Move> generatePawnMoves(Position position) {
		List<Move> generatedMoves = new ArrayList<Move>();
		long pawnList = (position.whiteToPlay ? position.whitePieces : position.blackPieces) & position.pawns;
		List<Integer> pawnLocations = BBO.getSquares(pawnList);
		for (int square : pawnLocations) {
			BBO.getSquares(pl.getCaptures(square, position)).stream().forEach(destination ->  {
				if (destination / 8 == 0 || destination / 8 == 7) {
					generatedMoves.addAll(generatePromotions(square, destination));
				} else {
					generatedMoves.add(new Move(Move.MoveType.CAPTURE, square, destination));
				}
			});
			BBO.getSquares(pl.getQuietMoves(square, position)).stream().forEach(destination ->  {
				if (destination / 8 == 0 || destination / 8 == 7) {
					generatedMoves.addAll(generatePromotions(square, destination));
				} else {
					generatedMoves.add(new Move(Move.MoveType.QUIET, square, destination));
				}
			});
			BBO.getSquares(pl.getEnPassant(square, position)).stream().forEach(destination -> 
				generatedMoves.add(new Move(Move.MoveType.ENPASSANT, square, destination)));
		}
		return generatedMoves;
	}
	
	private static List<Move> generateRookMoves(Position position) {
		List<Move> generatedMoves = new ArrayList<Move>();
		long rookList = (position.whiteToPlay ? position.whitePieces : position.blackPieces) & position.rooks;
		List<Integer> rookLocations = BBO.getSquares(rookList);
		for (int square : rookLocations) {
			BBO.getSquares(rl.getCaptures(square, position)).stream().forEach(destination ->  
					generatedMoves.add(new Move(Move.MoveType.CAPTURE, square, destination)));
			BBO.getSquares(rl.getQuietMoves(square, position)).stream().forEach(destination ->  
					generatedMoves.add(new Move(Move.MoveType.QUIET, square, destination)));
		}
		return generatedMoves;
	}
	
	private static List<Move> generateBishopMoves(Position position) {
		List<Move> generatedMoves = new ArrayList<Move>();
		long bishopList = (position.whiteToPlay ? position.whitePieces : position.blackPieces) & position.bishops;
		List<Integer> bishopLocations = BBO.getSquares(bishopList);
		for (int square : bishopLocations) {
			BBO.getSquares(bl.getCaptures(square, position)).stream().forEach(destination ->  
					generatedMoves.add(new Move(Move.MoveType.CAPTURE, square, destination)));
			BBO.getSquares(bl.getQuietMoves(square, position)).stream().forEach(destination ->  
					generatedMoves.add(new Move(Move.MoveType.QUIET, square, destination)));
		}
		return generatedMoves;
	}
	
	private static List<Move> generateKnightMoves(Position position) {
		List<Move> generatedMoves = new ArrayList<Move>();
		long knightList = (position.whiteToPlay ? position.whitePieces : position.blackPieces) & position.knights;
		List<Integer> knightLocations = BBO.getSquares(knightList);
		for (int square : knightLocations) {
			BBO.getSquares(nl.getCaptures(square, position)).stream().forEach(destination ->  
					generatedMoves.add(new Move(Move.MoveType.CAPTURE, square, destination)));
			BBO.getSquares(nl.getQuietMoves(square, position)).stream().forEach(destination ->  
					generatedMoves.add(new Move(Move.MoveType.QUIET, square, destination)));
		}
		return generatedMoves;
	}
	
	private static List<Move> generateKingMoves(Position position) {
		List<Move> generatedMoves = new ArrayList<Move>();
		long rookList = (position.whiteToPlay ? position.whitePieces : position.blackPieces) & position.kings;
		List<Integer> kingLocations = BBO.getSquares(rookList);
		for (int square : kingLocations) {
			BBO.getSquares(kl.getCaptures(square, position)).stream().forEach(destination ->  
					generatedMoves.add(new Move(Move.MoveType.CAPTURE, square, destination)));
			BBO.getSquares(kl.getQuietMoves(square, position)).stream().forEach(destination ->  
					generatedMoves.add(new Move(Move.MoveType.QUIET, square, destination)));
			BBO.getSquares(kl.generateCastles(square, position)).stream().forEach(destination ->
					generatedMoves.add(new Move(Move.MoveType.CASTLE, square, destination)));
		}
		return generatedMoves;
	}
	
	public static List<Move> generateQueenMoves(Position position) {
		List<Move> generatedMoves = new ArrayList<Move>();
		long queenList = (position.whiteToPlay ? position.whitePieces : position.blackPieces) & position.queens;
		List<Integer> queenLocations = BBO.getSquares(queenList);
		for (int square : queenLocations) {
			BBO.getSquares(bl.getCaptures(square, position)).stream().forEach(destination ->  
					generatedMoves.add(new Move(Move.MoveType.CAPTURE, square, destination)));
			BBO.getSquares(bl.getQuietMoves(square, position)).stream().forEach(destination ->  
					generatedMoves.add(new Move(Move.MoveType.QUIET, square, destination)));
			BBO.getSquares(rl.getCaptures(square, position)).stream().forEach(destination ->  
				generatedMoves.add(new Move(Move.MoveType.CAPTURE, square, destination)));
			BBO.getSquares(rl.getQuietMoves(square, position)).stream().forEach(destination ->  
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
	

	
}

/*LEGACY:
 * 
 * 	public static long generateBlackAttacks(Position position) {
		long attacks = 0L;
		
		for(int square : BBO.getSquares(position.blackPieces)) {
			if (BBO.squareHasPiece(position.pawns, square)) {
				attacks |= pl.getBlackPawnAttacks(square);
			} else if (BBO.squareHasPiece(position.rooks, square)) {
				attacks |= rl.getMoveBoard(square, position.occupancy);
			} else if (BBO.squareHasPiece(position.bishops, square)) {
				attacks |= bl.getMoveBoard(square, position.occupancy);
			} else if (BBO.squareHasPiece(position.queens, square)) {
				attacks |= rl.getMoveBoard(square, position.occupancy);
				attacks |= bl.getMoveBoard(square, position.occupancy);
			} else if (BBO.squareHasPiece(position.kings, square)) {
				attacks |= kl.getKingAttacks(square);
			} else if (BBO.squareHasPiece(position.knights, square)) {
				attacks |= nl.getKnightMoves(square);
			}
		}
		
		return attacks;
	}
 * 	private static List<Move> generatePawnMoves(int square, Position position) {
		List<Move> generatedMoves = new ArrayList<Move>();
		if (position.whiteToPlay) {
			long pushes = pl.getWhitePawnPushes(position.occupancy, square);
			BBO.getSquares(pushes).stream().forEach(destination -> 
					generatedMoves.add(new Move(Move.MoveType.QUIET, square, destination)));
			
			long captures = pl.getWhitePawnAttacks(square) & position.blackPieces;
			BBO.getSquares(captures).stream().forEach(destination -> 
			generatedMoves.add(new Move(Move.MoveType.CAPTURE, square, destination)));
			
			if (BBO.getRank(square) == 4 && //if on fourth rank
					position.priorMove != null && //prior move exists
					position.priorMove.start - position.priorMove.destination == 16 && //prior move is form of double pawn push
					BBO.squareHasPiece(position.pawns, position.priorMove.destination)) {//prior move is a pawn
				generatedMoves.add(new Move(Move.MoveType.ENPASSANT, square, position.priorMove.destination + 8));
			}
		} else {
			long pushes = pl.getBlackPawnPushes(position.occupancy, square);
			BBO.getSquares(pushes).stream().forEach(destination ->
				generatedMoves.add(new Move(Move.MoveType.QUIET, square, destination)));
			
			long captures = pl.getBlackPawnAttacks(square) & position.whitePieces;
			BBO.getSquares(captures).stream().forEach(destination -> 
				generatedMoves.add(new Move(Move.MoveType.CAPTURE, square, destination)));
			
			if (BBO.getRank(square) == 3 && //if on fourth rank
					position.priorMove != null && //prior move exists
					position.priorMove.destination - position.priorMove.start == 16 && //prior move is form of double pawn push
					BBO.squareHasPiece(position.pawns, position.priorMove.destination)) {//prior move is a pawn
				generatedMoves.add(new Move(Move.MoveType.ENPASSANT, square, position.priorMove.destination - 8));
			}
		}
		
		return generatedMoves;
	}
 * 	public static List<Move> generateAllMoves(Position position) {
		List<Move> generatedMoves = new ArrayList<Move>();
		BBO.getSquares(position.whiteToPlay ? position.whitePieces : position.blackPieces).stream().forEach(square -> {
			if (BBO.squareHasPiece(position.pawns, square)) {
				generatedMoves.addAll(generatePawnMoves(square, position));
			} else if (BBO.squareHasPiece(position.rooks, square)) {
				generatedMoves.addAll(generateRookMoves(square, position));
			} else if (BBO.squareHasPiece(position.bishops, square)) {
				generatedMoves.addAll(generateBishopMoves(square, position));
			} else if (BBO.squareHasPiece(position.queens, square)) {
				generatedMoves.addAll(generateQueenMoves(square, position));
			} else if (BBO.squareHasPiece(position.kings, square)) {
				generatedMoves.addAll(generateKingMoves(square, position));
			} else if (BBO.squareHasPiece(position.knights, square)) {
				generatedMoves.addAll(generateKnightMoves(square, position));
			}
		});
		return generatedMoves;
	}
 * 	private static List<Move> generateMoves(int square, Position position) {
		//assert that there is a piece of the correct color at this position
		assert position.whiteToPlay ? (position.whitePieces & (1L << square)) != 0 : (position.blackPieces & (1L << square)) != 0;
		
		List<Move> generatedMoves = new ArrayList<Move>();	 
		return generatedMoves;
	}
 * 
 * 
 * */