package testing.testEngine;

import board.FEN;
import board.MoveEncoding;
import board.Position;
import customExceptions.InvalidPositionException;
import engine.search.Search;
import engine.search.SearchState;
import moveGeneration.MoveGenerator;

public class TestSearch {

    public static void main(String[] args) throws InterruptedException, InvalidPositionException {
        FEN fen = new FEN("r6r/p1pkBQ2/6p1/8/4n3/8/PbP1KP1P/R7 b - - 0 1");
        Position position = new Position(fen);
        int[] moveBuffer = new int[256];

        int move = MoveGenerator.getMoveFromLAN("e4f2", position, moveBuffer);
        System.out.println(position.getDisplayBoard());
        MoveEncoding.getDetails(move);
        position.makeMove(move);
        System.out.println(position.getDisplayBoard());
        position.unMakeMove(move);
        System.out.println(position.getDisplayBoard());
        //FEN fen = new FEN("r6r/2pbkppp/p7/2n5/8/8/PP1N1PPP/2R1KB1R b K - 1 19");
        //FEN fen = new FEN("7k/5Q2/qq4pp/qq4q1/q5q1/8/8/7K w - - 0 1");
        //Position position = new Position(fen);
        //Position position = new Position();

        //position = new Position();
        //System.out.println("Position: \n" + position.getDisplayBoard());
        //int depth = 6;

        //Search.iterativeDeepening(position, 3000, new SearchState(18));
        //negaMaxTimeTest(position, depth);


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
        SearchState searchState = new SearchState(18);
        try {
            Search.MoveValue result = Search.negamax(Search.NEG_INFINITY, Search.POS_INFINITY, depth, position, searchState);
            long end = System.currentTimeMillis();
            long elapsed = end - start;

            System.out.println("Searched to depth: " + depth + "\nIn ms: " + elapsed + "\nwith result: " + result.value + "\nand move: " + MoveEncoding.getLAN(result.bestMove));
        } catch (InvalidPositionException ipe) {
            //searchState.searchMonitor.logSearchStack();
        }

    }

}
