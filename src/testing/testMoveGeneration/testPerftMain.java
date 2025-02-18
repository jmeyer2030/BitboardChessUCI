package testing.testMoveGeneration;

import board.MoveEncoding;
import customExceptions.InvalidPositionException;
import moveGeneration.MoveGenerator;
import zobrist.Hashing;

import static testing.testMoveGeneration.quickTestPerft.*;

public class testPerftMain {
    public static void main(String[] args) {
        int depth = 7;
        //perftFromFen("rnbqk2r/pBpp1ppp/5n2/4p3/4P3/b7/PPPP1PPP/RNBQK1NR b KQkq - 0 4", depth, false);
        long result = perftStartingPosition(depth);
    }
}
// Depth 7: 51194
/*
public void makeMove(int move) {
    // Case null move:
    if (move == 0) {
        makeNullMove();
        return;
    }

    // Get Encoded data
    int start = MoveEncoding.getStart(move);
    int destination = MoveEncoding.getDestination(move);
    int movedPiece = MoveEncoding.getMovedPiece(move);
    int capturedPiece = MoveEncoding.getCapturedPiece(move);
    int promotionType = MoveEncoding.getPromotionType(move);
    boolean isCapture = MoveEncoding.getIsCapture(move);
    boolean isEP = MoveEncoding.getIsEP(move);
    boolean isPromotion = MoveEncoding.getIsPromotion(move);
    boolean isCastle = MoveEncoding.getIsCastle(move);
    //boolean isCheck = MoveEncoding.getIsCheck(move);
    boolean isDoublePush = MoveEncoding.getIsDoublePush(move);
    boolean isReversible = MoveEncoding.getIsReversible(move);
    int castleSide = MoveEncoding.getCastleSide(move);

    hmcStack.push(this.halfMoveCount);
    epStack.push(this.enPassant);
    castleRightsStack.push(this.castleRights);

    zobristHash ^= Hashing.castleRights[castleRights];

    if (enPassant != 0) {
        zobristHash ^= Hashing.enPassant[enPassant % 8];
    }


    // Increment hmc
    halfMoveCount++;

    // Remove capture
    if (isCapture) {
        removePiece(destination, capturedPiece, 1 - activePlayer);
    }

    // Remove start, add destination
    removePiece(start, movedPiece, activePlayer);
    addPiece(destination, movedPiece, activePlayer);

    // Handle specific move types
    if (!isReversible) {
        halfMoveCount = 0;
    }

    if (isEP) {
        // Remove pawn captured en Passant
        int enPassantCaptureSquare = enPassant - 8 + 16 * activePlayer; // if white - 8, else + 8
        removePiece(enPassantCaptureSquare, 0, 1 - activePlayer);
    }

    if (isPromotion) {
        // Remove pawn, add promotion piece
        removePiece(destination, movedPiece, activePlayer);
        addPiece(destination, promotionType, activePlayer);
    }

    if (isCastle) {
        // Move the rook
        int rookStart = castleRookStarts[activePlayer][castleSide];
        int rookDestination = castleRookDestinations[activePlayer][castleSide];
        removePiece(rookStart, 3, activePlayer);
        addPiece(rookDestination, 3, activePlayer);
    }

    if (movedPiece == 5) {
        kingLocs[activePlayer] = destination;
        // Change castle rights
        castleRights &= castleRightsMask[activePlayer];
    }

    if (movedPiece == 3) {
        // Change castle rights
        if (start == 0) {
            castleRights &= 0b0000_0111;
        } else if (start == 7) {
            castleRights &= 0b0000_1011;
        } else if (start == 56) {
            castleRights &= 0b0000_1101;
        } else if (start == 63) {
            castleRights &= 0b0000_1110;
        }
    }

    if (isDoublePush) {
        // Set EP square
        enPassant = destination - 8 + 16 * activePlayer;
        zobristHash ^= Hashing.enPassant[enPassant % 8];
    } else {
        enPassant = 0;
    }

    //increment moveCounter if black moved
    fullMoveCount += activePlayer;

    // Switch active player
    activePlayer = 1 - activePlayer;

    this.zobristHash ^= Hashing.sideToMove[1 - activePlayer];
    this.zobristHash ^= Hashing.sideToMove[activePlayer];
    this.zobristHash ^= Hashing.castleRights[castleRights];

    try {
        validPosition();
    } catch (InvalidPositionException ipe) {
        throw new IllegalStateException();
    }

    this.checkers = MoveGenerator.computeCheckers(this);
    this.inCheck = Long.numberOfTrailingZeros(checkers) == 64 ? false : true;
}

public void unMakeMove(int move) {
    if (move == 0) {
        unmakeNullMove();
        return;
    }
    // Get Encoded data
    int start = MoveEncoding.getStart(move);
    int destination = MoveEncoding.getDestination(move);
    int movedPiece = MoveEncoding.getMovedPiece(move);
    int capturedPiece = MoveEncoding.getCapturedPiece(move);
    int promotionType = MoveEncoding.getPromotionType(move);
    boolean isCapture = MoveEncoding.getIsCapture(move);
    boolean isEP = MoveEncoding.getIsEP(move);
    boolean isPromotion = MoveEncoding.getIsPromotion(move);
    boolean isCastle = MoveEncoding.getIsCastle(move);
    int castleSide = MoveEncoding.getCastleSide(move);
    //boolean wasInCheck = MoveEncoding.getWasInCheck(move);

    // Change active player
    this.activePlayer = 1 - activePlayer;

    zobristHash ^= Hashing.castleRights[castleRights];
    if (enPassant != 0) {
        zobristHash ^= Hashing.enPassant[enPassant % 8];
    }

    if (isPromotion) {
        removePiece(destination, promotionType, activePlayer);
    } else {
        removePiece(destination, movedPiece, activePlayer);
    }

    addPiece(start, movedPiece, activePlayer);

    if (isCapture) {
        addPiece(destination, capturedPiece, 1 - activePlayer);
    }

    if (isEP) {
        int enPassantCaptureSquare = destination - 8 + 16 * activePlayer;
        addPiece(enPassantCaptureSquare, 0, 1 - activePlayer);
    }

    if (isCastle) {
        int rookStart = castleRookStarts[activePlayer][castleSide];
        int rookDestination = castleRookDestinations[activePlayer][castleSide];
        removePiece(rookDestination, 3, activePlayer);
        addPiece(rookStart, 3, activePlayer);
    }

    if (movedPiece == 5) {
        kingLocs[activePlayer] = start;
    }

    this.castleRights = castleRightsStack.pop();
    this.halfMoveCount = hmcStack.pop();
    this.enPassant = epStack.pop();

    fullMoveCount -= activePlayer; // if black moved, decrement

    //this.inCheck = wasInCheck;
    this.checkers = MoveGenerator.computeCheckers(this);
    this.inCheck = Long.numberOfTrailingZeros(checkers) == 64 ? false : true;

    this.zobristHash ^= Hashing.sideToMove[1 - activePlayer];
    this.zobristHash ^= Hashing.sideToMove[activePlayer];
    this.zobristHash ^= Hashing.castleRights[castleRights];

    if (enPassant != 0) {
        zobristHash ^= Hashing.enPassant[enPassant % 8];
    }
}
*/