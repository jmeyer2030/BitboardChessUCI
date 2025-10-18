package board;

/**
 * How we encode moves. Contains get methods to get features of moves, and methods to set them.
 *
 * <p>Core:</p>
 * <ul>
 * <li>0-5: start (6 bits 0-63)</li>
 * <li>6-11: destination (6 bits 0-63)</li>
 * <li>12-14: moved piece (3 bits)</li>
 * <li>15-17: captured piece (3 bits)</li>
 * <li>18-19: promotionType (2 bits, knight, bishop, rook, queen)</li>
 * </ul>
 *
 * <p>Flags:</p>
 * <ul>
 * <li>20: isQuiet</li>
 * <li>21: isCapture</li>
 * <li>22: isEP</li>
 * <li>23: isPromotion</li>
 * <li>24: isCastle</li>
 * <li>25: isCheck</li>
 * <li>26: isDoublePush</li>
 * <li>27: isReversible</li>
 * </ul>
 * <p>helper</p>
 * <ul>
 * <li>28: CaptureColor (0 White, 1 Black)</li>
 * <li>29: CastleSide (0 King, 1 Queen)</li>
 * </ul>
 * <p>30-31 unused<p>
 * <p>
 * Previously included that now are stored in a stack during search:
 * <ul>
 * <li>50 move count</li>
 * <li>ep square value</li>
 * <li>castle rights</li>
 * </ul>
 */
public final class MoveEncoding {

    // @formatter:off
    // Core:
    public static final int startMask         = 0b00000000_00000000_00000000_00111111;
    public static final int destinationMask   = 0b00000000_00000000_00001111_11000000;
    public static final int movedPieceMask    = 0b00000000_00000000_01110000_00000000;
    public static final int capturedPieceMask = 0b00000000_00000011_10000000_00000000;
    public static final int promotionTypeMask = 0b00000000_00001100_00000000_00000000;
    // Flags:
    public static final int isQuietMask       = 0b00000000_00010000_00000000_00000000;
    public static final int isCaptureMask     = 0b00000000_00100000_00000000_00000000;
    public static final int isEPMask          = 0b00000000_01000000_00000000_00000000;
    public static final int isPromotionMask   = 0b00000000_10000000_00000000_00000000;
    public static final int isCastleMask      = 0b00000001_00000000_00000000_00000000;
    public static final int isCheckMask       = 0b00000010_00000000_00000000_00000000;
    public static final int isDoublePushMask  = 0b00000100_00000000_00000000_00000000;
    public static final int isReversibleMask  = 0b00001000_00000000_00000000_00000000;
    public static final int wasInCheckMask    = 0b01000000_00000000_00000000_00000000;
    public static final int activePlayerMask  = 0b10000000_00000000_00000000_00000000;
    // Helper
    public static final int captureColorMask  = 0b00010000_00000000_00000000_00000000;
    public static final int castleSideMask    = 0b00100000_00000000_00000000_00000000;
// @formatter:on


    private MoveEncoding() {
    }

    // Start square
    public static int getStart(int move) {
        return (move & startMask);
    }

    public static int setStart(int move, int start) {
        assert start >= 0;
        assert start <= 64;
        move = move & ~startMask;
        return move | start;
    }

    // Destination square
    public static int getDestination(int move) {
        return (move & destinationMask) >> 6;
    }

    public static int setDestination(int move, int destination) {
        assert destination >= 0;
        assert destination <= 64;
        move = move & ~destinationMask;
        return move | (destination << 6);
    }

    // Moved Piece (as ordinal of the enum)
    public static int getMovedPiece(int move) {
        return (move & movedPieceMask) >> 12;
    }

    public static int setMovedPiece(int move, int piece) {
        assert piece >= 0;
        assert piece <= 5;
        move = move & ~movedPieceMask;
        return move | (piece << 12);
    }

    // Captured Piece (as ordinal of the enum)
    public static int getCapturedPiece(int move) {
        return (move & capturedPieceMask) >> 15;
    }

    public static int setCapturedPiece(int move, int piece) {
        assert piece >= 0;
        assert piece <= 5;
        move = move & ~capturedPieceMask;
        return move | (piece << 15);
    }

    // Promotion Type
    public static int getPromotionType(int move) {
        return ((move & promotionTypeMask) >> 18) + 1;
    }

    public static int setPromotionType(int move, int piece) {
        assert piece >= 1;
        assert piece <= 4;
        move = move & ~promotionTypeMask;
        return move | ((piece - 1) << 18);
    }

    // Quiet flag
    public static boolean getIsQuiet(int move) {
        return (move & isQuietMask) != 0;
    }

    public static int setIsQuiet(int move, int isQuiet) {
        assert isQuiet == 0 || isQuiet == 1;
        move = move & ~isQuietMask;
        return move | (isQuiet << 20);
    }

    // Capture flag
    public static boolean getIsCapture(int move) {
        return (move & isCaptureMask) != 0;
    }

    public static int setIsCapture(int move, int isCapture) {
        assert isCapture == 0 || isCapture == 1;
        move = move & ~isCaptureMask;
        return move | (isCapture << 21);
    }

    // Ep Flag
    public static boolean getIsEP(int move) {
        return (move & isEPMask) != 0;
    }

    public static int setIsEP(int move, int isEP) {
        assert isEP == 0 || isEP == 1;
        move = move & ~isEPMask;
        return move | (isEP << 22);
    }

    // Promotion flag
    public static boolean getIsPromotion(int move) {
        return (move & isPromotionMask) != 0;
    }

    public static int setIsPromotion(int move, int isPromotion) {
        assert isPromotion == 0 || isPromotion == 1;
        move = move & ~isPromotionMask;
        return move | (isPromotion << 23);
    }

    // Castle flag
    public static boolean getIsCastle(int move) {
        return (move & isCastleMask) != 0;
    }

    public static int setIsCastle(int move, int isCastle) {
        assert isCastle == 0 || isCastle == 1;
        move = move & ~isCastleMask;
        return move | (isCastle << 24);
    }

    // Check flag
    public static boolean getIsCheck(int move) {
        return (move & isCheckMask) != 0;
    }

    public static int setIsCheck(int move, int isCheck) {
        assert isCheck == 0 || isCheck == 1;
        move = move & ~isCheckMask;
        return move | (isCheck << 25);
    }

    // Double Push flag
    public static boolean getIsDoublePush(int move) {
        return (move & isDoublePushMask) != 0;
    }

    public static int setIsDoublePush(int move, int isDoublePush) {
        assert isDoublePush == 0 || isDoublePush == 1;
        move = move & ~isDoublePushMask;
        return move | (isDoublePush << 26);
    }

    // Reversible flag
    public static boolean getIsReversible(int move) {
        return (move & isReversibleMask) != 0;
    }

    public static int setIsReversible(int move, int isReversible) {
        assert isReversible == 0 || isReversible == 1;
        move = move & ~isReversibleMask;
        return move | (isReversible << 27);
    }

    /*
        Capture Color (0 White, 1 Black)
    */
    public static boolean getCaptureColor(int move) {
        return (move & captureColorMask) != 0;
    }

    public static int setCaptureColor(int move, int captureColor) {
        assert captureColor == 0 || captureColor == 1;
        move = move & ~captureColorMask;
        return move | (captureColor << 28);
    }

    // CastleSide (0 King, 1 Queen)
    public static int getCastleSide(int move) {
        return (move & castleSideMask) >> 29;
    }

    public static int setCastleSide(int move, int castleSide) {
        assert castleSide == 0 || castleSide == 1;
        move = move & ~castleSideMask;
        return move | (castleSide << 29);
    }

    // WasInCheck (0 false 1 true)
    public static boolean getWasInCheck(int move) {
        return (move & wasInCheckMask) != 0;
    }

    public static int setWasInCheck(int move, int wasInCheck) {
        assert wasInCheck == 0 || wasInCheck == 1;
        move = move & ~wasInCheckMask;
        return move | (wasInCheck << 30);
    }

    // ActivePlayer
    public static int getActivePlayer(int move) {
        return (move & activePlayerMask) >> 31 == -1 ? 1 : 0;
    }

    public static int setActivePlayer(int move, int activePlayer) {
        assert activePlayer == 0 || activePlayer == 1;
        move = move & ~wasInCheckMask;
        return move | (activePlayer << 31);
    }

    /**
     * returns a standard notation string for a square in little endian
     *
     * @param square square
     * @return standard notation square
     */
    public static String squareToAlgebraic(int square) {
        String[] files = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
        String[] ranks = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        int rank = square / 8;
        int file = square % 8;
        return files[file] + ranks[rank];
    }

    /**
     * Returns Long Algebraic Notation (LAN) representation of a move
     * Long Algebraic Notation: <Start><Destination><promotionType>
     *
     * @param move move to convert to LAN
     * @return LAN representation of the move
     */
    public static String getLAN(int move) {
        StringBuilder builder = new StringBuilder();

        int start = getStart(move);
        int destination = getDestination(move);

        builder.append(squareToAlgebraic(start));
        builder.append(squareToAlgebraic(destination));

        if (getIsPromotion(move)) {
            String[] pieceTypes = {"p", "n", "b", "r", "q", "k"};
            builder.append(pieceTypes[getPromotionType(move)]);
        }

        return builder.toString();
    }

    /**
     * Prints all details of a move to console
     * Only used for debugging
     *
     * @param move to print the details of
     */
    public static void getDetails(int move) {
        System.out.println("start :" + getStart(move));
        System.out.println("destination :" + getDestination(move));
        System.out.println("movedPiece :" + getMovedPiece(move));
        System.out.println("capturedPiece :" + getCapturedPiece(move));
        System.out.println("promotionType :" + getPromotionType(move));
        System.out.println("isQuiet :" + getIsQuiet(move));
        System.out.println("isCapture :" + getIsCapture(move));
        System.out.println("isEP :" + getIsEP(move));
        System.out.println("isPromotion :" + getIsPromotion(move));
        System.out.println("isCastle :" + getIsCastle(move));
        System.out.println("isCheck :" + getIsCheck(move));
        System.out.println("isDoublePush :" + getIsDoublePush(move));
        System.out.println("isReversible :" + getIsReversible(move));
        System.out.println("wasInCheck :" + getWasInCheck(move));
        System.out.println("captureColor :" + getCaptureColor(move));
        System.out.println("castleSide :" + getCastleSide(move));
        System.out.println("activePlayer: " + getActivePlayer(move));
    }
}
