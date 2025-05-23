package moveGeneration;

import board.MoveEncoding;
import board.Position;

/**
* This class is used to generate moves based on a template so that we don't need to explicitely set all fields in MoveGenerator
*/
public class MoveShortcuts {
/*
    Move templates
*/
    public static final int pawnEnPassantTemplate =          0b00000000010000000000000000000000;
    public static final int pawnPromotionNoCaptureTemplate = 0b00000000100000000000000000000000;
    public static final int pawnPromotionCaptureTemplate =   0b00000000101000000000000000000000;
    public static final int pawnDoublePushTemplate =         0b00000100000100000000000000000000;
    public static final int pawnSinglePushTemplate =         0b00000000000100000000000000000000;
    public static final int pawnCaptureTemplate =            0b00000000001000000000000000000000;
    public static final int knightNoCaptureTemplate =        0b00001000000100000001000000000000;
    public static final int knightCaptureTemplate =          0b00000000001000000001000000000000;
    public static final int bishopNoCaptureTemplate =        0b00001000000100000010000000000000;
    public static final int bishopCaptureTemplate =          0b00000000001000000010000000000000;
    public static final int rookNoCaptureTemplate =          0b00001000000100000011000000000000;
    public static final int rookCaptureTemplate =            0b00000000001000000011000000000000;
    public static final int queenNoCaptureTemplate =         0b00001000000100000100000000000000;
    public static final int queenCaptureTemplate =           0b00000000001000000100000000000000;
    public static final int kingNoCaptureTemplate =          0b00001000000100000101000000000000;
    public static final int kingCaptureTemplate =            0b00000000001000000101000000000000;
    public static final int kingKingSideCastleTemplate =     0b00001001000100000101000000000000;
    public static final int kingQueenSideCastleTemplate =    0b00101001000100000101000000000000;

    public static int generatePawnPromotionNoCapture(int start, int destination, int promotionPiece, Position position) {
        int move = pawnPromotionNoCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setPromotionType(move, promotionPiece);
        move = MoveEncoding.setWasInCheck(move, position.inCheck ? 1 : 0);
        return move;
    }

    public static int generatePawnPromotionCapture(int start, int destination, int promotionPiece, Position position) {
        int move = pawnPromotionCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setPromotionType(move, promotionPiece);
        move = MoveEncoding.setCapturedPiece(move, position.getPieceType(destination));
        move = MoveEncoding.setCaptureColor(move, 1 - position.activePlayer);
        move = MoveEncoding.setWasInCheck(move, position.inCheck ? 1 : 0);
        move = MoveEncoding.setWasInCheck(move, position.inCheck ? 1 : 0);
        return move;
    }

    public static int generatePawnEnPassant(int start, int destination, Position position) {
        int move = pawnEnPassantTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setWasInCheck(move, position.inCheck ? 1 : 0);
        return move;
    }

    public static int generatePawnDoublePush(int start, int destination, Position position) {
        int move = pawnDoublePushTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setWasInCheck(move, position.inCheck ? 1 : 0);
        return move;
    }
    public static int generatePawnSinglePush(int start, int destination, Position position) {
        int move = pawnSinglePushTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setWasInCheck(move, position.inCheck ? 1 : 0);
        return move;
    }
    public static int generatePawnCapture(int start, int destination, Position position) {
        int move = pawnCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setCapturedPiece(move, position.getPieceType(destination));
        move = MoveEncoding.setCaptureColor(move, 1 - position.activePlayer);
        move = MoveEncoding.setWasInCheck(move, position.inCheck ? 1 : 0);
        return move;
    }
    public static int generateKnightNoCapture(int start, int destination, Position position) {
        int move = knightNoCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setWasInCheck(move, position.inCheck ? 1 : 0);
        return move;
    }

    public static int generateKnightCapture(int start, int destination, Position position) {
        int move = knightCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setCapturedPiece(move, position.getPieceType(destination));
        move = MoveEncoding.setCaptureColor(move, 1 - position.activePlayer);
        move = MoveEncoding.setWasInCheck(move, position.inCheck ? 1 : 0);
        return move;
    }

    public static int generateBishopNoCapture(int start, int destination, Position position) {
        int move = bishopNoCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setWasInCheck(move, position.inCheck ? 1 : 0);
        return move;
    }

    public static int generateBishopCapture(int start, int destination, Position position) {
        int move = bishopCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setCapturedPiece(move, position.getPieceType(destination));
        move = MoveEncoding.setCaptureColor(move, 1 - position.activePlayer);
        move = MoveEncoding.setWasInCheck(move, position.inCheck ? 1 : 0);
        return move;
    }

    public static int generateRookNoCapture(int start, int destination, Position position) {
        int move = rookNoCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setWasInCheck(move, position.inCheck ? 1 : 0);
        return move;
    }

    public static int generateRookCapture(int start, int destination, Position position) {
        int move = rookCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setCapturedPiece(move, position.getPieceType(destination));
        move = MoveEncoding.setCaptureColor(move, 1 - position.activePlayer);
        move = MoveEncoding.setWasInCheck(move, position.inCheck ? 1 : 0);
        return move;
    }

    public static int generateQueenNoCapture(int start, int destination, Position position) {
        int move = queenNoCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setWasInCheck(move, position.inCheck ? 1 : 0);
        return move;
    }

    public static int generateQueenCapture(int start, int destination, Position position) {
        int move = queenCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setCapturedPiece(move, position.getPieceType(destination));
        move = MoveEncoding.setCaptureColor(move, 1 - position.activePlayer);
        move = MoveEncoding.setWasInCheck(move, position.inCheck ? 1 : 0);
        return move;
    }

    public static int generateKingNoCapture(int start, int destination, Position position) {
        int move = kingNoCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setWasInCheck(move, position.inCheck ? 1 : 0);
        return move;
    }

    public static int generateKingCapture(int start, int destination, Position position) {
        int move = kingCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setCapturedPiece(move, position.getPieceType(destination));
        move = MoveEncoding.setCaptureColor(move, 1 - position.activePlayer);
        move = MoveEncoding.setWasInCheck(move, position.inCheck ? 1 : 0);
        return move;
    }

    public static int generateKingKingSideCastle(int start, int destination, Position position) {
        int move = kingKingSideCastleTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setWasInCheck(move, position.inCheck ? 1 : 0);
        return move;
    }

    public static int generateKingQueenSideCastle(int start, int destination, Position position) {
        int move = kingQueenSideCastleTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setWasInCheck(move, position.inCheck ? 1 : 0);
        return move;
    }
}
