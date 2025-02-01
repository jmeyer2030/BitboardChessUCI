package zobrist.transposition;

import board.Move;
import engine.search.NodeType;

public class TranspositionTable {
    private final int numBits;
    private final long indexMask;
    private final TTElement[] table;

    public TranspositionTable(int numBits) {
        if (numBits < 1 || numBits > 30) {
            throw new IllegalArgumentException();
        }
        this.numBits = numBits;
        this.indexMask = (1L << numBits) - 1;
        this.table = new TTElement[1 << numBits];
    }

    public int getIndex(long zobristHash) {
        return (int) (indexMask & zobristHash);
    }


    public TTElement getElement(long zobristHash, int depth) {
        TTElement element = table[getIndex(zobristHash)];

        if ((element == null) || (element.zobristHash() != zobristHash) || (element.depth() < depth))
            return null;
        return element;
    }

    public void addElement(long zobristHash, Move bestMove, int depth, int score, NodeType nodeType) {
        table[getIndex(zobristHash)] = new TTElement(zobristHash, bestMove, depth, score, nodeType);
    }
}
