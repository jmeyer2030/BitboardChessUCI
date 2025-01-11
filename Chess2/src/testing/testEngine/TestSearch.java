package testing.testEngine;

import board.FEN;
import board.Position;
import engine.minimax;
import moveGeneration.MoveGenerator;
import zobrist.ZobristHashing;

public class TestSearch {

    public static void main(String[] args) {
        new MoveGenerator();
        ZobristHashing.initializeRandomNumbers();
        FEN fen = new FEN("8/6k1/K7/8/8/8/2r5/1r6 b - - 0 1");
        Position position = new Position(fen);
        System.out.println(MoveGenerator.generateStrictlyLegal(position).size());
        //Position position = new Position();
        int depth = 1;

        negaMax(position, depth);
        ttNegaMax(position, depth);

    }

    public static void negaMax(Position position, int depth) {
        System.out.println("Standard Negamax: ");
        long start = System.currentTimeMillis();
        minimax.MoveValue result = minimax.negaMax(minimax.NEGINFINITY, minimax.POSINFINITY, depth, position);
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        System.out.println("Searched to depth: " + depth + "\nIn ms: " + elapsed + "\nwith result: " + result.value + "\n");
    }

    public static void ttNegaMax(Position position, int depth) {
        System.out.println("Transposition Table Negamax: ");
        long start = System.currentTimeMillis();
        minimax.MoveValue result = minimax.ttNegaMax(minimax.NEGINFINITY, minimax.POSINFINITY, depth, position);
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        System.out.println("Searched to depth: " + depth + "\nIn ms: " + elapsed + "\nwith result: " + result.value + "\n");
    }

}
