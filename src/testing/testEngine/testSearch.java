package testing.testEngine;

import board.Position;
import board.PositionState;
import engine.search.Search;

public class testSearch {
    public static void main(String[] args) {
        PositionState positionState = new PositionState(18);
        Position position = new Position();

        Search.iterativeDeepening(position, 100_000_000, positionState);
    }
}
