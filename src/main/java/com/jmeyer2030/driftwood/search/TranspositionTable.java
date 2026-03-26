package com.jmeyer2030.driftwood.search;

/**
 * Transposition table using two {@code long[]} arrays for cache-friendly,
 * lockless-safe storage.
 *
 * <h3>Encoding</h3>
 * <pre>
 * keys[i] = zobristHash ^ data[i]          (XOR verification trick)
 *
 * data[i] bit layout (64 bits):
 *   0-31  bestMove   (32 bits, full move encoding)
 *  32-39  depth      ( 8 bits, 0-255)
 *  40-41  nodeType   ( 2 bits, 0=EXACT, 1=LOWER, 2=UPPER)
 *  42-63  score      (22 bits, signed via arithmetic shift)
 * </pre>
 *
 * <p>Total: 16 bytes per entry, 2 array accesses per probe.</p>
 */
public class TranspositionTable {
    // -- Bit layout constants --
    private static final long BEST_MOVE_MASK       = 0xFFFF_FFFFL;
    private static final int  DEPTH_SHIFT          = 32;
    private static final int  DEPTH_FIELD_MASK     = 0xFF;   // 8 bits (0-255)
    private static final int  NODE_TYPE_SHIFT      = 40;
    private static final int  NODE_TYPE_FIELD_MASK = 0x3;    // 2 bits (0-3)
    private static final int  SCORE_SHIFT          = 42;
    // Upper 22 bits - no explicit mask needed, arithmetic right-shift handles sign.

    // -- Constructor bounds --
    private static final int MIN_INDEX_BITS = 1;
    private static final int MAX_INDEX_BITS = 30;

    private final long indexMask;

    private final long[] keys;
    private final long[] data;

    /**
     * Initializes a new transposition table.
     *
     * @param numBits number of index bits. Table size = 2^numBits entries x 16 bytes.
     */
    public TranspositionTable(int numBits) {
        if (numBits < MIN_INDEX_BITS || numBits > MAX_INDEX_BITS) {
            throw new IllegalArgumentException("numBits must be between " + MIN_INDEX_BITS + " and " + MAX_INDEX_BITS);
        }

        this.indexMask = (1L << numBits) - 1;
        int size = 1 << numBits;
        this.keys = new long[size];
        this.data = new long[size];
    }

    /** Gets the table index from a zobristHash */
    public int getIndex(long zobristHash) {
        return (int) (indexMask & zobristHash);
    }

    /** Packs bestMove, depth, nodeType, and score into a long */
    private static long packData(int bestMove, int depth, int nodeType, int score) {
        return (bestMove & BEST_MOVE_MASK)
             | ((long)(depth    & DEPTH_FIELD_MASK)     << DEPTH_SHIFT)
             | ((long)(nodeType & NODE_TYPE_FIELD_MASK) << NODE_TYPE_SHIFT)
             | ((long) score                            << SCORE_SHIFT);
    }

    /** Extracts bestMove from packed data. */
    public static int unpackBestMove(long packed) {
        return (int)(packed & BEST_MOVE_MASK);
    }

    /** Extracts depth from packed data. */
    public static int unpackDepth(long packed) {
        return (int)((packed >>> DEPTH_SHIFT) & DEPTH_FIELD_MASK);
    }

    /** Extracts nodeType from packed data. */
    public static int unpackNodeType(long packed) {
        return (int)((packed >>> NODE_TYPE_SHIFT) & NODE_TYPE_FIELD_MASK);
    }

    /** Extracts score from packed data (arithmetic shift preserves sign). */
    public static int unpackScore(long packed) {
        return (int)(packed >> SCORE_SHIFT);
    }

    // -- Public read API --

    /**
     * Probes the table for a matching entry at sufficient depth.
     *
     * @param zobristHash   hash of the position to look up
     * @param requiredDepth minimum depth for the entry to be useful
     * @return packed data word, or {@code 0} on miss
     */
    public long probe(long zobristHash, int requiredDepth) {
        int index = getIndex(zobristHash);
        long packed = data[index];
        if ((keys[index] ^ packed) != zobristHash) return 0;
        if (unpackDepth(packed) < requiredDepth) return 0;
        if ((packed & BEST_MOVE_MASK) == 0) return 0;
        return packed;
    }

    /**
     * Returns the best move for the given position, or 0 on miss.
     * Does NOT require a depth check — useful for move ordering.
     */
    public int checkedGetBestMove(long zobristHash) {
        int index = getIndex(zobristHash);
        long packed = data[index];
        if ((keys[index] ^ packed) != zobristHash) return 0;
        return unpackBestMove(packed);
    }

    // -- Write API --

    /**
     * Stores an entry, replacing whatever was in the slot.
     */
    public void addElement(long zobristHash, int bestMove, int depth, int score, int nodeType) {
        int index = getIndex(zobristHash);
        long packed = packData(bestMove, depth, nodeType, score);
        data[index] = packed;
        keys[index] = zobristHash ^ packed;
    }
}
