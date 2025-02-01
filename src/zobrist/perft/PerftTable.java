package zobrist.perft;

public class PerftTable {
    private final int numBits;
    private final long indexMask;
    private final PerftElement[] table;

    public PerftTable(int numBits) {
        if (numBits < 1 || numBits > 30) {
            throw new IllegalArgumentException();
        }

        this.numBits = numBits;
        this.indexMask = (1L << numBits) - 1;
        this.table = new PerftElement[1 << numBits];
    }

    public int getIndex(long zobristHash) {
        return (int) (indexMask & zobristHash);
    }

    public PerftElement getElement(long zobristHash, int depth) {
        PerftElement element = table[getIndex(zobristHash)];

        if ((element == null) || (element.zobristHash() != zobristHash) || (element.depth() != depth)) {
            return null;
        } else {
            return element;
        }
    }

    public void addElement(long zobristHash, int depth, long perftResult) {
        table[getIndex(zobristHash)] = new PerftElement(zobristHash, depth, perftResult);
    }
}
