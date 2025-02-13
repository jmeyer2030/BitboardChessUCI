package testing.testMoveGeneration;

public class DebugPerft {
    public static void main(String[] args) {
        //MoveGenerator.initializeAll();
        String stockFishPerft = "a7a6: 1\n" +
                "c7c6: 1\n" +
                "d7d6: 1\n" +
                "g7g6: 1\n" +
                "h7h6: 1\n" +
                "a7a5: 1\n" +
                "c7c5: 1\n" +
                "d7d5: 1\n" +
                "g7g5: 1\n" +
                "h7h5: 1\n" +
                "f6e4: 1\n" +
                "f6g4: 1\n" +
                "f6d5: 1\n" +
                "f6h5: 1\n" +
                "f6g8: 1\n" +
                "b8a6: 1\n" +
                "b8c6: 1\n" +
                "a3b2: 1\n" +
                "a3b4: 1\n" +
                "a3c5: 1\n" +
                "a3d6: 1\n" +
                "a3e7: 1\n" +
                "a3f8: 1\n" +
                "c8b7: 1\n" +
                "h8f8: 1\n" +
                "h8g8: 1\n" +
                "d8e7: 1\n" +
                "e8e7: 1\n" +
                "e8f8: 1\n" +
                "e8g8: 1";
        String myPerft = "a7a5: 1\n" +
                "a7a6: 1\n" +
                "c7c5: 1\n" +
                "c7c6: 1\n" +
                "d7d5: 1\n" +
                "d7d6: 1\n" +
                "g7g5: 1\n" +
                "g7g6: 1\n" +
                "h7h5: 1\n" +
                "h7h6: 1\n" +
                "f6e4: 1\n" +
                "f6g4: 1\n" +
                "f6d5: 1\n" +
                "f6h5: 1\n" +
                "f6g8: 1\n" +
                "b8a6: 1\n" +
                "b8c6: 1\n" +
                "a3b2: 1\n" +
                "a3b4: 1\n" +
                "a3c5: 1\n" +
                "a3d6: 1\n" +
                "a3e7: 1\n" +
                "a3f8: 1\n" +
                "c8b7: 1\n" +
                "h8f8: 1\n" +
                "h8g8: 1\n" +
                "d8e7: 1\n" +
                "e8e7: 1\n" +
                "e8f8: 1";
        Testing.perftDiff(stockFishPerft, myPerft);
    }
}
