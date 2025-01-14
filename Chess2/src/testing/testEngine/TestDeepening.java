package testing.testEngine;

import board.Position;
import engine.Search;
import moveGeneration.MoveGenerator;
import zobrist.Hashing;

import static testing.testEngine.TestHelpSearch.repeatedNegaMax;
import static testing.testEngine.TestHelpSearch.repeatedttNegaMax;

public class TestDeepening {
    public static void main(String[] args) throws InterruptedException {
        new MoveGenerator();
        Hashing.initializeRandomNumbers();

        Position position = new Position();
        int depth = 6;

        System.out.println("ttNegaMax repeated");
        long start = System.currentTimeMillis();
        repeatedttNegaMax(position, depth);
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        System.out.println("Searched to depth: " + depth + "\nIn ms: " + elapsed + "\nwith result: " + "\n");


        System.out.println("NegaMax repeated");
        start = System.currentTimeMillis();
        repeatedNegaMax(position, depth);
        end = System.currentTimeMillis();
        elapsed = end - start;
        System.out.println("Searched to depth: " + depth + "\nIn ms: " + elapsed + "\nwith result: " + "\n");

    }
}
