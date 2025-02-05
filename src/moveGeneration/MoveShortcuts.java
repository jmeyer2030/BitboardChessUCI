package moveGeneration;

import board.MoveEncoding;
import board.Position;

public class MoveShortcuts {
//                                                           0b00000000000000000000000000000000;
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

    public static int generatePawnPromotionNoCapture(int start, int destination, int promotionPiece) {
        int move = pawnPromotionNoCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setPromotionType(move, promotionPiece);
        return move;
    }

    public static int generatePawnPromotionCapture(int start, int destination, int promotionPiece, Position position) {
        int move = pawnPromotionCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setPromotionType(move, promotionPiece);
        move = MoveEncoding.setCapturedPiece(move, position.getPieceType(destination));
        move = MoveEncoding.setCaptureColor(move, 1 - position.activePlayer.ordinal());
        return move;
    }

    public static int generatePawnDoublePush(int start, int destination) {
        int move = pawnDoublePushTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        return move;
    }
    public static int generatePawnSinglePush(int start, int destination) {
        int move = pawnSinglePushTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        return move;
    }
    public static int generatePawnCapture(int start, int destination, Position position) {
        int move = pawnCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setCapturedPiece(move, position.getPieceType(destination));
        move = MoveEncoding.setCaptureColor(move, 1 - position.activePlayer.ordinal());
        return move;
    }
    public static int generateKnightNoCapture(int start, int destination) {
        int move = knightNoCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        return move;
    }

    public static int generateKnightCapture(int start, int destination, Position position) {
        int move = knightCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setCapturedPiece(move, position.getPieceType(destination));
        move = MoveEncoding.setCaptureColor(move, 1 - position.activePlayer.ordinal());
        return move;
    }

    public static int generateBishopNoCapture(int start, int destination) {
        int move = bishopNoCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        return move;
    }

    public static int generateBishopCapture(int start, int destination, Position position) {
        int move = bishopCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setCapturedPiece(move, position.getPieceType(destination));
        move = MoveEncoding.setCaptureColor(move, 1 - position.activePlayer.ordinal());
        return move;
    }

    public static int generateRookNoCapture(int start, int destination) {
        int move = rookNoCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        return move;
    }

    public static int generateRookCapture(int start, int destination, Position position) {
        int move = rookCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setCapturedPiece(move, position.getPieceType(destination));
        move = MoveEncoding.setCaptureColor(move, 1 - position.activePlayer.ordinal());
        return move;
    }

    public static int generateQueenNoCapture(int start, int destination) {
        int move = queenNoCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        return move;
    }

    public static int generateQueenCapture(int start, int destination, Position position) {
        int move = queenCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setCapturedPiece(move, position.getPieceType(destination));
        move = MoveEncoding.setCaptureColor(move, 1 - position.activePlayer.ordinal());
        return move;
    }

    public static int generateKingNoCapture(int start, int destination) {
        int move = kingNoCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        return move;
    }

    public static int generateKingCapture(int start, int destination, Position position) {
        int move = kingCaptureTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        move = MoveEncoding.setCapturedPiece(move, position.getPieceType(destination));
        move = MoveEncoding.setCaptureColor(move, 1 - position.activePlayer.ordinal());
        return move;
    }

    public static int generateKingKingSideCastle(int start, int destination) {
        int move = kingKingSideCastleTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        return move;
    }

    public static int generateKingQueenSideCastle(int start, int destination) {
        int move = kingQueenSideCastleTemplate;
        move = MoveEncoding.setStart(move, start);
        move = MoveEncoding.setDestination(move, destination);
        return move;
    }
}
