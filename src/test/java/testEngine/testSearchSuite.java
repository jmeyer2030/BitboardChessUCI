package testEngine;

import board.FEN;
import board.Position;
import engine.search.Search;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static testMoveGeneration.testPerftSuite.getFEN;
/**
* Serves as a way to benchmark search speed.
*  - Searches over a suite of positions to depth 9, printing results and time
*
* NOTE: Search depth is not a metric of main.java.engine performance, over aggressive pruning will be quick but have poor results
*  - Use only to verify expectations of search speedup and rough functionality
*/
public class testSearchSuite {
    @Test
    void testSearchOnTestPositions() {
        int depth = 13;
        long startTime = System.currentTimeMillis();
        try (Scanner scanner = new Scanner(new File("src/test/resources/perftSuite.txt"))) {
            int lineNumber = 0;
            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine(); // Reads one line
                String fenString = getFEN(line);

                FEN fen = new FEN(fenString);

                Position position = new Position(fen);

                System.out.println("TESTING FEN: " + fenString + " TEST PROGRESS: " + lineNumber + "/" + 126);
                try {
                    Search.iterativeDeepeningFixedDepth(position, depth);
                } catch (Exception e) {
                    throw e;
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