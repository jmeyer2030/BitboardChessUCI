package zobrist;

public class PerftTable {
    private final int numBits;
    private final long indexMask;
    private final PerftElement[] table;

    public PerftTable(int numBits) {
        this.numBits = numBits;
        this.indexMask = (1L << numBits) - 1;
        this.table = new PerftElement[(int) Math.pow(2, numBits)];
    }

    public int getIndex(long zobristHash) {
        return (int) (indexMask & zobristHash);
    }


    public PerftElement getElement(long zobristHash, int depth) {
        PerftElement element = table[getIndex(zobristHash)];

        if (element == null || element.zobristHash() != zobristHash || depth != depth)
            return null;
        return element;
    }

    public void addElement(long zobristHash, int depth, long perftResult) {
        table[getIndex(zobristHash)] = new PerftElement(zobristHash, depth, perftResult);
    }
}
