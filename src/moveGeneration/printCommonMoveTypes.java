package moveGeneration;

import board.MoveEncoding;

public class printCommonMoveTypes {
    public static void main(String[] args) {
        int pawnPromotionNoCaptureTemplate = 0;
        //Start
        //destination
        pawnPromotionNoCaptureTemplate = MoveEncoding.setMovedPiece(pawnPromotionNoCaptureTemplate, 0);
        pawnPromotionNoCaptureTemplate = MoveEncoding.setCapturedPiece(pawnPromotionNoCaptureTemplate, 0);
        // PromotionType
        // Flags
        pawnPromotionNoCaptureTemplate = MoveEncoding.setIsQuiet(pawnPromotionNoCaptureTemplate, 0);
        pawnPromotionNoCaptureTemplate = MoveEncoding.setIsCapture(pawnPromotionNoCaptureTemplate, 0);
        pawnPromotionNoCaptureTemplate = MoveEncoding.setIsEP(pawnPromotionNoCaptureTemplate, 0);
        pawnPromotionNoCaptureTemplate = MoveEncoding.setIsPromotion(pawnPromotionNoCaptureTemplate, 1);
        pawnPromotionNoCaptureTemplate = MoveEncoding.setIsCastle(pawnPromotionNoCaptureTemplate, 0);
        pawnPromotionNoCaptureTemplate = MoveEncoding.setIsCheck(pawnPromotionNoCaptureTemplate, 0);
        pawnPromotionNoCaptureTemplate = MoveEncoding.setIsDoublePush(pawnPromotionNoCaptureTemplate, 0);
        pawnPromotionNoCaptureTemplate = MoveEncoding.setIsReversible(pawnPromotionNoCaptureTemplate, 0);
        pawnPromotionNoCaptureTemplate = MoveEncoding.setCaptureColor(pawnPromotionNoCaptureTemplate, 0);
        pawnPromotionNoCaptureTemplate = MoveEncoding.setCastleSide(pawnPromotionNoCaptureTemplate, 0);
        System.out.println("pawnPromotionNoCaptureTemplate = 0b" + Integer.toBinaryString(pawnPromotionNoCaptureTemplate) + ";");



        int pawnPromotionCaptureTemplate = 0;
        //Start
        //destination
        pawnPromotionCaptureTemplate = MoveEncoding.setMovedPiece(pawnPromotionCaptureTemplate, 0);
        pawnPromotionCaptureTemplate = MoveEncoding.setCapturedPiece(pawnPromotionCaptureTemplate, 0);
        // PromotionType
        pawnPromotionCaptureTemplate = MoveEncoding.setIsQuiet(pawnPromotionCaptureTemplate, 0);
        pawnPromotionCaptureTemplate = MoveEncoding.setIsCapture(pawnPromotionCaptureTemplate, 1);
        pawnPromotionCaptureTemplate = MoveEncoding.setIsEP(pawnPromotionCaptureTemplate, 0);
        pawnPromotionCaptureTemplate = MoveEncoding.setIsPromotion(pawnPromotionCaptureTemplate, 1);
        pawnPromotionCaptureTemplate = MoveEncoding.setIsCastle(pawnPromotionCaptureTemplate, 0);
        pawnPromotionCaptureTemplate = MoveEncoding.setIsCheck(pawnPromotionCaptureTemplate, 0);
        pawnPromotionCaptureTemplate = MoveEncoding.setIsDoublePush(pawnPromotionCaptureTemplate, 0);
        pawnPromotionCaptureTemplate = MoveEncoding.setIsReversible(pawnPromotionCaptureTemplate, 0);
        pawnPromotionCaptureTemplate = MoveEncoding.setCaptureColor(pawnPromotionCaptureTemplate, 0);
        pawnPromotionCaptureTemplate = MoveEncoding.setCastleSide(pawnPromotionCaptureTemplate, 0);
        System.out.println("pawnPromotionCaptureTemplate = 0b" + Integer.toBinaryString(pawnPromotionCaptureTemplate) + ";");

        int pawnDoublePushTemplate = 0;
        //Start
        //destination
        pawnDoublePushTemplate = MoveEncoding.setMovedPiece(pawnDoublePushTemplate, 0);
        pawnDoublePushTemplate = MoveEncoding.setCapturedPiece(pawnDoublePushTemplate, 0);
        // PromotionType
        pawnDoublePushTemplate = MoveEncoding.setIsQuiet(pawnDoublePushTemplate, 1);
        pawnDoublePushTemplate = MoveEncoding.setIsCapture(pawnDoublePushTemplate, 0);
        pawnDoublePushTemplate = MoveEncoding.setIsEP(pawnDoublePushTemplate, 0);
        pawnDoublePushTemplate = MoveEncoding.setIsPromotion(pawnDoublePushTemplate, 0);
        pawnDoublePushTemplate = MoveEncoding.setIsCastle(pawnDoublePushTemplate, 0);
        pawnDoublePushTemplate = MoveEncoding.setIsCheck(pawnDoublePushTemplate, 0);
        pawnDoublePushTemplate = MoveEncoding.setIsDoublePush(pawnDoublePushTemplate, 1);
        pawnDoublePushTemplate = MoveEncoding.setIsReversible(pawnDoublePushTemplate, 0);
        pawnDoublePushTemplate = MoveEncoding.setCaptureColor(pawnDoublePushTemplate, 0);
        pawnDoublePushTemplate = MoveEncoding.setCastleSide(pawnDoublePushTemplate, 0);
        System.out.println("pawnDoublePushTemplate = 0b" + Integer.toBinaryString(pawnDoublePushTemplate) + ";");

        int pawnSinglePushTemplate = 0;
        //Start
        //destination
        pawnSinglePushTemplate = MoveEncoding.setMovedPiece(pawnSinglePushTemplate, 0);
        pawnSinglePushTemplate = MoveEncoding.setCapturedPiece(pawnSinglePushTemplate, 0);
        // PromotionType
        pawnSinglePushTemplate = MoveEncoding.setIsQuiet(pawnSinglePushTemplate, 1);
        pawnSinglePushTemplate = MoveEncoding.setIsCapture(pawnSinglePushTemplate, 0);
        pawnSinglePushTemplate = MoveEncoding.setIsEP(pawnSinglePushTemplate, 0);
        pawnSinglePushTemplate = MoveEncoding.setIsPromotion(pawnSinglePushTemplate, 0);
        pawnSinglePushTemplate = MoveEncoding.setIsCastle(pawnSinglePushTemplate, 0);
        pawnSinglePushTemplate = MoveEncoding.setIsCheck(pawnSinglePushTemplate, 0);
        pawnSinglePushTemplate = MoveEncoding.setIsDoublePush(pawnSinglePushTemplate, 0);
        pawnSinglePushTemplate = MoveEncoding.setIsReversible(pawnSinglePushTemplate, 0);
        pawnSinglePushTemplate = MoveEncoding.setCaptureColor(pawnSinglePushTemplate, 0);
        pawnSinglePushTemplate = MoveEncoding.setCastleSide(pawnSinglePushTemplate, 0);
        System.out.println("pawnSinglePushTemplate = 0b" + Integer.toBinaryString(pawnSinglePushTemplate) + ";");

        int pawnCaptureTemplate = 0;
        //Start
        //destination
        pawnCaptureTemplate = MoveEncoding.setMovedPiece(pawnCaptureTemplate, 0);
        pawnCaptureTemplate = MoveEncoding.setCapturedPiece(pawnCaptureTemplate, 0);
        // PromotionType
        pawnCaptureTemplate = MoveEncoding.setIsQuiet(pawnCaptureTemplate, 0);
        pawnCaptureTemplate = MoveEncoding.setIsCapture(pawnCaptureTemplate, 1);
        pawnCaptureTemplate = MoveEncoding.setIsEP(pawnCaptureTemplate, 0);
        pawnCaptureTemplate = MoveEncoding.setIsPromotion(pawnCaptureTemplate, 0);
        pawnCaptureTemplate = MoveEncoding.setIsCastle(pawnCaptureTemplate, 0);
        pawnCaptureTemplate = MoveEncoding.setIsCheck(pawnCaptureTemplate, 0);
        pawnCaptureTemplate = MoveEncoding.setIsDoublePush(pawnCaptureTemplate, 0);
        pawnCaptureTemplate = MoveEncoding.setIsReversible(pawnCaptureTemplate, 0);
        pawnCaptureTemplate = MoveEncoding.setCaptureColor(pawnCaptureTemplate, 0);
        pawnCaptureTemplate = MoveEncoding.setCastleSide(pawnCaptureTemplate, 0);
        System.out.println("pawnCaptureTemplate = 0b" + Integer.toBinaryString(pawnCaptureTemplate) + ";");

        int knightNoCapture = 0;
        //Start
        //destination
        knightNoCapture = MoveEncoding.setMovedPiece(knightNoCapture, 1);
        knightNoCapture = MoveEncoding.setCapturedPiece(knightNoCapture, 0);
        // PromotionType
        knightNoCapture = MoveEncoding.setIsQuiet(knightNoCapture, 1);
        knightNoCapture = MoveEncoding.setIsCapture(knightNoCapture, 0);
        knightNoCapture = MoveEncoding.setIsEP(knightNoCapture, 0);
        knightNoCapture = MoveEncoding.setIsPromotion(knightNoCapture, 0);
        knightNoCapture = MoveEncoding.setIsCastle(knightNoCapture, 0);
        knightNoCapture = MoveEncoding.setIsCheck(knightNoCapture, 0);
        knightNoCapture = MoveEncoding.setIsDoublePush(knightNoCapture, 0);
        knightNoCapture = MoveEncoding.setIsReversible(knightNoCapture, 1);
        knightNoCapture = MoveEncoding.setCaptureColor(knightNoCapture, 0);
        knightNoCapture = MoveEncoding.setCastleSide(knightNoCapture, 0);
        System.out.println("knightNoCapture = 0b" + Integer.toBinaryString(knightNoCapture) + ";");

        int knightCapture = 0;
        //Start
        //destination
        knightCapture = MoveEncoding.setMovedPiece(knightCapture, 1);
        knightCapture = MoveEncoding.setCapturedPiece(knightCapture, 0);
        // PromotionType
        knightCapture = MoveEncoding.setIsQuiet(knightCapture, 0);
        knightCapture = MoveEncoding.setIsCapture(knightCapture, 1);
        knightCapture = MoveEncoding.setIsEP(knightCapture, 0);
        knightCapture = MoveEncoding.setIsPromotion(knightCapture, 0);
        knightCapture = MoveEncoding.setIsCastle(knightCapture, 0);
        knightCapture = MoveEncoding.setIsCheck(knightCapture, 0);
        knightCapture = MoveEncoding.setIsDoublePush(knightCapture, 0);
        knightCapture = MoveEncoding.setIsReversible(knightCapture, 0);
        knightCapture = MoveEncoding.setCaptureColor(knightCapture, 0);
        knightCapture = MoveEncoding.setCastleSide(knightCapture, 0);
        System.out.println("knightCapture = 0b" + Integer.toBinaryString(knightCapture) + ";");

        int bishopNoCapture = 0;
        //Start
        //destination
        bishopNoCapture = MoveEncoding.setMovedPiece(bishopNoCapture, 2);
        bishopNoCapture = MoveEncoding.setCapturedPiece(bishopNoCapture, 0);
        // PromotionType
        bishopNoCapture = MoveEncoding.setIsQuiet(bishopNoCapture, 1);
        bishopNoCapture = MoveEncoding.setIsCapture(bishopNoCapture, 0);
        bishopNoCapture = MoveEncoding.setIsEP(bishopNoCapture, 0);
        bishopNoCapture = MoveEncoding.setIsPromotion(bishopNoCapture, 0);
        bishopNoCapture = MoveEncoding.setIsCastle(bishopNoCapture, 0);
        bishopNoCapture = MoveEncoding.setIsCheck(bishopNoCapture, 0);
        bishopNoCapture = MoveEncoding.setIsDoublePush(bishopNoCapture, 0);
        bishopNoCapture = MoveEncoding.setIsReversible(bishopNoCapture, 1);
        bishopNoCapture = MoveEncoding.setCaptureColor(bishopNoCapture, 0);
        bishopNoCapture = MoveEncoding.setCastleSide(bishopNoCapture, 0);
        System.out.println("bishopNoCapture = 0b" + Integer.toBinaryString(bishopNoCapture) + ";");

        int bishopCapture = 0;
        //Start
        //destination
        bishopCapture = MoveEncoding.setMovedPiece(bishopCapture, 2);
        bishopCapture = MoveEncoding.setCapturedPiece(bishopCapture, 0);
        // PromotionType
        bishopCapture = MoveEncoding.setIsQuiet(bishopCapture, 0);
        bishopCapture = MoveEncoding.setIsCapture(bishopCapture, 1);
        bishopCapture = MoveEncoding.setIsEP(bishopCapture, 0);
        bishopCapture = MoveEncoding.setIsPromotion(bishopCapture, 0);
        bishopCapture = MoveEncoding.setIsCastle(bishopCapture, 0);
        bishopCapture = MoveEncoding.setIsCheck(bishopCapture, 0);
        bishopCapture = MoveEncoding.setIsDoublePush(bishopCapture, 0);
        bishopCapture = MoveEncoding.setIsReversible(bishopCapture, 0);
        bishopCapture = MoveEncoding.setCaptureColor(bishopCapture, 0);
        bishopCapture = MoveEncoding.setCastleSide(bishopCapture, 0);
        System.out.println("bishopCapture = 0b" + Integer.toBinaryString(bishopCapture) + ";");

        int rookNoCapture = 0;
        //Start
        //destination
        rookNoCapture = MoveEncoding.setMovedPiece(rookNoCapture, 3);
        rookNoCapture = MoveEncoding.setCapturedPiece(rookNoCapture, 0);
        // PromotionType
        rookNoCapture = MoveEncoding.setIsQuiet(rookNoCapture, 1);
        rookNoCapture = MoveEncoding.setIsCapture(rookNoCapture, 0);
        rookNoCapture = MoveEncoding.setIsEP(rookNoCapture, 0);
        rookNoCapture = MoveEncoding.setIsPromotion(rookNoCapture, 0);
        rookNoCapture = MoveEncoding.setIsCastle(rookNoCapture, 0);
        rookNoCapture = MoveEncoding.setIsCheck(rookNoCapture, 0);
        rookNoCapture = MoveEncoding.setIsDoublePush(rookNoCapture, 0);
        rookNoCapture = MoveEncoding.setIsReversible(rookNoCapture, 1);
        rookNoCapture = MoveEncoding.setCaptureColor(rookNoCapture, 0);
        rookNoCapture = MoveEncoding.setCastleSide(rookNoCapture, 0);
        System.out.println("rookNoCapture = 0b" + Integer.toBinaryString(rookNoCapture) + ";");

        int rookCapture = 0;
        //Start
        //destination
        rookCapture = MoveEncoding.setMovedPiece(rookCapture, 3);
        rookCapture = MoveEncoding.setCapturedPiece(rookCapture, 0);
        // PromotionType
        rookCapture = MoveEncoding.setIsQuiet(rookCapture, 0);
        rookCapture = MoveEncoding.setIsCapture(rookCapture, 1);
        rookCapture = MoveEncoding.setIsEP(rookCapture, 0);
        rookCapture = MoveEncoding.setIsPromotion(rookCapture, 0);
        rookCapture = MoveEncoding.setIsCastle(rookCapture, 0);
        rookCapture = MoveEncoding.setIsCheck(rookCapture, 0);
        rookCapture = MoveEncoding.setIsDoublePush(rookCapture, 0);
        rookCapture = MoveEncoding.setIsReversible(rookCapture, 0);
        rookCapture = MoveEncoding.setCaptureColor(rookCapture, 0);
        rookCapture = MoveEncoding.setCastleSide(rookCapture, 0);
        System.out.println("rookCapture = 0b" + Integer.toBinaryString(rookCapture) + ";");

        int queenNoCapture = 0;
        //Start
        //destination
        queenNoCapture = MoveEncoding.setMovedPiece(queenNoCapture, 4);
        queenNoCapture = MoveEncoding.setCapturedPiece(queenNoCapture, 0);
        // PromotionType
        queenNoCapture = MoveEncoding.setIsQuiet(queenNoCapture, 1);
        queenNoCapture = MoveEncoding.setIsCapture(queenNoCapture, 0);
        queenNoCapture = MoveEncoding.setIsEP(queenNoCapture, 0);
        queenNoCapture = MoveEncoding.setIsPromotion(queenNoCapture, 0);
        queenNoCapture = MoveEncoding.setIsCastle(queenNoCapture, 0);
        queenNoCapture = MoveEncoding.setIsCheck(queenNoCapture, 0);
        queenNoCapture = MoveEncoding.setIsDoublePush(queenNoCapture, 0);
        queenNoCapture = MoveEncoding.setIsReversible(queenNoCapture, 1);
        queenNoCapture = MoveEncoding.setCaptureColor(queenNoCapture, 0);
        queenNoCapture = MoveEncoding.setCastleSide(queenNoCapture, 0);
        System.out.println("queenNoCapture = 0b" + Integer.toBinaryString(queenNoCapture) + ";");

        int queenCapture = 0;
        //Start
        //destination
        queenCapture = MoveEncoding.setMovedPiece(queenCapture, 4);
        queenCapture = MoveEncoding.setCapturedPiece(queenCapture, 0);
        // PromotionType
        queenCapture = MoveEncoding.setIsQuiet(queenCapture, 0);
        queenCapture = MoveEncoding.setIsCapture(queenCapture, 1);
        queenCapture = MoveEncoding.setIsEP(queenCapture, 0);
        queenCapture = MoveEncoding.setIsPromotion(queenCapture, 0);
        queenCapture = MoveEncoding.setIsCastle(queenCapture, 0);
        queenCapture = MoveEncoding.setIsCheck(queenCapture, 0);
        queenCapture = MoveEncoding.setIsDoublePush(queenCapture, 0);
        queenCapture = MoveEncoding.setIsReversible(queenCapture, 0);
        queenCapture = MoveEncoding.setCaptureColor(queenCapture, 0);
        queenCapture = MoveEncoding.setCastleSide(queenCapture, 0);
        System.out.println("queenCapture = 0b" + Integer.toBinaryString(queenCapture) + ";");

        int kingNoCapture = 0;
        //Start
        //destination
        kingNoCapture = MoveEncoding.setMovedPiece(kingNoCapture, 5);
        kingNoCapture = MoveEncoding.setCapturedPiece(kingNoCapture, 0);
        // PromotionType
        kingNoCapture = MoveEncoding.setIsQuiet(kingNoCapture, 1);
        kingNoCapture = MoveEncoding.setIsCapture(kingNoCapture, 0);
        kingNoCapture = MoveEncoding.setIsEP(kingNoCapture, 0);
        kingNoCapture = MoveEncoding.setIsPromotion(kingNoCapture, 0);
        kingNoCapture = MoveEncoding.setIsCastle(kingNoCapture, 0);
        kingNoCapture = MoveEncoding.setIsCheck(kingNoCapture, 0);
        kingNoCapture = MoveEncoding.setIsDoublePush(kingNoCapture, 0);
        kingNoCapture = MoveEncoding.setIsReversible(kingNoCapture, 1);
        kingNoCapture = MoveEncoding.setCaptureColor(kingNoCapture, 0);
        kingNoCapture = MoveEncoding.setCastleSide(kingNoCapture, 0);
        System.out.println("kingNoCapture = 0b" + Integer.toBinaryString(kingNoCapture) + ";");

        int kingCapture = 0;
        //Start
        //destination
        kingCapture = MoveEncoding.setMovedPiece(kingCapture, 5);
        kingCapture = MoveEncoding.setCapturedPiece(kingCapture, 0);
        // PromotionType
        kingCapture = MoveEncoding.setIsQuiet(kingCapture, 0);
        kingCapture = MoveEncoding.setIsCapture(kingCapture, 1);
        kingCapture = MoveEncoding.setIsEP(kingCapture, 0);
        kingCapture = MoveEncoding.setIsPromotion(kingCapture, 0);
        kingCapture = MoveEncoding.setIsCastle(kingCapture, 0);
        kingCapture = MoveEncoding.setIsCheck(kingCapture, 0);
        kingCapture = MoveEncoding.setIsDoublePush(kingCapture, 0);
        kingCapture = MoveEncoding.setIsReversible(kingCapture, 0);
        kingCapture = MoveEncoding.setCaptureColor(kingCapture, 0);
        kingCapture = MoveEncoding.setCastleSide(kingCapture, 0);
        System.out.println("kingCapture = 0b" + Integer.toBinaryString(kingCapture) + ";");

        int kingKingSideCastle = 0;
        //Start
        //destination
        kingKingSideCastle = MoveEncoding.setMovedPiece(kingKingSideCastle, 5);
        kingKingSideCastle = MoveEncoding.setCapturedPiece(kingKingSideCastle, 0);
        // PromotionType
        kingKingSideCastle = MoveEncoding.setIsQuiet(kingKingSideCastle, 1);
        kingKingSideCastle = MoveEncoding.setIsCapture(kingKingSideCastle, 0);
        kingKingSideCastle = MoveEncoding.setIsEP(kingKingSideCastle, 0);
        kingKingSideCastle = MoveEncoding.setIsPromotion(kingKingSideCastle, 0);
        kingKingSideCastle = MoveEncoding.setIsCastle(kingKingSideCastle, 1);
        kingKingSideCastle = MoveEncoding.setIsCheck(kingKingSideCastle, 0);
        kingKingSideCastle = MoveEncoding.setIsDoublePush(kingKingSideCastle, 0);
        kingKingSideCastle = MoveEncoding.setIsReversible(kingKingSideCastle, 1);
        kingKingSideCastle = MoveEncoding.setCaptureColor(kingKingSideCastle, 0);
        kingKingSideCastle = MoveEncoding.setCastleSide(kingKingSideCastle, 0);
        System.out.println("kingKingSideCastle = 0b" + Integer.toBinaryString(kingKingSideCastle) + ";");

        int kingQueenSideCastle = 0;
        //Start
        //destination
        kingQueenSideCastle = MoveEncoding.setMovedPiece(kingQueenSideCastle, 5);
        kingQueenSideCastle = MoveEncoding.setCapturedPiece(kingQueenSideCastle, 0);
        // PromotionType
        kingQueenSideCastle = MoveEncoding.setIsQuiet(kingQueenSideCastle, 1);
        kingQueenSideCastle = MoveEncoding.setIsCapture(kingQueenSideCastle, 0);
        kingQueenSideCastle = MoveEncoding.setIsEP(kingQueenSideCastle, 0);
        kingQueenSideCastle = MoveEncoding.setIsPromotion(kingQueenSideCastle, 0);
        kingQueenSideCastle = MoveEncoding.setIsCastle(kingQueenSideCastle, 1);
        kingQueenSideCastle = MoveEncoding.setIsCheck(kingQueenSideCastle, 0);
        kingQueenSideCastle = MoveEncoding.setIsDoublePush(kingQueenSideCastle, 0);
        kingQueenSideCastle = MoveEncoding.setIsReversible(kingQueenSideCastle, 1);
        kingQueenSideCastle = MoveEncoding.setCaptureColor(kingQueenSideCastle, 0);
        kingQueenSideCastle = MoveEncoding.setCastleSide(kingQueenSideCastle, 1);
        System.out.println("kingQueenSideCastle = 0b" + Integer.toBinaryString(kingQueenSideCastle) + ";");


    }
}
