package testing.testEngine;

import board.Position;
import engine.Search;

import static engine.Search.*;
import static testing.testEngine.LegacySearch.negaMax;

public class TestHelpSearch {
    /*
     * Testing related methods
     */
    public static void repeatedttNegaMax(Position position, int depth) throws InterruptedException {
        for (int i = 1; i <= depth; i++) {
            Search.negamax(NEGINFINITY, POSINFINITY, depth, position);
        }
    }

    public static void repeatedNegaMax(Position position, int depth) {
        for (int i = 1; i <= depth; i++) {
            negaMax(NEGINFINITY, POSINFINITY, depth, position);
        }
    }


}
