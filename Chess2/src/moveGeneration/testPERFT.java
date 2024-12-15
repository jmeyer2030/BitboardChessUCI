package moveGeneration;

import board.Position;

public class testPERFT {
    public static void main(String[] args) {
        new MoveGenerator();

        long start = System.currentTimeMillis();
        Position position = new Position();
        int depth = 6;
        Testing.perft(depth, position);
        long end = System.currentTimeMillis();

        System.out.println("Total time: " + (end - start));
    }
}
