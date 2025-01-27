package testing.testEngine;

import board.Position;
import customExceptions.InvalidPositionException;
import engine.Search;
import system.SearchMonitor;

import static engine.Search.*;
//import static testing.testEngine.LegacySearch.negaMax;

public class TestHelpSearch {
    /*
     * Testing related methods
     */
    public static void repeatedttNegaMax(Position position, int depth) throws InterruptedException, InvalidPositionException {
        for (int i = 1; i <= depth; i++) {
            //Search.negamax(NEG_INFINITY, POS_INFINITY, depth, position, new SearchMonitor(position));
        }
    }

    public static void repeatedNegaMax(Position position, int depth) {
        for (int i = 1; i <= depth; i++) {
            //negaMax(NEG_INFINITY, POS_INFINITY, depth, position);
        }
    }


}
