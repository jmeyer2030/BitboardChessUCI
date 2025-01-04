package engine;

import board.FEN;
import board.Position;
import moveGeneration.MoveGenerator;

public class TestSearch {

    public static void main(String[] args) {


        new MoveGenerator();
        FEN fen = new FEN("r1bqkb1r/3pnpp1/p3p2p/4B3/3P4/2N1P3/PP3PPP/R2QKB1R w KQkq - 1 11");
        Position position = new Position(fen);

        int depth = 6;

        long start = System.currentTimeMillis();
        minimax.MoveValue result = minimax.minimax(position, true, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        System.out.println("Searched to depth: " + depth + "\nIn ms: " + elapsed + "\nwith result: " + result.value);
    }
}
