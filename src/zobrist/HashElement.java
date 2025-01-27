package zobrist;

public abstract class HashElement {
    long zobristHash;

    public boolean hashMatch(long zobristHash) {
        return this.zobristHash == zobristHash;
    }
}
