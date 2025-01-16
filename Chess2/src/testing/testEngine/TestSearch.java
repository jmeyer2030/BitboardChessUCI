package testing.testEngine;

import board.FEN;
import board.Position;
import engine.Search;
import moveGeneration.MoveGenerator;
import zobrist.Hashing;

public class TestSearch {

    public static void main(String[] args) throws InterruptedException {
        new MoveGenerator();
        Hashing.initializeRandomNumbers();
        FEN fen = new FEN("8/6k1/K7/8/8/8/2r5/1r6 b - - 0 1");
        Position position = new Position(fen);
        System.out.println(MoveGenerator.generateStrictlyLegal(position).size());
        //Position position = new Position();
        int depth = 1;

        negaMaxTimeTest(position, depth);
        noTTNegaMaxTimeTest(position, depth);

    }

    public static void noTTNegaMaxTimeTest(Position position, int depth) {
        System.out.println("Standard Negamax: ");
        long start = System.currentTimeMillis();
        Search.MoveValue result = LegacySearch.negaMax(Search.NEG_INFINITY, Search.POS_INFINITY, depth, position);
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        System.out.println("Searched to depth: " + depth + "\nIn ms: " + elapsed + "\nwith result: " + result.value + "\n");
    }

    public static void negaMaxTimeTest(Position position, int depth) throws InterruptedException {
        System.out.println("Transposition Table Negamax: ");
        long start = System.currentTimeMillis();
        Search.MoveValue result = Search.negamax(Search.NEG_INFINITY, Search.POS_INFINITY, depth, position);
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        System.out.println("Searched to depth: " + depth + "\nIn ms: " + elapsed + "\nwith result: " + result.value + "\n");
    }

}
