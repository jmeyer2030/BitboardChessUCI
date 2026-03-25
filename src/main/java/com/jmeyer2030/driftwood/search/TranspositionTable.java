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
 *   0–31  bestMove   (32 bits, full move encoding)
 *  32–39  depth      ( 8 bits, 0-255)
 *  40–41  nodeType   ( 2 bits, 0=EXACT, 1=LOWER, 2=UPPER)
 *  42–63  score      (22 bits, signed via arithmetic shift)
 * </pre>
 *
 * <p>Total: 16 bytes per entry (down from 24), 2 array accesses per probe
 * (down from up to 5).</p>
 */
public class TranspositionTable {
    private final long indexMask;

    private final long[] keys;
    private final long[] data;

    // ── Bit layout constants ──
    private static final long BEST_MOVE_MASK = 0xFFFF_FFFFL;
    private static final int  DEPTH_SHIFT     = 32;
    private static final long DEPTH_MASK      = 0xFFL << DEPTH_SHIFT;
    private static final int  NODE_TYPE_SHIFT = 40;
    private static final long NODE_TYPE_MASK  = 0x3L  << NODE_TYPE_SHIFT;
    private static final int  SCORE_SHIFT     = 42;
    // Upper 22 bits — no explicit mask needed, arithmetic right-shift handles sign.

    /**
     * Initialises a new transposition table.
     *
     * @param numBits number of index bits. Table size = 2^numBits entries × 16 bytes.
     */
    public TranspositionTable(int numBits) {
        if (numBits < 1 || numBits > 30) {
            throw new IllegalArgumentException("Transposition table size must be greater than 0 and less than 30");
        }

        this.indexMask = (1L << numBits) - 1;
        int size = 1 << numBits;
        this.keys = new long[size];
        this.data = new long[size];
    }

    // ── Index ──

    public int getIndex(long zobristHash) {
        return (int) (indexMask & zobristHash);
    }

    // ── Pack / unpack helpers ──

    private static long packData(int bestMove, int depth, int nodeType, int score) {
        return (bestMove & BEST_MOVE_MASK)
             | ((long)(depth    & 0xFF) << DEPTH_SHIFT)
             | ((long)(nodeType & 0x3 ) << NODE_TYPE_SHIFT)
             | ((long) score            << SCORE_SHIFT);
    }

    /** Extracts bestMove from packed data. */
    public static int unpackBestMove(long packed) {
        return (int)(packed & BEST_MOVE_MASK);
    }

    /** Extracts depth from packed data. */
    public static int unpackDepth(long packed) {
        return (int)((packed >>> DEPTH_SHIFT) & 0xFF);
    }

    /** Extracts nodeType from packed data. */
    public static int unpackNodeType(long packed) {
        return (int)((packed >>> NODE_TYPE_SHIFT) & 0x3);
    }

    /** Extracts score from packed data (arithmetic shift preserves sign). */
    public static int unpackScore(long packed) {
        return (int)(packed >> SCORE_SHIFT);
    }

    // ── Public read API ──

    /**
     * Probes the table for a matching entry at sufficient depth.
     *
     * @param zobristHash  hash of the position to look up
     * @param requiredDepth  minimum depth for the entry to be useful
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

    // ── Write API ──

    /**
     * Stores an entry, replacing whatever was in the slot.
     */
    public void addElement(long zobristHash, int bestMove, int depth, int score, int nodeType) {
        int index = getIndex(zobristHash);
        long packed = packData(bestMove, depth, nodeType, score);
        data[index]  = packed;
        keys[index] = zobristHash ^ packed;
    }
}
