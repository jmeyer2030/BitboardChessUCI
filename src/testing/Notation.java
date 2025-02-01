package testing;

public class Notation {
    /**
     * returns a standard notation string for a square in little endian
     * @param square square
     * @return standard notation square
     */
    public static String squareToChessNotation(int square) {
        String[] files = new String[] {"a", "b", "c", "d", "e", "f", "g", "h"};
        String[] ranks = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
        int rank = square / 8;
        int file = square % 8;
        return files[file] + ranks[rank] ;

    }
}
