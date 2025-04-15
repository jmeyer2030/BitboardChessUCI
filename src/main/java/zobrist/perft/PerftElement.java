package main.java.zobrist.perft;

public record PerftElement(long zobristHash, int depth, long perftResult) {
}
