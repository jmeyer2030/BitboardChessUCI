package com.jmeyer2030.driftwood.testEngine;

import com.jmeyer2030.driftwood.board.FEN;
import com.jmeyer2030.driftwood.board.Position;
import com.jmeyer2030.driftwood.search.Search;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static com.jmeyer2030.driftwood.testMoveGeneration.PerftSuiteTest.getFEN;
import static org.junit.jupiter.api.Assertions.fail;

/**
* Serves as a way to benchmark search speed.
*  - Searches over a suite of positions to depth 9, printing results and time
*
* NOTE: Search depth is not a metric of main.java.engine performance, over aggressive pruning will be quick but have poor results
*  - Use only to verify expectations of search speedup and rough functionality
*/
public class SearchSuiteTest {
    @Test
    void testSearchOnTestPositions() {
        int depth = 13;
        try (Scanner scanner = new Scanner(new File("src/test/resources/perftSuite.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine(); // Reads one line
                String fenString = getFEN(line);
                FEN fen = new FEN(fenString);
                Position position = new Position(fen);
                try {
                    Search.iterativeDeepeningFixedDepth(position, depth);
                } catch (Exception e) {
                    throw e;
                }
            }
        } catch (IOException e) {
            fail();
        }
    }
}