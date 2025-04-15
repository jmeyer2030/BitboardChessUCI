package test.java.testMoveGeneration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Testing {

    /**
     * Prints the differences between perft results
     * @Param stockfish perft result
     * @Param chess2 perft result
     */
    public static void perftDiff(String stockFish, String generated) {
        Set<String> stockFishParse = new HashSet<String> (Arrays.asList(stockFish.split("\\n")));
        Set<String> generatedParse = new HashSet<String> (Arrays.asList(generated.split("\\n")));

        Set<String> diffs = new HashSet<String>();

        diffs.addAll(stockFishParse);
        diffs.addAll(generatedParse);
        stockFishParse.retainAll(generatedParse);//stockfish becomes AND
        diffs.removeAll(stockFishParse);
        System.out.println("Differences: ");
        diffs.stream().forEach(diff -> System.out.println(diff));
    }

    /**
     * prints the main.java.board in Little-endian rank-file representation
     * @Param bitboard
     */
    public static void printBoard(long bitBoard) {
        for (int rank = 7; rank >= 0; rank--) {
            for (int file = 0; file < 8; file++) {
                System.out.print(((bitBoard & (1L << (rank * 8 + file))) != 0) ? "1 " : "0 ");
            }
            System.out.println();
        }
    }

    /**
     * returns a standard notation string for a square in little endian
     * @param square square
     * @return standard notation square
     */
    public static String notation(int square) {
        String[] files = new String[] {"a", "b", "c", "d", "e", "f", "g", "h"};
        String[] ranks = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
        int rank = square / 8;
        int file = square % 8;
        return files[file] + ranks[rank] ;

    }
}
