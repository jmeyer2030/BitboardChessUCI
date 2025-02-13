package testing.testMoveGeneration;

import board.FEN;
import board.Position;
import moveGeneration.MoveGenerator;

public class testComputePins {
    public static void main(String[] args) {

        FEN fen = new FEN("8/5k2/8/3P4/2B5/5K2/8/8 w - - 0 1");
        Position position = new Position(fen);

        MoveGenerator.computePins(position);

        for (int i = 0; i < 64; i++) {
            System.out.println(i + " " + position.pinnedPieces[i]);
        }


        //MoveGenerator2.computePotentialDiscoveries(position);

        //for (int i = 0; i < 64; i++) {
            //System.out.println(i + " " + position.potentialDiscoverers[i]);
        //}

        //System.out.println(position.getDisplayBoard());

    }
}
