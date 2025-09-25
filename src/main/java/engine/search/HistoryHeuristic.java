package engine.search;

import board.MoveEncoding;

public class HistoryHeuristic {
    private static final int MAX_HISTORY = 20000;
    private int[][][] history; // history[color][from][to]

    public HistoryHeuristic() {
        history = new int[2][64][64];
    }

    /**
    * When a quiet move causes a beta cutoff, we add value used in ordering
    */
    public void addMove(int color, int move, int depth) {
        int from = MoveEncoding.getStart(move);
        int to = MoveEncoding.getDestination(move);

        int bonus = getBonus(history[color][from][to], depth);
        history[color][from][to] += bonus;
    }

    /**
    * When we add a move to HH, we penalize other moves using this function
    */
    public void penalizeMove(int color, int move, int depth) {
        int from = MoveEncoding.getStart(move);
        int to = MoveEncoding.getDestination(move);

        int bonus = getNegativeBonus(depth);
        history[color][from][to] += bonus;
    }

    /**
    * History Gravity so make the HH higher if unexpected, and lower if expected
    */
    private int getBonus(int prevHH, int depth) {
        int bonus = depth * depth;
        int clampedBonus = Math.clamp(bonus, -MAX_HISTORY, MAX_HISTORY);

        // if this is expected, we care less.
        int historyGravity = clampedBonus - prevHH * Math.abs(clampedBonus) / MAX_HISTORY;
        return historyGravity;
    }

    private int getNegativeBonus(int depth) {
        return Math.clamp(-(depth * depth), -MAX_HISTORY, MAX_HISTORY);
    }


    public int getHeuristic(int move, int color) {
        int from = MoveEncoding.getStart(move);
        int to = MoveEncoding.getDestination(move);
        return history[color][from][to];
    }
}

/*
Used to help order non-capture moves

If a quiet move causes a beta cutoff -- increment arr[color][from][to] by depth * depth (to show favor to plys away from leaf nodes)


Store max
Or a more sophisticated approach
if (score >= beta) {

}


create int[2][64][64] for indexing arr[color][from][to]




*/
