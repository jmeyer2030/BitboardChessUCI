package com.jmeyer2030.driftwood.search;

import com.jmeyer2030.driftwood.board.MoveEncoding;

public class HistoryHeuristic {
    private static final int MAX_HISTORY = 16_384; // TODO: Tune this. Probably is essentially infinite when maybe we want more churn
    private final int[][][] history; // history[color][from][to]

    public HistoryHeuristic() {
        history = new int[2][64][64];
    }

    /**
     * When a quiet move causes a beta cutoff, we add value used in ordering
     * - If the cutoff is expected, we care less than if it is not expected
     * - We use depth(left) because we want to value closer to the root nodes more.
     *    - We do not use HMC because that does the opposite
     *
     * @Param color side making the move
     * @Param move the move we add
     * @Param depth depthLeft in the search
     */
    public void addMove(int color, int move, int depth) {
        int from = MoveEncoding.getStart(move);
        int to = MoveEncoding.getDestination(move);

        int bonus = depth * depth;
        int clampedBonus = Math.clamp(bonus, -MAX_HISTORY, MAX_HISTORY);
        // Reduce by a factor of the previous score
        int historyGravity = clampedBonus - history[color][from][to] * Math.abs(clampedBonus) / MAX_HISTORY;

        history[color][from][to] += historyGravity;
    }

    /**
     * When we add a move to HH, we penalize other moves using this function.
     * Uses the same gravity formula as addMove to keep entries bounded within [-MAX_HISTORY, MAX_HISTORY].
     */
    public void penalizeMove(int color, int move, int depth) {
        int from = MoveEncoding.getStart(move);
        int to = MoveEncoding.getDestination(move);

        int bonus = -(depth * depth);
        int clampedBonus = Math.clamp(bonus, -MAX_HISTORY, MAX_HISTORY);
        int historyGravity = clampedBonus - history[color][from][to] * Math.abs(clampedBonus) / MAX_HISTORY;

        history[color][from][to] += historyGravity;
    }

    public int getHeuristic(int move, int color) {
        int from = MoveEncoding.getStart(move);
        int to = MoveEncoding.getDestination(move);
        return history[color][from][to];
    }
}
