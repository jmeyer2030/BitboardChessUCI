package testing.testMoveGeneration;

public class DebugPerft {
    public static void main(String[] args) {
        //MoveGenerator.initializeAll();
        String stockFishPerft = "a2a3: 1\n" +
                "a2a4: 1\n" +
                "b2b3: 1\n" +
                "b2b4: 1\n" +
                "c2c3: 1\n" +
                "c2c4: 1\n" +
                "d2d3: 1\n" +
                "d2d4: 1\n" +
                "g2g3: 1\n" +
                "g2g4: 1\n" +
                "e4e5: 1\n" +
                "b1a3: 1\n" +
                "b1c3: 1\n" +
                "f3h2: 1\n" +
                "f3g1: 1\n" +
                "f3d4: 1\n" +
                "f3h4: 1\n" +
                "f3e5: 1\n" +
                "f3g5: 1\n" +
                "e2f1: 1\n" +
                "e2d3: 1\n" +
                "e2c4: 1\n" +
                "e2b5: 1\n" +
                "e2a6: 1\n" +
                "h1h2: 1\n" +
                "h1f1: 1\n" +
                "h1g1: 1\n" +
                "e1f1: 1\n" +
                "e1g1: 1";
        String myPerft = "a2a3: 1\n" +
                "b2b3: 1\n" +
                "c2c3: 1\n" +
                "d2d3: 1\n" +
                "g2g3: 1\n" +
                "e4e5: 1\n" +
                "a2a4: 1\n" +
                "b2b4: 1\n" +
                "c2c4: 1\n" +
                "d2d4: 1\n" +
                "g2g4: 1\n" +
                "b1a3: 1\n" +
                "b1c3: 1\n" +
                "f3g1: 1\n" +
                "f3h2: 1\n" +
                "f3d4: 1\n" +
                "f3h4: 1\n" +
                "f3e5: 1\n" +
                "f3g5: 1\n" +
                "e2f1: 1\n" +
                "e2d3: 1\n" +
                "e2c4: 1\n" +
                "e2b5: 1\n" +
                "e2a6: 1\n" +
                "h1f1: 1\n" +
                "h1g1: 1\n" +
                "h1h2: 1\n" +
                "e1f1: 1";
        Testing.perftDiff(stockFishPerft, myPerft);
    }
}
