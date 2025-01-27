package zobrist;

import board.Move;
import engine.NodeType;

public record TTElement(long zobristHash, Move bestMove, int depth, int score, NodeType nodeType) {
}