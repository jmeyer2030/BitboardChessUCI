package testing.testMoveGeneration;

public class DebugPerft {
    public static void main(String[] args) {
        //MoveGenerator.initializeAll();
        String stockFishPerft = "a2a3: 34\n" +
                "a2a4: 34\n" +
                "b2b3: 34\n" +
                "b2b4: 33\n" +
                "c2c3: 34\n" +
                "c2c4: 34\n" +
                "g2g3: 34\n" +
                "g2g4: 34\n" +
                "h2h3: 34\n" +
                "h2h4: 34\n" +
                "h1f1: 33\n" +
                "h1g1: 33\n" +
                "d3f1: 34\n" +
                "d3e2: 34\n" +
                "d3c4: 34\n" +
                "d3b5: 31\n" +
                "d3a6: 33\n" +
                "b1a3: 34\n" +
                "b1c3: 34\n" +
                "f3e5: 34\n" +
                "f3g1: 34\n" +
                "f3d4: 33\n" +
                "f3h4: 34\n" +
                "f3g5: 32\n" +
                "d1e2: 34\n" +
                "e1f1: 33\n" +
                "e1e2: 34\n" +
                "e1g1: 33";
        String myPerft = "a2a3: 34\n" +
                "b2b3: 34\n" +
                "c2c3: 34\n" +
                "g2g3: 34\n" +
                "h2h3: 34\n" +
                "a2a4: 34\n" +
                "b2b4: 33\n" +
                "c2c4: 34\n" +
                "g2g4: 34\n" +
                "h2h4: 34\n" +
                "b1a3: 34\n" +
                "b1c3: 34\n" +
                "f3g1: 34\n" +
                "f3d4: 33\n" +
                "f3h4: 34\n" +
                "f3e5: 34\n" +
                "f3g5: 32\n" +
                "d3f1: 34\n" +
                "d3e2: 34\n" +
                "d3c4: 34\n" +
                "d3b5: 31\n" +
                "d3a6: 33\n" +
                "h1f1: 34\n" +
                "h1g1: 34\n" +
                "d1e2: 34\n" +
                "e1f1: 34\n" +
                "e1e2: 34\n" +
                "e1g1: 34";
        Testing.perftDiff(stockFishPerft, myPerft);
    }
}
