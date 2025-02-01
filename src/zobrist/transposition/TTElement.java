package zobrist.transposition;

import board.Move;
import engine.search.NodeType;

public record TTElement(long zobristHash, Move bestMove, int depth, int score, NodeType nodeType) {
}