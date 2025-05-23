package engine.search;

import board.MoveEncoding;

public class HistoryHeuristic {
    private int maxHistory = 20000;
    private int[][][] history;

    public HistoryHeuristic() {
        history = new int[2][64][64];
    }

    public void addMove(int color, int move, int depth, int bonus) {
        int from = MoveEncoding.getStart(move);
        int to = MoveEncoding.getDestination(move);
        int clampedBonus = Math.clamp(bonus, -maxHistory, maxHistory);
        history[color][from][to] += clampedBonus - history[color][from][to] * Math.abs(clampedBonus) / maxHistory;
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
