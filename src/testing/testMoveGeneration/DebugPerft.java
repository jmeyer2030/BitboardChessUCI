package testing.testMoveGeneration;

public class DebugPerft {
    public static void main(String[] args) {
        //MoveGenerator.initializeAll();
        String stockFishPerft = "a2a3: 1\n" +
                "b2b3: 1\n" +
                "c2c3: 1\n" +
                "d2d3: 1\n" +
                "e2e3: 1\n" +
                "g2g3: 1\n" +
                "h2h3: 1\n" +
                "a2a4: 1\n" +
                "b2b4: 1\n" +
                "c2c4: 1\n" +
                "d2d4: 1\n" +
                "e2e4: 1\n" +
                "g2g4: 1\n" +
                "h2h4: 1\n" +
                "b1a3: 1\n" +
                "b1c3: 1\n" +
                "g1f3: 1\n" +
                "g1h3: 1\n" +
                "d1e1: 1\n" +
                "f2e1: 1\n" +
                "f2f3: 1";
        String myPerft = "a2a3: 1\n" +
                "a2a4: 1\n" +
                "b2b3: 1\n" +
                "b2b4: 1\n" +
                "c2c3: 1\n" +
                "c2c4: 1\n" +
                "d2d3: 1\n" +
                "d2d4: 1\n" +
                "e2e3: 1\n" +
                "e2e4: 1\n" +
                "g2g3: 1\n" +
                "g2g4: 1\n" +
                "h2h3: 1\n" +
                "h2h4: 1\n" +
                "b1a3: 1\n" +
                "b1c3: 1\n" +
                "g1f3: 1\n" +
                "g1h3: 1\n" +
                "d1e1: 1\n" +
                "f2e1: 1\n" +
                "f2e3: 1\n" +
                "f2f3: 1\n" +
                "f2g3: 1";
        Testing.perftDiff(stockFishPerft, myPerft);
    }
}
