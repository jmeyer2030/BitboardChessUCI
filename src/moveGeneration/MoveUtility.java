package moveGeneration;

import board.MoveEncoding;
import testing.testMoveGeneration.Testing;

public class MoveUtility {

    public static final char[] pieceTypeToChar = new char[] {'p', 'n', 'b', 'r', 'q'};


    public static String toLongAlgebraic(int move) {
        int start = MoveEncoding.getStart(move);
        int destination = MoveEncoding.getDestination(move);
        boolean isPromotion = MoveEncoding.getIsPromotion(move);
        int promotionType = MoveEncoding.getPromotionType(move);

        StringBuilder builder = new StringBuilder();
        builder.append(Testing.notation(start));
        builder.append(Testing.notation(destination));
        if (isPromotion) {
            builder.append(pieceTypeToChar[promotionType]);
        }

        return builder.toString();
    }
}
