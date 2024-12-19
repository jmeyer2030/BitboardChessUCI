package Search;

import board.Position;
import moveGeneration.MoveGenerator;

public class TestSearch {

    public static void main(String[] args) {
        new MoveGenerator();
        Position position = new Position();
        minimax.iterativeDeepening(position, 10_000);
    }
}
