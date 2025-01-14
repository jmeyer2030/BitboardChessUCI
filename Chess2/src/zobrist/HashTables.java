package zobrist;

import board.Move;
import engine.NodeType;

public class HashTables {

/*
 * Set Size
 */
    // Transposition Table
    public static int transpositionBitSize = 28;
    public static long transpositionIndexMask = (1L << transpositionBitSize) - 1;
    // Perft Table
    public static int perftIndexBitSize = 28;
    public static long perftIndexMask = (1L << perftIndexBitSize) - 1;
    // Three-fold table
    public static int threeFoldBitSize = 16;
    public static long threeFoldIndexMask = (1L << threeFoldBitSize) - 1;


/*
 * Initialize
 */
    // Transposition Table
    public static TTElement[] transpositionTable = new TTElement[(int) Math.pow(2, transpositionBitSize)];
    // Perft Table
    public static PerftElement[] perftTable = new PerftElement[(int) Math.pow(2, perftIndexBitSize)];
    // Three-Fold Table
    public static ThreeFoldElement[] threeFoldTable = new ThreeFoldElement[(int) Math.pow(2, threeFoldBitSize)];

/*
 * Get Index
 */
    public static int getTranspositionIndex(long zobristHash) {
        return (int) (transpositionIndexMask & zobristHash);
    }

    public static int getPerftIndex(long zobristHash) {
        return (int) (perftIndexMask & zobristHash);
    }

    public static int getThreeFoldIndex(long zobristHash) {
        return (int) (threeFoldIndexMask & zobristHash);
    }

/*
 * Get Element
 */
    public static TTElement getTranspositionElement(long zobristHash) {
        return transpositionTable[getTranspositionIndex(zobristHash)];
    }

    public static PerftElement getPerftElement(long zobristHash) {
        return perftTable[getPerftIndex(zobristHash)];
    }

    public static ThreeFoldElement getThreeFoldElement(long zobristHash) {
        return threeFoldTable[getThreeFoldIndex(zobristHash)];
    }

/*
 * Add Element
 */
    public static void addPerftElement(long zobristHash, int depth, long perftResult) {
        perftTable[getTranspositionIndex(zobristHash)] = new PerftElement(zobristHash, depth, perftResult);
    }

    public static void addTranspositionElement(long zobristHash, Move bestMove, int depth, int score, NodeType nodeType, int age) {
        transpositionTable[getTranspositionIndex(zobristHash)] = new TTElement(zobristHash, bestMove, depth, score, nodeType, age);
    }

    public static void addThreeFoldElement(long zobristHash, int numRepetitions) {
        threeFoldTable[getThreeFoldIndex(zobristHash)] = new ThreeFoldElement(zobristHash, numRepetitions);
    }

/*
 * Helper Functions
 */


    public static void incrementThreeFold(long zobristHash) {
        ThreeFoldElement element = getThreeFoldElement(zobristHash);
        if (element == null || element.zobristHash != zobristHash) {
            addThreeFoldElement(zobristHash, 1);
        } else {
            threeFoldTable[getThreeFoldIndex(zobristHash)].numRepetitions++;
        }
    }

    public static void decrementThreeFold(long zobristHash) {
        ThreeFoldElement element = getThreeFoldElement(zobristHash);
        if (element == null) {
            throw new IllegalStateException("If we are decrementing then the element should be defined!");
        } else if (element.numRepetitions >= 1) {
            element.numRepetitions--;
        }
    }

    public static boolean repetitionsExceeded(long zobristHash) {
        ThreeFoldElement element = getThreeFoldElement(zobristHash);
        if (element != null && element.numRepetitions >= 3) {
            return true;
        }
        return false;
    }


/*
 * Define Element Classes
 */
    /**
    * A class for use in the Transposition Table data structure
    */
    public static class TTElement {
        public long zobristHash;
        public Move bestMove;
        public int depth; // Ply depth
        public int score;
        public NodeType nodeType;
        public int age; // Half move count

        public TTElement() {
        }

        public TTElement(long zobristHash, Move bestMove, int depth, int score,
            NodeType nodeType, int age) {

            this.zobristHash = zobristHash;
            this.bestMove = bestMove;
            this.score = score;
            this.depth = depth;
            this.nodeType = nodeType;
            this.age = age;
        }
    }

    /**
    * A class for use in the Perft Table data structure
    */
    public static class PerftElement {
        public long zobristHash;
        public int depth;
        public long perftResult;

        public PerftElement(long zobristHash, int depth, long perftResult) {
            this.zobristHash = zobristHash;
            this.depth = depth;
            this.perftResult = perftResult;
        }
    }

    /**
     * A class for use in the Three-Fold Table data structure
     */
    public static class ThreeFoldElement {
        public long zobristHash;
        public int numRepetitions;

        public ThreeFoldElement(long zobristHash, int numRepetitions) {
            this.zobristHash = zobristHash;
            this.numRepetitions = numRepetitions;
        }
    }
}
