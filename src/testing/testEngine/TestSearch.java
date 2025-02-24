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
        /*
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
        */
        //FEN fen = new FEN("r6r/2pbkppp/p7/2n5/8/8/PP1N1PPP/2R1KB1R b K - 1 19");
        FEN fen = new FEN("3r1nk1/1pq1bppp/2p1p3/p3P2P/2P5/PP4P1/1BB1QP2/R5K1 b - - 2 23");
        Position position = new Position(fen);

        position = new Position();
        //System.out.println("Position: \n" + position.getDisplayBoard());
        int depth = 6;
        long start = System.currentTimeMillis();
        Search.iterativeDeepening(position, 10_000, new SearchState(18));
        //Search.iterativeDeepeningFixedDepth(position, 5);
        long end = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (end - start));
        System.out.println(Search.nodes);
        //negaMaxTimeTest(position, depth);
    }

    // 8, 19376, 20
    // 7, 65124, -100

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
            Search.MoveValue result = Search.negamax(Search.NEG_INFINITY, Search.POS_INFINITY, depth, position, searchState, true);
            long end = System.currentTimeMillis();
            long elapsed = end - start;

            System.out.println("Searched to depth: " + depth + "\nIn ms: " + elapsed + "\nwith result: " + result.value + "\nand move: " + MoveEncoding.getLAN(result.bestMove));
        } catch (InvalidPositionException ipe) {
            //searchState.searchMonitor.logSearchStack();
        }

    }

}
