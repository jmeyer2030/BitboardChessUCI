package zobrist.transposition;

import engine.search.NodeType;
/*
Overwrites are relatively common. This is not an issue with the tt or hashing, just a reality of how hashing works.

Storing and handling Mates:
 - When a mate is found and stored in the tt, the mate should be relative to the position that we are in

Suppose search depth 5, finds mate, stored in tt

if we see it in a later search, even if it is 20 plys away,

ISSUE IS STORING AND RETRIEVING. IF WE RETRIEVE SUPPOSED M5, IT IS INCREMENTED
 - Yes, but correctly since it is a m5 from that position, but maybe 8 from current position


# M5


*/
public class TranspositionTable {
    private final long indexMask;

    private final long[] zobristHash;
    private final int[] bestMove;
    private final int[] depth;
    private final int[] score;
    private final int[] nodeType;


    /**
    * Initializes a new Transposition table
    * @param numBits number of bits used to index positions. Size is 2 ^ numBits * 24 Bytes
    *
    */
    public TranspositionTable(int numBits) {
        if (numBits < 1 || numBits > 30) {
            throw new IllegalArgumentException("Transposition table size must be greater than 0 and less than 30");
        }

        this.indexMask = (1L << numBits) - 1;

        this.zobristHash = new long[1 << numBits];
        this.bestMove = new int[1 << numBits];
        this.depth = new int[1 << numBits];
        this.score = new int[1 << numBits];
        this.nodeType = new int[1 << numBits];
    }

    public int getIndex(long zobristHash) {
        return (int) (indexMask & zobristHash);
    }

    /*
    public TTElement getElement(long zobristHash, int depth) {
        TTElement element = table[getIndex(zobristHash)];

        if ((element == null) || (element.zobristHash() != zobristHash) || (element.depth() < depth))
            return null;
        return element;
    }*/


    public int getNodeType(long zobristHash) {
        return this.nodeType[getIndex(zobristHash)];
    }


    public int getScore(long zobristHash) {
        return this.score[getIndex(zobristHash)];
    }

    public int checkedGetScore(long zobristHash) {
        int index = getIndex(zobristHash);

        // Return if the hash isn't the one stored
        if (this.zobristHash[index] != zobristHash) {
            return Integer.MAX_VALUE;
        }

        return score[index];
    }

    public int getBestMove(long zobristHash) {
        return this.bestMove[getIndex(zobristHash)];
    }

    /**
    * Returns the best move associated with the zobrist hash AND verifies that hashes match.
    *
    * @param zobristHash zobristHash for the position we want the best move for
    * @return 0 if the hashes don't match or the position isn't in the table, otherwise the best move
    */
    public int checkedGetBestMove(long zobristHash) {
        int index = getIndex(zobristHash);

        // Return if the hash isn't the one stored
        if (this.zobristHash[index] != zobristHash) {
            return 0;
        }

        return bestMove[index];
    }

    /**
     * An element is "useful" IFF the hashes are the same, and this is as deep or deeper
     *
     * @param zobristHash hash of the position we are looking for
     * @param depth       depth of the search
     * @return if the tt has a useful element stored
     */
    public boolean elementIsUseful(long zobristHash, int depth) {
        int index = getIndex(zobristHash);

        return (this.zobristHash[index] == zobristHash) && (this.depth[index] >= depth) && (this.bestMove[index] != 0);
    }

    public boolean elementExists(long zobristHash) {
        int index = getIndex(zobristHash);
        return this.zobristHash[index] == zobristHash;
    }

    public void addElement(long zobristHash, int bestMove, int depth, int score, NodeType nodeType) {
        int index = getIndex(zobristHash);

        this.zobristHash[index] = zobristHash;
        this.bestMove[index] = bestMove;
        this.depth[index] = depth;
        this.score[index] = score;
        this.nodeType[index] = nodeType.ordinal();
    }
}
