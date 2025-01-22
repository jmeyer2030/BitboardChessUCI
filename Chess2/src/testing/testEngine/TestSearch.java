package testing.testEngine;

import board.FEN;
import board.Move;
import board.Position;
import customExceptions.InvalidPositionException;
import engine.Search;
import moveGeneration.MoveGenerator;
import system.SearchMonitor;
import zobrist.Hashing;

import java.util.List;

public class TestSearch {

    public static void main(String[] args) throws InterruptedException, InvalidPositionException {
        new MoveGenerator();
        Hashing.initializeRandomNumbers();


        FEN fen = new FEN("7k/5Q2/qq4pp/qq4q1/q5q1/8/8/7K w - - 0 1");
        Position position = new Position(fen);
        List<Move> moveList = MoveGenerator.generateStrictlyLegal(position);
        //System.out.println(moveList.size());
        //moveList.stream().forEach(move -> System.out.println(move));

        //position = new Position();
        System.out.println("Position: \n" + position.getDisplayBoard());
        int depth = 6;

        negaMaxTimeTest(position, depth);
        //noTTNegaMaxTimeTest(position, depth);


    }
    /*
    public static void noTTNegaMaxTimeTest(Position position, int depth) {
        System.out.println("Standard Negamax: ");
        long start = System.currentTimeMillis();
        Search.MoveValue result = LegacySearch.negaMax(Search.NEG_INFINITY, Search.POS_INFINITY, depth, position);
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        System.out.println("Searched to depth: " + depth + "\nIn ms: " + elapsed + "\nwith result: " + result.value + "\n");
    }
    */

    public static void negaMaxTimeTest(Position position, int depth) throws InterruptedException, InvalidPositionException {
        System.out.println("Transposition Table Negamax: ");
        long start = System.currentTimeMillis();
        SearchMonitor searchMonitor = new SearchMonitor(position);
        try {
            Search.MoveValue result = Search.negamax(Search.NEG_INFINITY, Search.POS_INFINITY, depth, position, searchMonitor);
            long end = System.currentTimeMillis();
            long elapsed = end - start;
            System.out.println("Searched to depth: " + depth + "\nIn ms: " + elapsed + "\nwith result: " + result.value + "\n");
        } catch (InvalidPositionException ipe) {
            searchMonitor.logSearchStack();
        }

    }

}
