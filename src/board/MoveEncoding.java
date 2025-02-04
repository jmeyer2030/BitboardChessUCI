package board;

public final class MoveEncoding {
/*
 * Necessary:
 * 0-5: start (6 bits 0-63)
 * 6-11: destination (6 bits 0-63)
 * 12-14: moved piece (3 bits)
 * 15-17: captured piece (3 bits)
 * 18-19: promotionType (2 bits, knight, bishop, rook, queen)
 *
 * Flags:
 * 20: isQuiet
 * 21: isCapture
 * 22: isEP
 * 23: isPromotion
 * 24: isCastle
 * 25: isCheck
 * 26: isDoublePush
 * 27: isReversible
 *
 * 28: CaptureColor (0 White, 1 Black)
 * 29: CastleSide (0 King, 1 Queen)
 *
 * 30-31 unused
 *
 * Positions Stack:
 * 50 move count
 * ep square value
 * castle rights
 *
 */
    // Core:
    public static final int startMask =         0b00000000_00000000_00000000_00111111;
    public static final int destinationMask =   0b00000000_00000000_00001111_11000000;
    public static final int movedPieceMask =    0b00000000_00000000_01110000_00000000;
    public static final int capturedPieceMask = 0b00000000_00000011_10000000_00000000;
    public static final int promotionTypeMask = 0b00000000_00001100_00000000_00000000;

    // Flags:
    public static final int isQuietMask =       0b00000000_00010000_00000000_00000000;
    public static final int isCaptureMask =     0b00000000_00100000_00000000_00000000;
    public static final int isEPMask =          0b00000000_01000000_00000000_00000000;
    public static final int isPromotionMask =   0b00000000_10000000_00000000_00000000;
    public static final int isCastleMask =      0b00000001_00000000_00000000_00000000;
    public static final int isCheckMask =       0b00000010_00000000_00000000_00000000;
    public static final int isDoublePushMask =  0b00000100_00000000_00000000_00000000;
    public static final int isReversibleMask =  0b00001000_00000000_00000000_00000000;

    // Helper
    public static final int captureColorMask =  0b00010000_00000000_00000000_00000000;
    public static final int castleSideMask =    0b00100000_00000000_00000000_00000000;
/*
    Start
*/
    public static int getStart(int move) {
        return (move & startMask);
    }

    public static int setStart(int move, int start) {
        if (start < 0 || start > 63) {
            throw new IllegalArgumentException();
        }
        move = move & ~startMask; // Clear start bits
        return move | start;
    }
/*
    Destination
*/
    public static int getDestination(int move) {
        return (move & destinationMask) >> 6;
    }

    public static int setDestination(int move, int destination) {
        if (destination < 0 || destination > 63) {
            throw new IllegalArgumentException();
        }
        move = move & ~destinationMask; // Clear destination bits
        return move | (destination << 6);
    }
/*
    Moved Piece (as ordinal of the enum)
*/
    public static int getMovedPiece(int move) {
        return (move & movedPieceMask) >> 12;
    }

    public static int setMovedPiece(int move, int piece) {
        if (piece < 0 || piece > 5) {
            throw new IllegalArgumentException();
        }

        move = move & ~movedPieceMask; // Clear moved piece
        return move | piece << 12;
    }
/*
    Captured Piece (as ordinal of the enum)
*/

    public static int getCapturedPiece(int move) {
        return (move & capturedPieceMask) >> 15;
    }

    public static int setCapturedPiece(int move, int piece) {
        if (piece < 0 || piece > 4) {
            throw new IllegalArgumentException();
        }

        move = move & ~capturedPieceMask;
        return move | piece << 15;
    }

/*
    Promotion Type
*/

    public static int getPromotionType(int move) {
        return (move & promotionTypeMask) >> 18;
    }

    public static int setPromotionType(int move, int piece) {
        if (piece < 1 || piece > 4) {
            throw new IllegalArgumentException();
        }

        move = move & ~promotionTypeMask;
        return move | piece << 18;
    }
/*
    Quiet flag
*/
    public static boolean getIsQuiet(int move) {
        return (move & isQuietMask) != 0;
    }

    public static int setIsQuiet(int move, int isQuiet) {
        if (isQuiet != 0 || isQuiet != 1) {
            throw new IllegalArgumentException();
        }

        move = move & ~isQuietMask;
        return move | isQuiet << 20;
    }
/*
    Capture flag
*/
    public static boolean getIsCapture(int move) {
        return (move & isCaptureMask) != 0;
    }

    public static int setIsCapture(int move, int isCapture) {
        if (isCapture != 0 || isCapture != 1) {
            throw new IllegalArgumentException();
        }

        move = move & ~isCaptureMask;
        return move | isCapture << 21;
    }
/*
    EP flag
*/
    public static boolean getIsEP(int move) {
        return (move & isEPMask) != 0;
    }

    public static int setIsEP(int move, int isEP) {
        if (isEP != 0 || isEP != 1) {
            throw new IllegalArgumentException();
        }

        move = move & ~isEPMask;
        return move | isEP << 22;
    }

/*
    Promotion flag
*/
    public static boolean getIsPromotion(int move) {
        return (move & isPromotionMask) != 0;
    }

    public static int setIsPromotion(int move, int isPromotion) {
        if (isPromotion != 0 || isPromotion != 1) {
            throw new IllegalArgumentException();
        }

        move = move & ~isPromotionMask;
        return move | isPromotion << 23;
    }

/*
    Castle flag
*/
    public static boolean getIsCastle(int move) {
        return (move & isCastleMask) != 0;
    }

    public static int setIsCastle(int move, int isCastle) {
        if (isCastle != 0 || isCastle != 1) {
            throw new IllegalArgumentException();
        }

        move = move & ~isCastleMask;
        return move | isCastle << 24;
    }

/*
    Check flag
*/
    public static boolean getIsCheck(int move) {
        return (move & isCheckMask) != 0;
    }

    public static int setIsCheck(int move, int isCheck) {
        if (isCheck != 0 || isCheck != 1) {
            throw new IllegalArgumentException();
        }

        move = move & ~isCheckMask;
        return move | isCheck << 25;
    }
/*
    Double Push flag
*/
    public static boolean getIsDoublePush(int move) {
        return (move & isDoublePushMask) != 0;
    }

    public static int setIsDoublePush(int move, int isDoublePush) {
        if (isDoublePush != 0 || isDoublePush != 1) {
            throw new IllegalArgumentException();
        }

        move = move & ~isDoublePushMask;
        return move | isDoublePush << 26;
    }
/*
    Reversible flag
*/
    public static boolean getIsReversible(int move) {
        return (move & isReversibleMask) != 0;
    }

    public static int setIsReversible(int move, int isReversible) {
        if (isReversible != 0 || isReversible != 1) {
            throw new IllegalArgumentException();
        }

        move = move & ~isReversibleMask;
        return move | isReversible << 27;
    }
/*
    Capture Color (0 White, 1 Black)
*/
    public static boolean getCaptureColor(int move) {
        return (move & captureColorMask) != 0;
    }

    public static int setCaptureColor(int move, int captureColor) {
        if (captureColor != 0 || captureColor != 1) {
            throw new IllegalArgumentException();
        }

        move = move & ~captureColorMask;
        return move | captureColor << 28;
    }

/*
    CastleSide (0 King, 1 Queen)
*/
    public static boolean getCastleSide(int move) {
        return (move & castleSideMask) != 0;
    }

    public static int setCastleSide(int move, int castleSide) {
        if (castleSide != 0 || castleSide != 1) {
            throw new IllegalArgumentException();
        }

        move = move & ~castleSideMask;
        return move | castleSide << 29;
    }

    public static void main(String[] args) {
        int move = 0;
        System.out.println(Integer.toBinaryString(move));
        move = setDestination(move, 24);
        System.out.println(getDestination(move));
    }
}
