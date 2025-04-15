package main.java.zobrist.transposition;

import main.java.engine.search.NodeType;

public record TTElement(long zobristHash, int bestMove, int depth, int score, NodeType nodeType) {
}