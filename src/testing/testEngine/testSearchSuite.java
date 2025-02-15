package testing.testEngine;

import board.FEN;
import board.Position;
import engine.search.Search;
import engine.search.SearchState;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static testing.testEngine.TestSearch.negaMaxTimeTest;
import static testing.testMoveGeneration.testPerftSuite.getFEN;

public class testSearchSuite {

    @Test
    void testSearchOnTestPositions() {
        int depth = 7;
        long startTime = System.currentTimeMillis();
        try (Scanner scanner = new Scanner(new File("src/testing/testMoveGeneration/perftSuite.txt"))) {
            int lineNumber = 0;
            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine(); // Reads one line
                String fenString = getFEN(line);

                FEN fen = new FEN(fenString);

                Position position = new Position(fen);

                System.out.println("TESTING FEN: " + fenString + " TEST PROGRESS: " + lineNumber + "/" + 126);
                try {
                    //Search.iterativeDeepening(position, 1000, new SearchState(18));
                    negaMaxTimeTest(position, depth);
                    //Search.iterativeDeepeningFixedDepth(position, depth);
                } catch (Exception e) {
                    throw new RuntimeException();
                    //throw e;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Elapsed time: " + (endTime - startTime));
        //System.out.println("Total searched depth: " + Search.searchedDepth);
    }
}

/*
depth 7 negamax: 36349
fixed depth 7 iterativeDeepening: 35380
fixed depth 7 iterativeDeepening: 36597
fixed depth 7 iterativeDeepening: 34861
fixed depth 7 iterativeDeepening: 35735 (tt in front of terminal)
fixed depth 7 iterativeDeepening: 28995 (tt in front of terminal AND quiescence before mate)
fixed depth 7 iterativeDeepening: 29246 (tt in front of terminal AND quiescence before mate)
depth 7 negamax: 30039 (tt in front of terminal AND quiescence before mate)
fixed depth 7 iterativeDeepening: 38713 (no mobility)
fixed depth 7 iterativeDeepening: 40459 (no mobility)


RESULTS:
fixed depth 7 iterativeDeepening: 29246 (tt in front of terminal AND quiescence before mate)
depth 7 negamax: 30039 (tt in front of terminal AND quiescence before mate)

Deepening is faster than negamax
SUGGESTS that selection move ordering benefit is SIGNIFICANT



*/