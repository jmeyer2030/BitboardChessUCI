package zobrist.transposition;

import engine.search.NodeType;

public record TTElement(long zobristHash, int bestMove, int depth, int score, NodeType nodeType) {
}