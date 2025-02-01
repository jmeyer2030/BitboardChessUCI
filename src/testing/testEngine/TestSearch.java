package testing.testEngine;

import board.FEN;
import board.Move;
import board.Position;
import customExceptions.InvalidPositionException;
import engine.search.Search;
import engine.search.SearchState;
import moveGeneration.MoveGenerator;
import zobrist.Hashing;

import java.util.List;

public class TestSearch {

    public static void main(String[] args) throws InterruptedException, InvalidPositionException {
        MoveGenerator.initializeAll();
        Hashing.initializeRandomNumbers();

        FEN fen = new FEN("r6r/2pbkppp/p7/2n5/8/8/PP1N1PPP/2R1KB1R b K - 1 19");
        //FEN fen = new FEN("7k/5Q2/qq4pp/qq4q1/q5q1/8/8/7K w - - 0 1");
        Position position = new Position(fen);
        //position = new Position();
        //List<Move> moveList = MoveGenerator.generateStrictlyLegal(position);
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
        SearchState searchState = new SearchState(18, position);
        try {
            Search.MoveValue result = Search.negamax(Search.NEG_INFINITY, Search.POS_INFINITY, depth, position, searchState);
            long end = System.currentTimeMillis();
            long elapsed = end - start;
            System.out.println("Searched to depth: " + depth + "\nIn ms: " + elapsed + "\nwith result: " + result.value + "\nand move: " + result.bestMove.toLongAlgebraic());
        } catch (InvalidPositionException ipe) {
            searchState.searchMonitor.logSearchStack();
        }

    }

}
