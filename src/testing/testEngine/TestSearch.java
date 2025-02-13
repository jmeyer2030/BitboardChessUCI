package testing.testEngine;

import board.MoveEncoding;
import board.Position;
import customExceptions.InvalidPositionException;
import engine.search.Search;
import engine.search.SearchState;

public class TestSearch {

    public static void main(String[] args) throws InterruptedException, InvalidPositionException {

        //FEN fen = new FEN("r6r/2pbkppp/p7/2n5/8/8/PP1N1PPP/2R1KB1R b K - 1 19");
        //FEN fen = new FEN("7k/5Q2/qq4pp/qq4q1/q5q1/8/8/7K w - - 0 1");
        //Position position = new Position(fen);
        Position position = new Position();

        //position = new Position();
        System.out.println("Position: \n" + position.getDisplayBoard());
        int depth = 7;

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

            System.out.println("Searched to depth: " + depth + "\nIn ms: " + elapsed + "\nwith result: " + result.value + "\nand move: " + MoveEncoding.getLAN(result.bestMove));
        } catch (InvalidPositionException ipe) {
            searchState.searchMonitor.logSearchStack();
        }

    }

}
