package zobrist;

/*
* Transposition tables seek to store information gained about positions during search
* so that repeated calculation waste is minimized.
*
* The first n bits of a Zobrist hash can be used for the index of a position in a TT
*   27 seems to work for others
*
* Stores:
* - Zobrist hash
* - Best move
* - Depth
* - Score
* - Node type
*   - exact (a < s < b)
*   - lower bound (s >= b)
*   - upper bound (s <= a)
* - Age (for overwriting old nodes, often half move count)
*/

import board.Move;
import engine.NodeType;

public class TranspositionTable {

    public static int indexBitSize = 28;

    public static long indexMask = (1L << indexBitSize) - 1;
    public static TTElement[] tt = new TTElement[(int) Math.pow(2,indexBitSize)];
    public static perftTTElement[] perftTT = new perftTTElement[(int) Math.pow(2, indexBitSize)];

    public static TTElement getElement(long zobristHash) {
        return tt[getTTIndex(zobristHash)];
    }

    public static perftTTElement getPerftElement(long zobristHash) {
        return perftTT[getTTIndex(zobristHash)];
    }

    public static void addPerftElement(long zobristHash, int depth, long perftResult) {
        perftTT[getTTIndex(zobristHash)] = new perftTTElement(zobristHash, depth, perftResult);
        //System.out.println(getPerftElement(zobristHash).depth);
    }

    public static void addTTElement(long zobristHash, Move bestMove, int depth, int score, NodeType nodeType, int age) {
        tt[getTTIndex(zobristHash)] = new TTElement(zobristHash, bestMove, depth, score, nodeType, age);
    }

    public static int getTTIndex(long zobristHash) {
        return (int) (indexMask & zobristHash);
    }

    public static class TTElement {
        public long zobristHash;
        public Move bestMove;
        public int depth; // Ply depth
        public int score;
        public NodeType nodeType;
        public int age; // Half move count

        public TTElement() {

        }

        public TTElement(long zobristHash, Move bestMove, int depth, int score,
            NodeType nodeType, int age) {

            this.zobristHash = zobristHash;
            this.bestMove = bestMove;
            this.score = score;
            this.depth = depth;
            this.nodeType = nodeType;
            this.age = age;
        }
    }

    public static class perftTTElement {
        public long zobristHash;
        public int depth;
        public long perftResult;

        public perftTTElement(long zobristHash, int depth, long perftResult) {
            this.zobristHash = zobristHash;
            this.depth = depth;
            this.perftResult = perftResult;
        }
    }
}
