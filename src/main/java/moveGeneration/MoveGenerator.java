package moveGeneration;

import board.*;

import java.util.Arrays;

public class MoveGenerator {

    /**
     * Returns the move object from a description in long algebraic notation (LAN)
     *
     * @param lan      move described in Long Algebraic Notation
     * @param position position that the move would be played on
     * @return the described move object
     */
    public static int getMoveFromLAN(String lan, Position position, int[] moveBuffer) {
        int firstNonMove = generateAllMoves(position, moveBuffer, 0);

        for (int i = 0; i < firstNonMove; i++) {
            int move = moveBuffer[i];
            if (MoveEncoding.getLAN(move).equals(lan)) return move;
        }

        // Case that no generated move matches the lan description
        throw new IllegalArgumentException();
    }

    /**
     * Generates all legal moves and puts them in the move buffer starting at firstNonMove
     * Returns the index of the new firstNonMove
     *
     * @param position     position to find moves for
     * @param moveBuffer   place to put moves
     * @param firstNonMove location to put the first move
     * @return new first non move
     */
    public static int generateAllMoves(Position position, int[] moveBuffer, int firstNonMove) {
        computePins(position);

        int numCheckers = Long.bitCount(position.checkers);

        if (numCheckers == 0) {
            return generateMovesNoChecks(position, moveBuffer, firstNonMove);
        } else if (numCheckers == 1) {
            int checkerSquare = Long.numberOfTrailingZeros(position.checkers);
            int checkerType = position.getPieceType(checkerSquare);

            // Capturing the piece is allowed
            long validDestinations = 1L << checkerSquare;
            // If sliding, intercepting the attack is allowed
            if (checkerType == 2 || checkerType == 3 || checkerType == 4) {
                validDestinations |= AbsolutePins.inBetween[position.kingLocs[position.activePlayer]][checkerSquare];
            }
            return generateMovesOneCheck(position, moveBuffer, firstNonMove, validDestinations);
        } else {
            return generateMovesDoubleCheck(position, moveBuffer, firstNonMove);
        }
    }

    private static int generateMovesNoChecks(Position position, int[] moveBuffer, int firstNonMove) {
        firstNonMove = generatePawnMoves(position, moveBuffer, firstNonMove, ~0L, false);
        firstNonMove = generateKnightMoves(position, moveBuffer, firstNonMove, ~0L, false);
        firstNonMove = generateBishopMoves(position, moveBuffer, firstNonMove, ~0L, false);
        firstNonMove = generateRookMoves(position, moveBuffer, firstNonMove, ~0L, false);
        firstNonMove = generateQueenMoves(position, moveBuffer, firstNonMove, ~0L, false);
        firstNonMove = generateKingMoves(position, moveBuffer, firstNonMove, false, false);
        return firstNonMove;
    }

    private static int generateMovesOneCheck(Position position, int[] moveBuffer, int firstNonMove, long checkHandleMask) {
        firstNonMove = generatePawnMoves(position, moveBuffer, firstNonMove, checkHandleMask, false);
        firstNonMove = generateKnightMoves(position, moveBuffer, firstNonMove, checkHandleMask, false);
        firstNonMove = generateBishopMoves(position, moveBuffer, firstNonMove, checkHandleMask, false);
        firstNonMove = generateRookMoves(position, moveBuffer, firstNonMove, checkHandleMask, false);
        firstNonMove = generateQueenMoves(position, moveBuffer, firstNonMove, checkHandleMask, false);
        firstNonMove = generateKingMoves(position, moveBuffer, firstNonMove, true, false);
        return firstNonMove;
    }

    private static int generateMovesDoubleCheck(Position position, int[] moveBuffer, int firstNonMove) {
        return generateKingMoves(position, moveBuffer, firstNonMove, true, false);
    }

    /**
     * Specialized move generation function for Quiescence search
     * IF in check:
     * - Generate all moves (we want to consider all evasions)
     * ELSE:
     * - Generate only captures
     * <p>
     * We can generate
     */
    public static int generateQSearchMoves(Position position, int[] moveBuffer, int firstNonMove) {
        int numCheckers = Long.bitCount(position.checkers);

        if (numCheckers == 0) {
            computePins(position);
            firstNonMove = generatePawnMoves(position, moveBuffer, firstNonMove, ~0L, true);
            firstNonMove = generateKnightMoves(position, moveBuffer, firstNonMove, ~0L, true);
            firstNonMove = generateBishopMoves(position, moveBuffer, firstNonMove, ~0L, true);
            firstNonMove = generateRookMoves(position, moveBuffer, firstNonMove, ~0L, true);
            firstNonMove = generateQueenMoves(position, moveBuffer, firstNonMove, ~0L, true);
            firstNonMove = generateKingMoves(position, moveBuffer, firstNonMove, false, true);
            return firstNonMove;
        } else {
            return generateAllMoves(position, moveBuffer, firstNonMove);
        }
    }

    /**
     * Generates a bitboard representing the locations of checkers of the active player's king
     */
    public static long computeCheckers(Position position) {
        int kingSquare = position.kingLocs[position.activePlayer];
        // Get intersections of attacks from king and enemy pieces of the correct type
        long pawnAttacks = PawnLogic.getAttackBoard(kingSquare, position.activePlayer) & position.pieces[0] & position.pieceColors[1 - position.activePlayer];
        long knightAttacks = KnightLogic.getAttackBoard(kingSquare, position) & position.pieces[1] & position.pieceColors[1 - position.activePlayer];
        long bishopAttacks = BishopLogic.getAttackBoard(kingSquare, position.occupancy) & (position.pieces[2] | position.pieces[4]) & position.pieceColors[1 - position.activePlayer];
        long rookAttacks = RookLogic.getAttackBoard(kingSquare, position.occupancy) & (position.pieces[3] | position.pieces[4]) & position.pieceColors[1 - position.activePlayer];

        return pawnAttacks | knightAttacks | bishopAttacks | rookAttacks;
    }

    /**
     * Generates a bitboard representing the locations of attackers of a square.
     *
     * @param position position to check
     * @param square   to check for attackers
     * @return bitboard representing pieces of attackerColor that are attacking square in position
     */
    public static long getSEEAttackers(Position position, int square) {
        // Get intersections of attacks from king and enemy pieces of the correct type
        long pawnAttacks = PawnLogic.getAttackBoard(square, 0) & position.pieces[0] & position.pieceColors[1];
        pawnAttacks |= PawnLogic.getAttackBoard(square, 1) & position.pieces[0] & position.pieceColors[0];
        long knightAttacks = KnightLogic.getAttackBoard(square, position) & position.pieces[1];
        long bishopAttacks = BishopLogic.getAttackBoard(square, position.occupancy) & (position.pieces[2] | position.pieces[4]);
        long rookAttacks = RookLogic.getAttackBoard(square, position.occupancy) & (position.pieces[3] | position.pieces[4]);
        long kingAttacks = KingLogic.getKingAttacks(square);

        return pawnAttacks | knightAttacks | bishopAttacks | rookAttacks | kingAttacks;
    }

    /**
     * Returns true if the king would be attacked if it moved to the move's destination
     *
     * @param move     move to check
     * @param position the move is applied to
     * @return if the move puts themselves in check
     */
    public static boolean kingMoveSelfInCheck(int move, Position position, int kingColor) {
        int kingLoc = Long.numberOfTrailingZeros(position.pieceColors[kingColor] & position.pieces[5]);
        long removedKingOccupancy = position.occupancy & ~(1L << kingLoc);
        long potentialAttackers = position.pieceColors[1 - kingColor];
        int destination = MoveEncoding.getDestination(move);

        return ((PawnLogic.getAttackBoard(destination, kingColor) & potentialAttackers & position.pieces[0]) != 0) || // Look at king color pawn move FROM the king's loc
                ((BishopLogic.getAttackBoard(destination, removedKingOccupancy) & potentialAttackers & (position.pieces[2] | position.pieces[4])) != 0) || ((RookLogic.getAttackBoard(destination, removedKingOccupancy) & potentialAttackers & (position.pieces[3] | position.pieces[4])) != 0) || ((KnightLogic.getAttackBoard(destination, position) & potentialAttackers & position.pieces[1]) != 0) || ((KingLogic.getKingAttacks(destination) & potentialAttackers & position.pieces[5]) != 0);
    }

    /**
     * Updates checks on the move and sees if its legal. If it's valid, it is added to the move buffer
     * and the new firstNonMove of the array is returned.
     *
     * @param move         move to add
     * @param moveBuffer   buffer to add to
     * @param firstNonMove index to add to
     * @param position     position move is applied to
     * @return new buffer location
     */
    private static int addAndValidateMove(int move, int[] moveBuffer, int firstNonMove, Position position) {
        moveBuffer[firstNonMove] = move;
        return ++firstNonMove;
    }

    /**
     * Checks if an EP move puts self in check
     * Simulates it and sees if self in check by discovery or
     */
    private static boolean epIsValid(int start, int destination, Position position, long checkHandleMask) {
        int epCaptureSquare = position.enPassant - 8 + 16 * position.activePlayer;
        long modifiedOccupancy = position.occupancy ^ (1L << epCaptureSquare) ^ (1L << start) | (1L << destination); // Simulate the move

        // First check if the move causes a bishop check
        long kingBishopView = BishopLogic.getAttackBoard(position.kingLocs[position.activePlayer], modifiedOccupancy); // get attacks from the king (as bishop)
        long attackers = (position.pieces[2] | position.pieces[4]) & position.pieceColors[1 - position.activePlayer];
        if ((attackers & kingBishopView) != 0) {
            return false;
        }

        // then check if it causes a rook check
        long kingRookView = RookLogic.getAttackBoard(position.kingLocs[position.activePlayer], modifiedOccupancy);
        attackers = (position.pieces[3] | position.pieces[4]) & position.pieceColors[1 - position.activePlayer];
        if ((attackers & kingRookView) != 0) {
            return false;
        }

        // fail if checker is non-slider AND it isn't captured
        if (Long.bitCount(checkHandleMask) == 1 && Long.numberOfTrailingZeros(checkHandleMask) != epCaptureSquare) {
            return false;
        }

        // resultant isn't attacked by a slider, or the attacker was stopped by
        return true;
    }


    /**
     * Generates and returns all pawn moves
     * En Passant moves have the issue that they remove two pieces on the same rank which can lead to a discovered attack
     * Additionally the captured piece isn't the square that the pawn moves to, leading to not seeing an ep as capturing a checker
     * These are handled manually in the ep generation section
     * <p>
     * For EP, we need to make sure:
     * - new occupancy doesn't cause a discovery on the same rank
     * - removing the captured pawn doesn't cause a bishop discovery
     * - capturing the pawn is considered removing a checker
     *
     * @param position     to generate moves for
     * @param moveBuffer   to fill moves
     * @param firstNonMove index of moveBuffer to start adding
     * @return firstNonMove updated
     */
    private static int generatePawnMoves(Position position, int[] moveBuffer, int firstNonMove, long checkHandleMask, boolean onlyCaptures) {
        long pawnList = position.pieces[0] & position.pieceColors[position.activePlayer];

        while (pawnList != 0L) {
            int start = Long.numberOfTrailingZeros(pawnList);

            long pinMask = ~0L;
            if (position.pinnedPieces[start] != -1) {
                pinMask = AbsolutePins.inBetween[position.kingLocs[position.activePlayer]][position.pinnedPieces[start]] | (1L << position.pinnedPieces[start]);
            }

            pawnList &= (pawnList - 1);

            // Handle captures
            long destinations = PawnLogic.getCaptures(start, position) & pinMask & checkHandleMask;
            while (destinations != 0) {
                int destination = Long.numberOfTrailingZeros(destinations);
                destinations &= (destinations - 1);

                if (destination / 8 == 0 || destination / 8 == 7) {
                    int moveN = MoveShortcuts.generatePawnPromotionCapture(start, destination, 1, position);
                    int moveB = MoveShortcuts.generatePawnPromotionCapture(start, destination, 2, position);
                    int moveR = MoveShortcuts.generatePawnPromotionCapture(start, destination, 3, position);
                    int moveQ = MoveShortcuts.generatePawnPromotionCapture(start, destination, 4, position);

                    firstNonMove = addAndValidateMove(moveN, moveBuffer, firstNonMove, position);
                    firstNonMove = addAndValidateMove(moveB, moveBuffer, firstNonMove, position);
                    firstNonMove = addAndValidateMove(moveR, moveBuffer, firstNonMove, position);
                    firstNonMove = addAndValidateMove(moveQ, moveBuffer, firstNonMove, position);

                } else {
                    int move = MoveShortcuts.generatePawnCapture(start, destination, position);
                    firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
                }
            }

            // Handle En Passant
            long enPassantMoves = PawnLogic.getEnPassant(start, position) & pinMask;
            while (enPassantMoves != 0) {
                int destination = Long.numberOfTrailingZeros(enPassantMoves);
                enPassantMoves &= (enPassantMoves - 1);
                int move = MoveShortcuts.generatePawnEnPassant(start, destination, position);
                if (epIsValid(start, destination, position, checkHandleMask)) {
                    firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
                }
            }
            if (onlyCaptures) {
                continue;
            }

            // Handle Quiet Moves
            long quietMoves = PawnLogic.getQuietMoves(start, position) & pinMask & checkHandleMask;
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
        }
        return firstNonMove;
    }

    /**
     * Generates and returns all Rook moves
     *
     * @param position     to generate moves for
     * @param moveBuffer   to fill moves
     * @param firstNonMove index of moveBuffer to start adding
     * @return updated firstNonMove
     */
    private static int generateRookMoves(Position position, int[] moveBuffer, int firstNonMove, long checkHandleMask, boolean onlyCaptures) {
        long rookList = (position.pieceColors[position.activePlayer]) & position.pieces[3];
        // Iterate over the rook positions using bitwise manipulation
        while (rookList != 0L) {
            int start = Long.numberOfTrailingZeros(rookList);
            rookList &= (rookList - 1);

            long pinMask = ~0L;
            if (position.pinnedPieces[start] != -1) {
                pinMask = AbsolutePins.inBetween[position.kingLocs[position.activePlayer]][position.pinnedPieces[start]] | (1L << position.pinnedPieces[start]);
            }

            long moves = RookLogic.getMoveBoard(start, position) & pinMask & checkHandleMask;

            // Process capture moves
            long captureDestinations = moves & position.pieceColors[1 - position.activePlayer];
            while (captureDestinations != 0) {
                int destination = Long.numberOfTrailingZeros(captureDestinations);
                captureDestinations &= (captureDestinations - 1);

                int move = MoveShortcuts.generateRookCapture(start, destination, position);
                firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
            }

            if (onlyCaptures) {
                continue;
            }

            // Process quiet moves
            long quietDestinations = moves & ~position.occupancy;
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
     *
     * @param position to generate moves for
     * @return Move list
     */
    private static int generateBishopMoves(Position position, int[] moveBuffer, int firstNonMove, long checkHandleMask, boolean onlyCaptures) {
        long bishopList = position.pieceColors[position.activePlayer] & position.pieces[2];
        // Iterate over bishop positions using bitwise manipulation
        while (bishopList != 0L) {
            int start = Long.numberOfTrailingZeros(bishopList);
            bishopList &= (bishopList - 1);

            long pinMask = ~0L;
            if (position.pinnedPieces[start] != -1) {
                pinMask = AbsolutePins.inBetween[position.kingLocs[position.activePlayer]][position.pinnedPieces[start]] | (1L << position.pinnedPieces[start]);
            }

            long moves = BishopLogic.getMoveBoard(start, position) & pinMask & checkHandleMask;

            // Process capture moves
            long captureDestinations = moves & position.pieceColors[1 - position.activePlayer];
            while (captureDestinations != 0) {
                int destination = Long.numberOfTrailingZeros(captureDestinations);
                captureDestinations &= (captureDestinations - 1);

                int move = MoveShortcuts.generateBishopCapture(start, destination, position);
                firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
            }

            if (onlyCaptures) {
                continue;
            }

            // Process quiet moves
            long quietDestinations = moves & ~position.occupancy;
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
     *
     * @param position to generate moves for
     * @return Move list
     */
    private static int generateKnightMoves(Position position, int[] moveBuffer, int firstNonMove, long checkHandleMask, boolean onlyCaptures) {
        long knightList = position.pieceColors[position.activePlayer] & position.pieces[1];

        // Iterate over knight positions using bitwise manipulation
        while (knightList != 0L) {
            int start = Long.numberOfTrailingZeros(knightList);
            knightList &= (knightList - 1);

            long pinMask = ~0L;
            if (position.pinnedPieces[start] != -1) {
                pinMask = AbsolutePins.inBetween[position.kingLocs[position.activePlayer]][position.pinnedPieces[start]] | (1L << position.pinnedPieces[start]);
            }

            if (onlyCaptures) {
                continue;
            }

            // Process capture moves
            long captureDestinations = KnightLogic.getCaptures(start, position) & pinMask & checkHandleMask;
            while (captureDestinations != 0) {
                int destination = Long.numberOfTrailingZeros(captureDestinations);
                captureDestinations &= (captureDestinations - 1);
                int move = MoveShortcuts.generateKnightCapture(start, destination, position);
                firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
            }

            // Process quiet moves
            long quietDestinations = KnightLogic.getQuietMoves(start, position) & pinMask & checkHandleMask;
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
     *
     * @param position position to generate moves for
     * @return Move list
     */
    private static int generateKingMoves(Position position, int[] moveBuffer, int firstNonMove, boolean inCheck, boolean onlyCaptures) {
        long kingList = position.pieceColors[position.activePlayer] & position.pieces[5];

        // Iterate over king positions using bitwise manipulation
        while (kingList != 0L) {
            int start = Long.numberOfTrailingZeros(kingList);
            kingList &= (kingList - 1);

            // Process capture moves
            long captureDestinations = KingLogic.getCaptures(start, position);
            while (captureDestinations != 0) {
                int destination = Long.numberOfTrailingZeros(captureDestinations);
                captureDestinations &= (captureDestinations - 1);


                int move = MoveShortcuts.generateKingCapture(start, destination, position);
                if (!kingMoveSelfInCheck(move, position, position.activePlayer))
                    firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
            }

            if (onlyCaptures) {
                continue;
            }

            // Process quiet moves
            long quietDestinations = KingLogic.getQuietMoves(start, position);
            while (quietDestinations != 0) {
                int destination = Long.numberOfTrailingZeros(quietDestinations);
                quietDestinations &= (quietDestinations - 1);

                int move = MoveShortcuts.generateKingNoCapture(start, destination, position);

                if (!kingMoveSelfInCheck(move, position, position.activePlayer))
                    firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
            }

            // Process castling moves
            long castleDestinations = KingLogic.generateCastles(start, position);
            while (castleDestinations != 0) {
                int destination = Long.numberOfTrailingZeros(castleDestinations);
                castleDestinations &= (castleDestinations - 1);

                // Check if they move through or are in check
                if (castleSquaresAttacked(position, destination, inCheck)) continue;

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
     *
     * @param position to generate moves for
     * @return Move list
     */
    public static int generateQueenMoves(Position position, int[] moveBuffer, int firstNonMove, long checkHandleMask, boolean onlyCaptures) {
        long queenList = position.pieceColors[position.activePlayer] & position.pieces[4];

        // Iterate over queen positions using bitwise manipulation
        while (queenList != 0L) {
            int start = Long.numberOfTrailingZeros(queenList);
            queenList &= (queenList - 1);

            long pinMask = ~0L;
            if (position.pinnedPieces[start] != -1) {
                pinMask = AbsolutePins.inBetween[position.kingLocs[position.activePlayer]][position.pinnedPieces[start]] | (1L << position.pinnedPieces[start]);
            }

            long moves = (RookLogic.getMoveBoard(start, position) | BishopLogic.getMoveBoard(start, position)) & pinMask & checkHandleMask;

            // Process capture moves
            long captureDestinations = moves & position.pieceColors[1 - position.activePlayer];
            while (captureDestinations != 0) {
                int destination = Long.numberOfTrailingZeros(captureDestinations);
                captureDestinations &= (captureDestinations - 1);

                int move = MoveShortcuts.generateQueenCapture(start, destination, position);
                firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
            }

            if (onlyCaptures) {
                continue;
            }

            // Process quiet moves
            long quietDestinations = moves & ~position.occupancy;
            while (quietDestinations != 0) {
                int destination = Long.numberOfTrailingZeros(quietDestinations);
                quietDestinations &= (quietDestinations - 1);

                int move = MoveShortcuts.generateQueenNoCapture(start, destination, position);
                firstNonMove = addAndValidateMove(move, moveBuffer, firstNonMove, position);
            }
        }

        return firstNonMove;
    }


    /**
     * Returns if castle squares are attacked
     * Only checks starting square and move-through square
     *
     * @param position    position to check
     * @param destination destination of the king
     * @return if the castle move is valid
     */
    private static boolean castleSquaresAttacked(Position position, int destination, boolean inCheck) {
        boolean squareAttacked = false;
        if (inCheck) {
            return true;
        }
        if (destination == 2) {
            squareAttacked |= squareAttackedBy(position, 3, 1);
            squareAttacked |= squareAttackedBy(position, 2, 1);
        } else if (destination == 6) {
            squareAttacked |= squareAttackedBy(position, 5, 1);
            squareAttacked |= squareAttackedBy(position, 6, 1);
        } else if (destination == 58) {
            squareAttacked |= squareAttackedBy(position, 59, 0);
            squareAttacked |= squareAttackedBy(position, 58, 0);
        } else if (destination == 62) {
            squareAttacked |= squareAttackedBy(position, 61, 0);
            squareAttacked |= squareAttackedBy(position, 62, 0);
        }
        return squareAttacked;
    }


    /**
     * Returns if a square is attacked by the specified color
     *
     * @param position    position to check
     * @param square      square to check
     * @param attackColor color of attacking pieces
     * @return squareAttacked if square is attacked by specified color
     */
    public static boolean squareAttackedBy(Position position, int square, int attackColor) {
        long potentialAttackers = position.pieceColors[attackColor];
        return ((PawnLogic.getAttackBoard(square, 1 - attackColor) & potentialAttackers & position.pieces[0]) != 0) || ((BishopLogic.getAttackBoard(square, position) & potentialAttackers & (position.pieces[2] | position.pieces[4])) != 0) || ((RookLogic.getAttackBoard(square, position) & potentialAttackers & (position.pieces[3] | position.pieces[4])) != 0) || ((KnightLogic.getAttackBoard(square, position) & potentialAttackers & position.pieces[1]) != 0) || ((KingLogic.getKingAttacks(square) & potentialAttackers & position.pieces[5]) != 0);
    }

    /**
     * Returns if a king of the specified color is attacked
     *
     * @param position  position to check
     * @param kingColor color of the king to check
     * @return true if the king is attacked
     */
    public static boolean kingAttacked(Position position, int kingColor) {
        int square = position.kingLocs[kingColor];
        int attackColor = 1 - kingColor;
        return squareAttackedBy(position, square, attackColor);
    }

    /**
     * A pinned piece is a piece that is movement restricted because it is intercepting an attack that would otherwise
     * hit the active player's king.
     * Populates position.pinnedPieces[0:63] with either the square of the pinning piece, or -1 where the index is the pinned piece location.
     *
     * @param position position to compute pins on
     */
    public static void computePins(Position position) {
        // First reset locations of pinned pieces
        Arrays.fill(position.pinnedPieces, -1);

        // Get potential pinners that move like a rook
        long rookAttackPieces = (position.pieces[3] | position.pieces[4]) & position.pieceColors[1 - position.activePlayer];

        // Get xray attacks if the king moved like a rook
        long rookXRay = RookLogic.xrayAttacks(position.kingLocs[position.activePlayer], position);

        // Get intersection of rooks and xray
        long pinningPieces = rookAttackPieces & rookXRay;

        while (pinningPieces != 0) {
            int rookAttackPiece = Long.numberOfTrailingZeros(pinningPieces);
            pinningPieces &= (pinningPieces - 1);

            int blockerPiece = Long.numberOfTrailingZeros(AbsolutePins.inBetween[rookAttackPiece][position.kingLocs[position.activePlayer]] & position.pieceColors[position.activePlayer]);

            // Case that the attack isn't blocked
            if (blockerPiece == 64) {
                continue;
            }

            position.pinnedPieces[blockerPiece] = rookAttackPiece;
        }

        long bishopAttackPieces = (position.pieces[2] | position.pieces[4]) & position.pieceColors[1 - position.activePlayer];

        long bishopXRay = BishopLogic.xrayAttacks(position.kingLocs[position.activePlayer], position);

        pinningPieces = bishopAttackPieces & bishopXRay;

        while (pinningPieces != 0) {
            int bishopAttackPiece = Long.numberOfTrailingZeros(pinningPieces);
            int blockerPiece = Long.numberOfTrailingZeros(AbsolutePins.inBetween[bishopAttackPiece][position.kingLocs[position.activePlayer]] & position.pieceColors[position.activePlayer]);

            pinningPieces &= (pinningPieces - 1);
            if (blockerPiece == 64) {
                continue;
            }

            position.pinnedPieces[blockerPiece] = bishopAttackPiece;
        }
    }
}
