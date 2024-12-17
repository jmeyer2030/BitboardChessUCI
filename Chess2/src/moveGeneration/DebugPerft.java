package moveGeneration;

import board.Position;
import moveGeneration.MoveGenerator;
import moveGeneration.Testing;

public class DebugPerft {
    public static void main(String[] args) {
        //new MoveGenerator();
        String stockFishPerft = "a2a3: 8457\n" +
                "b2b3: 9345\n" +
                "c2c3: 9272\n" +
                "d2d3: 11959\n" +
                "e2e3: 13134\n" +
                "f2f3: 8457\n" +
                "g2g3: 9345\n" +
                "h2h3: 8457\n" +
                "a2a4: 9329\n" +
                "b2b4: 9332\n" +
                "c2c4: 9744\n" +
                "d2d4: 12435\n" +
                "e2e4: 13160\n" +
                "f2f4: 8929\n" +
                "g2g4: 9328\n" +
                "h2h4: 9329\n" +
                "b1a3: 8885\n" +
                "b1c3: 9755\n" +
                "g1f3: 9748\n" +
                "g1h3: 8881";
        String myPerft = "a2a3: 8457\n" +
             "a2a4: 9330\n" +
             "b2b3: 9347\n" +
             "b2b4: 9333\n" +
             "c2c3: 9347\n" +
             "c2c4: 9819\n" +
             "d2d3: 11977\n" +
             "d2d4: 12455\n" +
             "e2e3: 13312\n" +
             "e2e4: 13341\n" +
             "f2f3: 8457\n" +
             "f2f4: 8931\n" +
             "g2g3: 9347\n" +
             "g2g4: 9329\n" +
             "h2h3: 8457\n" +
             "h2h4: 9330\n" +
             "b1a3: 8889\n" +
             "b1c3: 9766\n" +
             "g1f3: 9764\n" +
             "g1h3: 8887";
        Testing.perftDiff(stockFishPerft, myPerft);

    }
}
