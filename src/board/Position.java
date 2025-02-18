package board;

import engine.evaluation.PieceSquareTables;
import moveGeneration.MoveGenerator;
import testing.testMoveGeneration.Testing;
import customExceptions.InvalidPositionException;
import zobrist.Hashing;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/*
 * Represents a position with Bitboards
 */
public class Position {

    public Stack<Integer> hmcStack;
    public Stack<Integer> epStack;
    public Stack<Byte> castleRightsStack;

    public static int[][] castleRookStarts = new int[][]{{7, 0}, {63, 56}}; // [activePlayer][castleSide] [0][0] is white king
    public static int[][] castleRookDestinations = new int[][]{{5, 3}, {61, 59}}; // [activePlayer][castleSide] [0][0] is white king
    public static byte[] castleRightsMask = new byte[]{0b0000_0011, 0b0000_1100}; //[activePlayer] is mask to remove rights of active player

    public long zobristHash;

    //Piece Locations
    public long occupancy;
    public long[] pieceColors;
    public long[] pieces; // This stores all piece BBs at the PieceType.ordinal() position
    public int[] kingLocs;

    //State:
    public int activePlayer; // 0 is white, 1 is black
    public byte castleRights; //Castle Rights: 0b0000(whiteQueen)(whiteKing)(blackQueen)(blackKing)
    public int enPassant; //Same as fen, is the location where the pawn would be if it advanced one square.
    public int halfMoveCount;
    public int fullMoveCount;

    public int[] pinnedPieces;

    public int[][] pieceCounts; // Indexed as: pieceCounts[color][piece], for use in NMP and draw eval

    public long checkers;
    public boolean inCheck;


    // Evaluation
    public int mgScore;
    public int egScore;
    public int gamePhase;


    /*
     * Constructors
     */

    /**
     * Build starting position
     */
    public Position() {
        pinnedPieces = new int[64];
        Arrays.fill(pinnedPieces, -1);

        hmcStack = new Stack<Integer>();
        epStack = new Stack<Integer>();
        castleRightsStack = new Stack<Byte>();


        //Piece Locations:
        occupancy = 0b11111111_11111111_00000000_00000000_00000000_00000000_11111111_11111111L;

        pieceColors = new long[2];
        pieceColors[0] = 0b00000000_00000000_00000000_00000000_00000000_00000000_11111111_11111111L;
        pieceColors[1] = 0b11111111_11111111_00000000_00000000_00000000_00000000_00000000_00000000L;

        pieces = new long[6];
        pieces[0] = 0b00000000_11111111_00000000_00000000_00000000_00000000_11111111_00000000L;
        pieces[1] = 0b01000010_00000000_00000000_00000000_00000000_00000000_00000000_01000010L;
        pieces[2] = 0b00100100_00000000_00000000_00000000_00000000_00000000_00000000_00100100L;
        pieces[3] = 0b10000001_00000000_00000000_00000000_00000000_00000000_00000000_10000001L;
        pieces[4] = 0b00001000_00000000_00000000_00000000_00000000_00000000_00000000_00001000L;
        pieces[5] = 0b00010000_00000000_00000000_00000000_00000000_00000000_00000000_00010000L;

        kingLocs = new int[]{Long.numberOfTrailingZeros(pieces[5] & pieceColors[0]), Long.numberOfTrailingZeros(pieces[5] & pieceColors[1])};


        //State:
        activePlayer = 0;
        castleRights = 0b00001111;
        enPassant = 0;
        halfMoveCount = 0;
        fullMoveCount = 1;
        inCheck = false;

        zobristHash = Hashing.computeZobrist(this);

        mgScore = PieceSquareTables.computeMGScore(this);
        egScore = PieceSquareTables.computeEGScore(this);
        gamePhase = PieceSquareTables.computeGamePhase(this);

        pieceCounts = new int[][] {new int[] {8, 2, 2, 2, 1, 1}, new int[] {8, 2, 2, 2, 1, 1}};
        checkers = 0;
    }

    /**
     * Copy a position
     */
    public Position(Position position) {
        hmcStack = (Stack<Integer>) position.hmcStack.clone();
        epStack = (Stack<Integer>) position.epStack.clone();
        castleRightsStack = (Stack<Byte>) position.castleRightsStack.clone();

        this.occupancy = position.occupancy;
        this.pieceColors = Arrays.copyOf(position.pieceColors, 2);
        this.pieces = Arrays.copyOf(position.pieces, 6);
        this.kingLocs = Arrays.copyOf(position.kingLocs, 2);
        this.activePlayer = position.activePlayer;
        this.castleRights = position.castleRights;
        this.enPassant = position.enPassant;
        this.halfMoveCount = position.halfMoveCount;
        this.fullMoveCount = position.fullMoveCount;
        this.inCheck = position.inCheck;
        this.zobristHash = position.zobristHash;

        this.pinnedPieces = Arrays.copyOf(position.pinnedPieces, 64);

        this.mgScore = position.mgScore;
        this.egScore = position.egScore;
        this.gamePhase = position.gamePhase;

        this.pieceCounts = new int[][] {Arrays.copyOf(position.pieceCounts[0], 6), Arrays.copyOf(position.pieceCounts[1], 6)};
        this.checkers = position.checkers;
    }

    /**
     * Build from FEN
     */
    public Position(FEN fen) {
        pieceCounts = new int[][] {new int[6], new int[6]};

        pinnedPieces = new int[64];
        Arrays.fill(pinnedPieces, -1);

        hmcStack = new Stack<Integer>();
        epStack = new Stack<Integer>();
        castleRightsStack = new Stack<Byte>();

        // Initialize bitboards for occupancy and individual pieces
        long occupancy = 0L;
        long whitePieces = 0L;
        long blackPieces = 0L;

        long pawns = 0L;
        long rooks = 0L;
        long knights = 0L;
        long bishops = 0L;
        long queens = 0L;
        long kings = 0L;

        // Parse piece placement
        String[] ranks = fen.piecePlacement.split("/");
        for (int rank = 7; rank >= 0; rank--) { // Iterate over ranks from 8th to 1st
            String currentRank = ranks[rank];
            int file = 0;

            for (char c : currentRank.toCharArray()) {
                if (Character.isDigit(c)) {
                    // Empty squares
                    file += c - '0';
                } else {
                    // Piece on the square
                    int square = (7 - rank) * 8 + file; // Convert rank and file to square index
                    long squareBit = 1L << square;
                    occupancy |= squareBit;

                    switch (c) {
                        case 'P':
                            pieceCounts[0][0] += 1;
                            pawns |= squareBit;
                            whitePieces |= squareBit;
                            break;
                        case 'p':
                            pieceCounts[1][0] += 1;
                            pawns |= squareBit;
                            blackPieces |= squareBit;
                            break;
                        case 'R':
                            pieceCounts[0][3] += 1;
                            rooks |= squareBit;
                            whitePieces |= squareBit;
                            break;
                        case 'r':
                            pieceCounts[1][3] += 1;
                            rooks |= squareBit;
                            blackPieces |= squareBit;
                            break;
                        case 'N':
                            pieceCounts[0][1] += 1;
                            knights |= squareBit;
                            whitePieces |= squareBit;
                            break;
                        case 'n':
                            pieceCounts[1][1] += 1;
                            knights |= squareBit;
                            blackPieces |= squareBit;
                            break;
                        case 'B':
                            pieceCounts[0][2] += 1;
                            bishops |= squareBit;
                            whitePieces |= squareBit;
                            break;
                        case 'b':
                            pieceCounts[1][2] += 1;
                            bishops |= squareBit;
                            blackPieces |= squareBit;
                            break;
                        case 'Q':
                            pieceCounts[0][4] += 1;
                            queens |= squareBit;
                            whitePieces |= squareBit;
                            break;
                        case 'q':
                            pieceCounts[1][4] += 1;
                            queens |= squareBit;
                            blackPieces |= squareBit;
                            break;
                        case 'K':
                            pieceCounts[0][5] += 1;
                            kings |= squareBit;
                            whitePieces |= squareBit;
                            break;
                        case 'k':
                            pieceCounts[1][5] += 1;
                            kings |= squareBit;
                            blackPieces |= squareBit;
                            break;
                    }
                    file++;
                }
            }

        }

        // Parse active color
        int activePlayer = fen.activeColor == 'w' ? 0 : 1;

        // Parse castling rights
        byte castleRights = 0;
        if (fen.castlingAvailible.contains("K")) castleRights |= 0b0100;
        if (fen.castlingAvailible.contains("Q")) castleRights |= 0b1000;
        if (fen.castlingAvailible.contains("k")) castleRights |= 0b0001;
        if (fen.castlingAvailible.contains("q")) castleRights |= 0b0010;

        // Parse en passant square
        int enPassant = 0;
        if (!fen.enPassant.equals("-")) {
            char fileChar = fen.enPassant.charAt(0);
            char rankChar = fen.enPassant.charAt(1);
            int file = fileChar - 'a';
            int rank = rankChar - '1';
            enPassant = rank * 8 + file;
        }

        //Game Status
        fullMoveCount = fen.fullMoves;

        //Piece Locations:
        this.occupancy = occupancy;
        this.pieceColors = new long[]{whitePieces, blackPieces};
        this.pieces = new long[]{pawns, knights, bishops, rooks, queens, kings};

        //State:
        this.activePlayer = activePlayer;
        this.castleRights = castleRights;
        this.enPassant = enPassant;

        kingLocs = new int[]{Long.numberOfTrailingZeros(pieces[5] & pieceColors[0]), Long.numberOfTrailingZeros(pieces[5] & pieceColors[1])};

        this.inCheck = MoveGenerator.kingAttacked(this, this.activePlayer);

        this.zobristHash = Hashing.computeZobrist(this);


        mgScore = PieceSquareTables.computeMGScore(this);
        egScore = PieceSquareTables.computeEGScore(this);
        gamePhase = PieceSquareTables.computeGamePhase(this);

        checkers = MoveGenerator.computeCheckers(this);
    }

    /*
     * Make and unMake
     */
    private void removePiece(int square, int pieceType, int color) {
        this.zobristHash ^= Hashing.pieceSquare[square][color][pieceType];
        this.occupancy &= ~(1L << square);
        this.pieceColors[color] &= ~(1L << square);
        this.pieces[pieceType] &= ~(1L << square);
        this.pieceCounts[color][pieceType]--;

        if (color == 0) {
            this.mgScore -= PieceSquareTables.pieceTables[0][pieceType][square] + PieceSquareTables.pieceValues[0][pieceType];
            this.egScore -= PieceSquareTables.pieceTables[1][pieceType][square] + PieceSquareTables.pieceValues[1][pieceType];
        } else {
            this.mgScore += PieceSquareTables.pieceTables[0][pieceType][PieceSquareTables.flip[square]] + PieceSquareTables.pieceValues[0][pieceType];
            this.egScore += PieceSquareTables.pieceTables[1][pieceType][PieceSquareTables.flip[square]] + PieceSquareTables.pieceValues[1][pieceType];
        }

        this.gamePhase -= PieceSquareTables.gameStageInc[pieceType];
    }

    private void addPiece(int square, int pieceType, int color) {
        this.zobristHash ^= Hashing.pieceSquare[square][color][pieceType];
        this.occupancy |= (1L << square);
        this.pieceColors[color] |= (1L << square);
        this.pieces[pieceType] |= (1L << square);
        this.pieceCounts[color][pieceType]++;

        if (color == 0) {
            this.mgScore += PieceSquareTables.pieceTables[0][pieceType][square] + PieceSquareTables.pieceValues[0][pieceType];
            this.egScore += PieceSquareTables.pieceTables[1][pieceType][square] + PieceSquareTables.pieceValues[1][pieceType];
        } else {
            this.mgScore -= PieceSquareTables.pieceTables[0][pieceType][PieceSquareTables.flip[square]] + PieceSquareTables.pieceValues[0][pieceType];
            this.egScore -= PieceSquareTables.pieceTables[1][pieceType][PieceSquareTables.flip[square]] + PieceSquareTables.pieceValues[1][pieceType];
        }

        this.gamePhase += PieceSquareTables.gameStageInc[pieceType];
    }


    public void makeNullMove() {
        // Push stored things
        hmcStack.push(this.halfMoveCount);
        epStack.push(this.enPassant);
        castleRightsStack.push(this.castleRights);

        // Undo ep hash
        if (enPassant != 0) {
            zobristHash ^= Hashing.enPassant[enPassant % 8];
        }

        // Increment HMC
        this.halfMoveCount++;

        // Set ep to 0
        this.enPassant = 0;

        // If black moving, increment FMC
        this.fullMoveCount += activePlayer;

        // Switch active player
        this.activePlayer = 1 - activePlayer;

        // Switch active player hash
        this.zobristHash ^= Hashing.sideToMove[0];
        this.zobristHash ^= Hashing.sideToMove[1];
    }


    public void unmakeNullMove() {
        // Switch active player
        this.activePlayer = 1 - activePlayer;

        zobristHash ^= Hashing.castleRights[castleRights];

        // Pop stored things
        halfMoveCount = hmcStack.pop();
        enPassant = epStack.pop();
        castleRights = castleRightsStack.pop();

        // If black moving, increment FMC
        this.fullMoveCount -= activePlayer;

        // Switch active player hash
        this.zobristHash ^= Hashing.sideToMove[0];
        this.zobristHash ^= Hashing.sideToMove[1];
        zobristHash ^= Hashing.castleRights[castleRights];

        if (enPassant != 0) { // Should always be 0
            zobristHash ^= Hashing.enPassant[enPassant % 8];
        }
    }
    /**
     * Applies a move
     */
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

    /**
     * Unmakes a move
     */
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


    /**
     * Returns the PieceType on a square
     *
     * @param square square
     * @return pieceType
     */
    public int getPieceType(int square) {
        long squareMask = (1L << square);
        if ((this.pieces[0] & squareMask) != 0) {
            return 0;
        } else if ((this.pieces[3] & squareMask) != 0) {
            return 3;
        } else if ((this.pieces[1] & squareMask) != 0) {
            return 1;
        } else if ((this.pieces[2] & squareMask) != 0) {
            return 2;
        } else if ((this.pieces[4] & squareMask) != 0) {
            return 4;
        } else if ((this.pieces[5] & squareMask) != 0) {
            return 5;
        }
        return 6;
    }

    /**
     * Returns if this position is equal to another
     *
     * @param position position
     * @return true if they are equal
     */
    public boolean equals(Position position) {
        boolean equal = true;
        equal &= compareValues(this.occupancy, position.occupancy, "Occupancy");
        equal &= compareValues(this.pieces[0], position.pieces[0], "Pawns");
        equal &= compareValues(this.pieces[1], position.pieces[1], "Knight");
        equal &= compareValues(this.pieces[2], position.pieces[2], "Bishop");
        equal &= compareValues(this.pieces[3], position.pieces[3], "Rook");
        equal &= compareValues(this.pieces[4], position.pieces[4], "Queen");
        equal &= compareValues(this.pieces[5], position.pieces[5], "King");
        equal &= compareValues(this.pieceColors[0], position.pieceColors[0], "White Pieces");
        equal &= compareValues(this.pieceColors[1], position.pieceColors[1], "Black Pieces");
        equal &= compareValues(this.castleRights, position.castleRights, "Castle Rights");
        equal &= compareValues(this.halfMoveCount, position.halfMoveCount, "Rule 50");
        equal &= compareValues(this.enPassant, position.enPassant, "En Passant");
        equal &= compareValues(this.activePlayer, position.activePlayer, "Active Player");
        equal &= compareValues(this.fullMoveCount, position.fullMoveCount, "Full Move Count");
        return equal;
    }

    /**
     * Returns true if values are equal, else false
     *
     * @param value1    first value
     * @param value2    value to compare to
     * @param fieldName field name of these values
     * @return if they are equal
     */
    private boolean compareValues(Object value1, Object value2, String fieldName) {
        if (!value1.equals(value2)) {
            System.out.println(fieldName + " different");
            return false;
        }
        return true;
    }

    /*
     * Visualization Method:
     */

    /**
     * Prints all bitboards
     */
    public void printBoard() {
        System.out.println("Occupancy: ");
        Testing.printBoard(occupancy);

        System.out.println("White Pieces: ");
        Testing.printBoard(pieceColors[0]);

        System.out.println("Black Pieces: ");
        Testing.printBoard(pieceColors[1]);

        System.out.println("Pawns: ");
        Testing.printBoard(pieces[0]);

        System.out.println("Rooks: ");
        Testing.printBoard(pieces[3]);

        System.out.println("Bishops: ");
        Testing.printBoard(pieces[2]);

        System.out.println("Queens: ");
        Testing.printBoard(pieces[4]);

        System.out.println("Kings: ");
        Testing.printBoard(pieces[5]);

        System.out.println("Knights: ");
        Testing.printBoard(pieces[1]);

        System.out.println("Castle Rights" + Integer.toBinaryString((castleRights + 256) % 256));
        System.out.println("En Passant: " + enPassant);
        System.out.println("HalfMoveCount: " + halfMoveCount);
        System.out.println("FullMoveCount: " + fullMoveCount);
        System.out.println("ActivePlayer: " + activePlayer);
    }

    /**
     * Prints a board in a human-readable format
     */
/*
    +---+---+---+---+---+---+---+---+
  8 | r | n | b | q | k | b | n | r |
    +---+---+---+---+---+---+---+---+
  7 | p | p | p | p | p | p | p | p |
    +---+---+---+---+---+---+---+---+
  6 |   |   |   |   |   |   |   |   |
    +---+---+---+---+---+---+---+---+
  5 |   |   |   |   |   |   |   |   |
    +---+---+---+---+---+---+---+---+
  4 |   |   |   |   |   |   |   |   |
    +---+---+---+---+---+---+---+---+
  3 |   |   |   |   |   |   |   |   |
    +---+---+---+---+---+---+---+---+
  2 | P | P | P | P | P | P | P | P |
    +---+---+---+---+---+---+---+---+
  1 | R | N | B | Q | K | B | N | R |
    +---+---+---+---+---+---+---+---+
      A   B   C   D   E   F   G   H
*/
    public String getDisplayBoard() {
        try {
            validPosition();
        } catch (InvalidPositionException ipe) {
            return "Position isn't valid!";
        }

        char[] board = new char[64];
        char[] pieceSymbols = {'p', 'n', 'b', 'r', 'q', 'k'};


        // fill board with correct piece symbol
        Arrays.fill(board, ' ');
        for (int i = 0; i < 6; i++) {
            long currentPieceType = pieces[i];
            while (currentPieceType != 0L) {
                int loc = Long.numberOfTrailingZeros(currentPieceType);
                currentPieceType &= (currentPieceType - 1);
                board[loc] = pieceSymbols[i];
            }
        }

        // Shift case of the board array
        long shiftCase = pieceColors[0];
        while (shiftCase != 0L) {
            int loc = Long.numberOfTrailingZeros(shiftCase);
            shiftCase &= (shiftCase - 1);
            board[loc] = Character.toUpperCase(board[loc]);
        }
        char[][] boardTemplate = new char[][]{
                "    +---+---+---+---+---+---+---+---+\n".toCharArray(),
                "  8 |   |   |   |   |   |   |   |   |\n".toCharArray(),
                "    +---+---+---+---+---+---+---+---+\n".toCharArray(),
                "  7 |   |   |   |   |   |   |   |   |\n".toCharArray(),
                "    +---+---+---+---+---+---+---+---+\n".toCharArray(),
                "  6 |   |   |   |   |   |   |   |   |\n".toCharArray(),
                "    +---+---+---+---+---+---+---+---+\n".toCharArray(),
                "  5 |   |   |   |   |   |   |   |   |\n".toCharArray(),
                "    +---+---+---+---+---+---+---+---+\n".toCharArray(),
                "  4 |   |   |   |   |   |   |   |   |\n".toCharArray(),
                "    +---+---+---+---+---+---+---+---+\n".toCharArray(),
                "  3 |   |   |   |   |   |   |   |   |\n".toCharArray(),
                "    +---+---+---+---+---+---+---+---+\n".toCharArray(),
                "  2 |   |   |   |   |   |   |   |   |\n".toCharArray(),
                "    +---+---+---+---+---+---+---+---+\n".toCharArray(),
                "  1 |   |   |   |   |   |   |   |   |\n".toCharArray(),
                "    +---+---+---+---+---+---+---+---+\n".toCharArray(),
                "      A   B   C   D   E   F   G   H  \n".toCharArray()};

        int firstSquare = 6;
        int squareIncrement = 4;

        // Replace characters in template with their piece
        for (int i = 0; i < 64; i++) {
            int boardRow = 1 + (i / 8) * 2; // boardRow of boardTemplate
            int boardCol = firstSquare + ((i % 8) * squareIncrement);

            int row = 7 - i / 8;
            int col = i % 8;
            int index = row * 8 + col;

            boardTemplate[boardRow][boardCol] = board[index];
        }

        // Convert char[][] to a list
        List<Character> result = new LinkedList<>();
        for (char[] arr : boardTemplate) {
            for (char c : arr) {
                result.add(c);
            }
        }

        StringBuilder bldr = new StringBuilder();

        for (Character c : result) {
            bldr.append(c);
        }

        return bldr.toString();
    }

    /**
     * A position is "valid" iff:
     * - pieceColors[0-1] have no overlap AND
     * - pieces[0-5] have no overlap AND
     * - pieceColors[0-1] (with OR operator) is equivalent to occupancy
     * - pieces[0-5] (with OR operator) is equivalent to occupancy
     */
    public void validPosition() throws InvalidPositionException {
        // Test pieceColors no overlap
        long pieceColorsAND = pieceColors[0] & pieceColors[1];
        if (pieceColorsAND != 0)
            throw new InvalidPositionException("Piece colors overlap");

        // Test pieces no overlap
        long piecesAND = pieces[0] & pieces[1] & pieces[2] & pieces[3] & pieces[4] & pieces[5];
        if (piecesAND != 0)
            throw new InvalidPositionException("Piece types overlap");

        // Test pieceColors OR == occupancy
        long pieceColorsOR = pieceColors[0] | pieceColors[1];
        if (occupancy != pieceColorsOR)
            throw new InvalidPositionException("Piece colors aren't consistent with occupancy");

        // Test pieces OR == occupancy
        long piecesOR = pieces[0] | pieces[1] | pieces[2] | pieces[3] | pieces[4] | pieces[5];
        if (occupancy != piecesOR)
            throw new InvalidPositionException("Piece types aren't consistent with occupancy");
    }

}
