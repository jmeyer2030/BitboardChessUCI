package testing.testMoveGeneration;

import board.FEN;
import board.Position;
import org.junit.jupiter.api.Test;
import testing.Perft;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class testPerftSuite {
    @Test
    void testAll() {
        try (Scanner scanner = new Scanner(new File("src/testing/testMoveGeneration/perftSuite.txt"))) {
            int lineNumber = 0;
            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine(); // Reads one line
                String fen = getFEN(line);
                long[] perftTable = getValues(line);
                System.out.println("TESTING FEN: " + fen + " TEST PROGRESS: " + lineNumber + "/" + 126);
                for (int depth = 0; depth < perftTable.length; depth++) {
                    long result = perftFromFen(fen, depth + 1);
                    assertEquals(perftTable[depth], result);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static long perftFromFen(String fen, int depth) {
        FEN fenP = new FEN(fen);
        Position position = new Position(fenP);

        long start = System.currentTimeMillis();
        long result = Perft.perft(depth, position);
        long end = System.currentTimeMillis();

        System.out.println("Total time: " + (end - start));
        return result;
    }

    /**
     * Returns the FEN portion of the line (everything before the first semicolon).
     *
     * @param line a String containing the FEN and evaluation tokens.
     * @return the FEN string.
     */
    public static String getFEN(String line) {
        int semicolonIndex = line.indexOf(";");
        if (semicolonIndex != -1) {
            return line.substring(0, semicolonIndex).trim();
        }
        return line.trim(); // If no semicolon is found, return the whole line trimmed.
    }

    /**
     * Returns an array of long values corresponding to the evaluation values D1 ... D6.
     * Assumes that each token is in the format "Dx value" and that they appear in order.
     *
     * @param line a String containing the FEN and evaluation tokens.
     * @return a long array with the values (e.g., arr[0] for D1, ..., arr[5] for D6).
     */
    public static long[] getValues(String line) {
        // Split the line by semicolon. The first part is the FEN.
        String[] parts = line.split(";");

        // Create an array for the values (skip the first part, which is the FEN).
        long[] values = new long[parts.length - 1];

        // Loop through each evaluation token.
        for (int i = 1; i < parts.length; i++) {
            // Trim to remove any extra whitespace.
            String token = parts[i].trim(); // e.g., "D1 4"

            // Split the token on whitespace.
            String[] tokens = token.split("\\s+");
            if (tokens.length >= 2) {
                try {
                    // Parse the second token as a long.
                    values[i - 1] = Long.parseLong(tokens[1]);
                } catch (NumberFormatException e) {
                    // Handle the case where the value isn't a valid number.
                    System.err.println("Error parsing number from token: " + token);
                    values[i - 1] = 0;
                }
            }
        }
        return values;
    }

}
