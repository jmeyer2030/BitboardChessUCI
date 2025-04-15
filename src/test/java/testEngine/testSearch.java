package test.java.testEngine;

import main.java.board.Position;
import main.java.board.PositionState;
import main.java.engine.search.Search;

public class testSearch {
    public static void main(String[] args) {
        PositionState positionState = new PositionState(18);
        Position position = new Position();

        Search.iterativeDeepening(position, 100_000_000, positionState);
    }
}
