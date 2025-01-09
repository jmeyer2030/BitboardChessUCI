package testing.testEngine;

import board.FEN;
import board.Position;
import engine.minimax;
import moveGeneration.MoveGenerator;

public class TestSearch {

    public static void main(String[] args) {
        new MoveGenerator();
        FEN fen = new FEN("r1bqkb1r/3pnpp1/p3p2p/4B3/3P4/2N1P3/PP3PPP/R2QKB1R w KQkq - 1 11");
        Position position = new Position(fen);
        //Position position = new Position();
        int depth = 4;

        position.printDisplayBoard();

        System.out.println("Negamax: ");
        long start = System.currentTimeMillis();
        minimax.MoveValue result = minimax.negaMax(minimax.NEGINFINITY, minimax.POSINFINITY, depth, position);
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        System.out.println("Searched to depth: " + depth + "\nIn ms: " + elapsed + "\nwith result: " + result.value + "\n");

    }
}
