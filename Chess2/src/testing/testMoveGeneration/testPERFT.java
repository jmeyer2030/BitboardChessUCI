package testing.testMoveGeneration;

import engine.StaticEvaluation;
import board.*;
import moveGeneration.MoveGenerator;
import zobrist.TranspositionTable;
import zobrist.ZobristHashing;

public class testPERFT {
    public static void main(String[] args) {
        // Initialize requisite data
        new MoveGenerator();
        ZobristHashing.initializeRandomNumbers();

        // Change this variable
        int depth = 7;
        boolean useTTs = true;

        perftStartingPosition(depth, useTTs);
        //perftFromFen("", depth, useTTs);
    }

    public static void perftStartingPosition(int depth, boolean useTTs) {
        Position position = new Position();
        long start = System.currentTimeMillis();

        if (useTTs) {
            Testing.ttPerft(depth, position);
        } else {
            Testing.perft(depth, position);
        }

        long end = System.currentTimeMillis();

        System.out.println("Total time: " + (end - start));
    }

    public static void perftFromFen(String fen, int depth, boolean useTTs) {
        FEN fenP = new FEN(fen);
        Position position = new Position(fenP);
        long start = System.currentTimeMillis();

         if (useTTs) {
             Testing.ttPerft(depth, position);
         } else {
             Testing.perft(depth, position);
         }

        long end = System.currentTimeMillis();

        System.out.println("Total time: " + (end - start));
    }
}
