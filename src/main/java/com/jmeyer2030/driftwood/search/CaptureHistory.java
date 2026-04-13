package com.jmeyer2030.driftwood.search;

import com.jmeyer2030.driftwood.board.MoveEncoding;

/**
 * Capture history table indexed by [color][movedPiece][toSquare][capturedPiece].
 * Used as a scaled tiebreaker within MVV-based capture ordering (MVV is the
 * primary key; capture history is divided by {@link MoveOrder#CAPTURE_HISTORY_DIVISOR}
 * before being added to the score).
 *
 * <p>Uses the same gravity-based update formula as {@link HistoryHeuristic}
 * to keep entries bounded within [-MAX_CAPTURE_HISTORY, MAX_CAPTURE_HISTORY].</p>
 *
 * <p>On a beta cutoff caused by a capture, the cutoff capture receives a bonus
 * and all previously searched captures that didn't cause a cutoff receive a malus.</p>
 */
public class CaptureHistory {
    private static final int MAX_CAPTURE_HISTORY = 16_384;

    // captureHistory[color][movedPiece][toSquare][capturedPiece]
    private final int[][][][] table;

    public CaptureHistory() {
        table = new int[2][6][64][6];
    }

    /**
     * Awards a bonus to a capture that caused a beta cutoff.
     *
     * @param color         side making the capture
     * @param move          the capture move (must have capture flag set)
     * @param depth         depth remaining when the cutoff occurred
     */
    public void addBonus(int color, int move, int depth) {
        int piece    = MoveEncoding.getMovedPiece(move);
        int to       = MoveEncoding.getDestination(move);
        int captured = MoveEncoding.getCapturedPiece(move);

        int bonus = depth * depth;
        int clampedBonus = Math.clamp(bonus, -MAX_CAPTURE_HISTORY, MAX_CAPTURE_HISTORY);
        int gravity = clampedBonus - table[color][piece][to][captured] * Math.abs(clampedBonus) / MAX_CAPTURE_HISTORY;

        table[color][piece][to][captured] += gravity;
    }

    /**
     * Applies a malus to a capture that was searched but did not cause a beta cutoff.
     *
     * @param color         side making the capture
     * @param move          the capture move (must have capture flag set)
     * @param depth         depth remaining at the node
     */
    public void addMalus(int color, int move, int depth) {
        int piece    = MoveEncoding.getMovedPiece(move);
        int to       = MoveEncoding.getDestination(move);
        int captured = MoveEncoding.getCapturedPiece(move);

        int bonus = -(depth * depth);
        int clampedBonus = Math.clamp(bonus, -MAX_CAPTURE_HISTORY, MAX_CAPTURE_HISTORY);
        int gravity = clampedBonus - table[color][piece][to][captured] * Math.abs(clampedBonus) / MAX_CAPTURE_HISTORY;

        table[color][piece][to][captured] += gravity;
    }

    /**
     * Returns the capture history score for a given capture move.
     *
     * @param color side making the capture
     * @param move  the capture move
     * @return history score in [-MAX_CAPTURE_HISTORY, MAX_CAPTURE_HISTORY]
     */
    public int getScore(int color, int move) {
        int piece    = MoveEncoding.getMovedPiece(move);
        int to       = MoveEncoding.getDestination(move);
        int captured = MoveEncoding.getCapturedPiece(move);
        return table[color][piece][to][captured];
    }
}

