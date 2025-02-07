package moveGeneration;

import board.*;
import customExceptions.InvalidPositionException;
import zobrist.Hashing;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator2 {


    public static void main(String[] args) {
        MoveGenerator2.initializeAll();
        Hashing.initializeRandomNumbers();
        Position position = new Position();
        int[] moveBuffer = new int[256];
        int firstNonMove = 0;

        int result = generateAllMoves(position, moveBuffer, firstNonMove);

        for (int i = 0; i < 20; i++) {
            System.out.println(moveBuffer[i]);
        }

        System.out.println(result);

    }

    /*
     * Fields
     */

    public static int[] moves = new int[256];
    public static int firstNonMove = 0;

    private static PawnLogic pl;
    private static KingLogic kl;
    private static RookLogic rl;
    private static BishopLogic bl;
    private static KnightLogic nl;

    /**
     * initializes static fields
     */
    public static void initializeAll() {
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
     * Returns the move object from a description in long algebraic notation (LAN)
     * @param lan move described in Long Algebraic Notation
     * @param position position that the move would be played on
     * @return the described move object
     */
    public static Move getMoveFromLAN(String lan, Position position) {
        List<Move> moves = null;
        try {
            moves = moveGeneration.MoveGenerator.generateStrictlyLegal(position);
        } catch (InvalidPositionException e) {
            throw new RuntimeException(e);
        }

        for (Move move : moves) {
            if (move.toLongAlgebraic().equals(lan))
                return move;
        }

        // Case that no generated move matches the lan description
        return null;
    }



    /**
     * returns all legal moves
     * @param position
     * @return Move list
     */
    public static int generateAllMoves(Position position, int[] moveBuffer, int firstNonMove) {
        firstNonMove = generatePawnMoves(position, moveBuffer, firstNonMove);
        firstNonMove = generateKnightMoves(position, moveBuffer, firstNonMove);
        firstNonMove = generateBishopMoves(position, moveBuffer, firstNonMove);
        firstNonMove = generateRookMoves(position, moveBuffer, firstNonMove);
        firstNonMove = generateQueenMoves(position, moveBuffer, firstNonMove);
        firstNonMove = generateKingMoves(position, moveBuffer, firstNonMove);
        return firstNonMove;
    }
    /*
     * Private methods
     */

    /**
    * if the move results in self in check, returns 0. Otherwise, checks if the move puts the enemy in check,
    * if it does, adds the check flag, then returns the move
    * @param move the move to check
    * @param position the position to check the move on
    * @return move null move if the move is illegal otherwise the move with updated check flag
    */
     private static int updateChecks(int move, Position position) {
         position.makeMove(move);

         if (kingInCheck(position, 1 - position.activePlayer)) {
            return 0;
         }

         move = MoveEncoding.setIsCheck(move, kingInCheck(position, position.activePlayer) ? 1 : 0);

         position.unMakeMove(move);

         return move;
     }

     /**
     * Updates checks on the move and sees if its legal. If it's valid, it is added to the move buffer
     * and the new firstNonMove of the array is returned.
     * @param move move to add
     * @param moveBuffer buffer to add to
     * @param firstNonMove index to add to
     * @param position position move is applied to
     * @return new buffer location
     */
     private static int addAndValidateMove(int move, int[] moveBuffer, int firstNonMove, Position position) {

        move = MoveEncoding.setActivePlayer(move, position.activePlayer);
        move = updateChecks(move, position);
        if (move == 0) {
            return firstNonMove;
        }
        moveBuffer[firstNonMove] = move;
        return ++firstNonMove;
     }

    /**
     * Generates and returns all pawn moves
     * @param position to generate moves for
     * @param moveBuffer to fill moves
     * @param firstNonMove index of moveBuffer to start adding
     * @return firstNonMove updated
     */
    private static int generatePawnMoves(Position position, int[] moveBuffer, int firstNonMove) {
        long pawnList = position.pieces[0] & position.pieceColors[position.activePlayer];

        while (pawnList != 0L) {
            int start = Long.numberOfTrailingZeros(pawnList);
            pawnList &= (pawnList - 1);

            // Handle captures
            long destinations = pl.getCaptures(start, position);
            while (destinations != 0) {
                int destination = Long.numberOfTrailingZeros(destinations);
                destinations &= (destinations - 1);

                if (destination / 8 == 0 || destination / 8 == 7) {
                    int moveN = MoveShortcuts.generatePawnPromotionCapture(start, destination, 1, position);
                    int moveB = MoveShortcuts.generatePawnPromotionCapture(start, destination, 2, position);
                    int moveR = MoveShortcuts.generatePawnPromotionCapture(start, destination, 2, position);
                    int moveQ = MoveShortcuts.generatePawnPromotionCapture(start, destination, 2, position);

                    firstNonMove = addAndValidateMove(moveN, moveBuffer, firstNonMove, position);
                    firstNonMove = addAndValidateMove(moveB, moveBuffer, firstNonMove, position);
                    firstNonMove = addAndValidateMove(moveR, moveBuffer, firstNonMove, position);
                    firstNonMove = addAndValidateMove(moveQ, moveBuffer, firstNonMove, position);

                } else {
                    int move = MoveShortcuts.generatePawnCapture(start, destination, position);
                    firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
                }
            }

            // Handle Quiet Moves
            long quietMoves = pl.getQuietMoves(start, position);
            while (quietMoves != 0) {
                int destination = Long.numberOfTrailingZeros(quietMoves);
                quietMoves &= (quietMoves - 1);

                if (destination / 8 == 0 || destination / 8 == 7) {
                    int moveN = MoveShortcuts.generatePawnPromotionNoCapture(start, destination, 1, position);
                    int moveB = MoveShortcuts.generatePawnPromotionNoCapture(start, destination, 2, position);
                    int moveR = MoveShortcuts.generatePawnPromotionNoCapture(start, destination, 3, position);
                    int moveQ = MoveShortcuts.generatePawnPromotionNoCapture(start, destination, 4, position);

                    firstNonMove = addAndValidateMove(moveN, moveBuffer, firstNonMove, position);
                    firstNonMove = addAndValidateMove(moveB, moveBuffer, firstNonMove, position);
                    firstNonMove = addAndValidateMove(moveR, moveBuffer, firstNonMove, position);
                    firstNonMove = addAndValidateMove(moveQ, moveBuffer, firstNonMove, position);
                } else if (Math.abs(destination - start) == 16) {
                    int move = MoveShortcuts.generatePawnDoublePush(start, destination, position);
                    firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
                } else {
                    int move = MoveShortcuts.generatePawnSinglePush(start, destination, position);
                    firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
                }
            }

            // Handle En Passant
            long enPassantMoves = pl.getEnPassant(start, position);
            while (enPassantMoves != 0) {
                int destination = Long.numberOfTrailingZeros(enPassantMoves);
                enPassantMoves &= (enPassantMoves - 1);

                int move = MoveShortcuts.generatePawnEnPassant(start, destination, position);
                firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
            }
        }

        return firstNonMove;
    }

    /**
     * Generates and returns all Rook moves
     * @param position to generate moves for
     * @param moveBuffer to fill moves
     * @param firstNonMove index of moveBuffer to start adding
     * @return firstNonMove updated
     */
    private static int generateRookMoves(Position position, int[] moveBuffer, int firstNonMove) {

        long rookList = (position.pieceColors[position.activePlayer]) & position.pieces[3];
        // Iterate over the rook positions using bitwise manipulation
        while (rookList != 0L) {
            int start = Long.numberOfTrailingZeros(rookList);
            rookList &= (rookList - 1);

            // Process capture moves
            long captureDestinations = rl.getCaptures(start, position);
            while (captureDestinations != 0) {
                int destination = Long.numberOfTrailingZeros(captureDestinations);
                captureDestinations &= (captureDestinations - 1);

                int move = MoveShortcuts.generateRookCapture(start, destination, position);
                firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
            }

            // Process quiet moves
            long quietDestinations = rl.getQuietMoves(start, position);
            while (quietDestinations != 0) {
                int destination = Long.numberOfTrailingZeros(quietDestinations);
                quietDestinations &= (quietDestinations - 1);

                int move = MoveShortcuts.generateRookNoCapture(start, destination, position);
                firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
            }
        }
        return firstNonMove;
    }

    /**
     * Generates and returns all bishop moves
     * @param position
     * @return Move list
     */
    private static int generateBishopMoves(Position position, int[] moveBuffer, int firstNonMove) {

        long bishopList = position.pieceColors[position.activePlayer] & position.pieces[2];
        // Iterate over bishop positions using bitwise manipulation
        while (bishopList != 0L) {
            int start = Long.numberOfTrailingZeros(bishopList);
            bishopList &= (bishopList - 1);

            // Process capture moves
            long captureDestinations = bl.getCaptures(start, position);
            while (captureDestinations != 0) {
                int destination = Long.numberOfTrailingZeros(captureDestinations);
                captureDestinations &= (captureDestinations - 1);

                int move = MoveShortcuts.generateBishopCapture(start, destination, position);
                firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
            }

            // Process quiet moves
            long quietDestinations = bl.getQuietMoves(start, position);
            while (quietDestinations != 0) {
                int destination = Long.numberOfTrailingZeros(quietDestinations);
                quietDestinations &= (quietDestinations - 1);

                int move = MoveShortcuts.generateBishopNoCapture(start, destination, position);
                firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
            }
        }

        return firstNonMove;
    }

    /**
     * Generates and returns all knight moves
     * @param position
     * @return Move list
     */
    private static int generateKnightMoves(Position position, int[] moveBuffer, int firstNonMove) {

        long knightList = position.pieceColors[position.activePlayer] & position.pieces[1];

        // Iterate over knight positions using bitwise manipulation
        while (knightList != 0L) {
            int start = Long.numberOfTrailingZeros(knightList);
            knightList &= (knightList - 1);

            // Process capture moves
            long captureDestinations = nl.getCaptures(start, position);
            while (captureDestinations != 0) {
                int destination = Long.numberOfTrailingZeros(captureDestinations);
                captureDestinations &= (captureDestinations - 1);
                int move = MoveShortcuts.generateKnightCapture(start, destination, position);
                firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
            }

            // Process quiet moves
            long quietDestinations = nl.getQuietMoves(start, position);
            while (quietDestinations != 0) {
                int destination = Long.numberOfTrailingZeros(quietDestinations);
                quietDestinations &= (quietDestinations - 1);

                int move = MoveShortcuts.generateKnightNoCapture(start, destination, position);
                firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
            }
        }

        return firstNonMove;
    }

    /**
     * Generates and returns all king moves
     * @param position
     * @return Move list
     */
    private static int generateKingMoves(Position position, int[] moveBuffer, int firstNonMove) {

        long kingList = position.pieceColors[position.activePlayer] & position.pieces[5];

        // Iterate over king positions using bitwise manipulation
        while (kingList != 0L) {
            int start = Long.numberOfTrailingZeros(kingList);
            kingList &= (kingList - 1);

            // Process capture moves
            long captureDestinations = kl.getCaptures(start, position);
            while (captureDestinations != 0) {
                int destination = Long.numberOfTrailingZeros(captureDestinations);
                captureDestinations &= (captureDestinations - 1);

                int move = MoveShortcuts.generateKingCapture(start, destination, position);
                firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
            }

            // Process quiet moves
            long quietDestinations = kl.getQuietMoves(start, position);
            while (quietDestinations != 0) {
                int destination = Long.numberOfTrailingZeros(quietDestinations);
                quietDestinations &= (quietDestinations - 1);

                int move = MoveShortcuts.generateKingNoCapture(start, destination, position);
                firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
            }

            // Process castling moves
            long castleDestinations = kl.generateCastles(start, position);
            while (castleDestinations != 0) {
                int destination = Long.numberOfTrailingZeros(castleDestinations);
                castleDestinations &= (castleDestinations - 1);

                // Check if they move through or are in check
                if (castleSquaresAttacked(position, destination))
                    continue;

                int move;

                if (destination % 8 == 2) { // Queen side castle
                    move = MoveShortcuts.generateKingQueenSideCastle(start, destination, position);
                } else { // King side castle
                    move = MoveShortcuts.generateKingKingSideCastle(start, destination, position);
                }

                firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
            }
        }

        return firstNonMove;
    }

    /**
     * Generates and returns all queen moves
     * @param position
     * @return Move list
     */
    public static int generateQueenMoves(Position position, int[] moveBuffer, int firstNonMove) {
        List<Move> generatedMoves = new ArrayList<Move>();
        long queenList = position.pieceColors[position.activePlayer] & position.pieces[4];

        // Iterate over queen positions using bitwise manipulation
        while (queenList != 0L) {
            int start = Long.numberOfTrailingZeros(queenList);
            queenList &= (queenList - 1);

            // Process rook-like capture and quiet moves (same as.pieces[3])
            long rookCaptureDestinations = rl.getCaptures(start, position);
            while (rookCaptureDestinations != 0) {
                int destination = Long.numberOfTrailingZeros(rookCaptureDestinations);
                rookCaptureDestinations &= (rookCaptureDestinations - 1);

                int move = MoveShortcuts.generateQueenCapture(start, destination, position);
                firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
            }

            long rookQuietDestinations = rl.getQuietMoves(start, position);
            while (rookQuietDestinations != 0) {
                int destination = Long.numberOfTrailingZeros(rookQuietDestinations);
                rookQuietDestinations &= (rookQuietDestinations - 1);

                int move = MoveShortcuts.generateQueenNoCapture(start, destination, position);
                firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
            }

            // Process bishop-like capture and quiet moves (same as.pieces[2])
            long bishopCaptureDestinations = bl.getCaptures(start, position);
            while (bishopCaptureDestinations != 0) {
                int destination = Long.numberOfTrailingZeros(bishopCaptureDestinations);
                bishopCaptureDestinations &= (bishopCaptureDestinations - 1);

                int move = MoveShortcuts.generateQueenCapture(start, destination, position);
                firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
            }

            long bishopQuietDestinations = bl.getQuietMoves(start, position);
            while (bishopQuietDestinations != 0) {
                int destination = Long.numberOfTrailingZeros(bishopQuietDestinations);
                bishopQuietDestinations &= (bishopQuietDestinations - 1);

                int move = MoveShortcuts.generateQueenNoCapture(start, destination, position);
                firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
            }
        }

        return firstNonMove;
    }


    public static long getPawnAttacks(Position position, int square, int attackColor)  {
        return pl.getAttackBoard(square, attackColor);
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
        boolean squareAttacked = false;
        if (position.activePlayer == 0 && position.inCheck) {
            return true;
        }
        if (position.activePlayer == 1 && position.inCheck) {
            return true;
        }

        if (destination == 2) {
            squareAttacked |= squareAttackedBy(position, 4, 1);
            squareAttacked |= squareAttackedBy(position, 3, 1);
        } else if (destination == 6) {
            squareAttacked |= squareAttackedBy(position, 4, 1);
            squareAttacked |= squareAttackedBy(position, 5, 1);
        } else if (destination == 58) {
            squareAttacked |= squareAttackedBy(position, 60, 0);
            squareAttacked |= squareAttackedBy(position, 59, 0);
        } else if (destination == 62) {
            squareAttacked |= squareAttackedBy(position, 60, 0);
            squareAttacked |= squareAttackedBy(position, 61, 0);
        }
        return squareAttacked;
    }

    /**
     * Returns if the king of the specified color is in check
     * @param position position to check
     * @param kingColor color of king
     * @return if the specified king is in check
     */
    public static boolean kingInCheck(Position position, int kingColor) {
        int kingLoc = Long.numberOfTrailingZeros(position.pieces[5] & position.pieceColors[kingColor]);
        if (kingLoc > 63 || kingLoc < 0) { // Checks if generated move was a king capture (we just return false so it's illegal)
            System.out.println("King CAPTURED!");
            return true;
        }
        return squareAttackedBy(position, kingLoc, 1 - kingColor);
    }

    /**
     * Returns if a square is attacked by the specified color
     * @param position position to check
     * @param square square to check
     * @param attackColor color of attacking pieces
     * @return squareAttacked if square is attacked by specified color
     */
    public static boolean squareAttackedBy(Position position, int square, int attackColor) {
        long potentialAttackers = position.pieceColors[attackColor];
        if (((pl.getAttackBoard(square, attackColor) & potentialAttackers & position.pieces[0]) != 0) ||
                ((bl.getAttackBoard(square, position) & potentialAttackers & (position.pieces[2] | position.pieces[4])) != 0) ||
                ((rl.getAttackBoard(square, position) & potentialAttackers & (position.pieces[3] | position.pieces[4])) != 0) ||
                ((nl.getAttackBoard(square, position) & potentialAttackers & position.pieces[1]) != 0) ||
                ((kl.getKingAttacks(square) & potentialAttackers & position.pieces[5]) != 0)) {
            return true;
        }
        return false;
    }

    /**
     * Does check detection on a move
     * @throws InvalidPositionException if position becomes invalid after make/unmake
     */
    public static void moveUpdateChecks(Move move, Position position) throws InvalidPositionException {
        //move.prevWhiteInCheck = position.whiteInCheck;
        //move.prevBlackInCheck = position.blackInCheck;
        //position.makeMove(move);
    /*
    if (move.movePiece == PieceType.KING) {
        // Check all pieces that could attack it
    } else {
        // Check if that piece attacks enemy king
        // Check all sliding pieces
    }
    */
        move.resultWhiteInCheck = kingInCheck(position, 0);
        move.resultBlackInCheck = kingInCheck(position, 0);
        //position.unMakeMove(move);
    }

	/*
	if king move, need to check all attackers
	if non-king move, need to check sliders and that piece
	*/
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
