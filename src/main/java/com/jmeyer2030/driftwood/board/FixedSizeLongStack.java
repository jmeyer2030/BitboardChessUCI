package com.jmeyer2030.driftwood.board;

import com.jmeyer2030.driftwood.config.GlobalConstants;

/**
 * A fixed-size, allocation-free long stack backed by a primitive array.
 * Used to save/restore {@code long} state (e.g. checkers bitboard) across
 * make/unmake without boxing or GC pressure.
 *
 * <p>Capacity is governed by {@link GlobalConstants#MAX_GAME_MOVES}.</p>
 */
public final class FixedSizeLongStack {
    private final long[] data;
    private int size;

    public FixedSizeLongStack() {
        this.data = new long[GlobalConstants.MAX_GAME_MOVES];
        this.size = 0;
    }

    /** Private constructor used by {@link #copy()}. */
    private FixedSizeLongStack(long[] source, int size) {
        this.data = new long[source.length];
        System.arraycopy(source, 0, this.data, 0, size);
        this.size = size;
    }

    /** Push a value onto the stack. */
    public void push(long value) {
        data[size++] = value;
    }

    /** Pop the top value from the stack and return it. */
    public long pop() {
        return data[--size];
    }

    /** Returns a shallow copy with the same logical contents. */
    public FixedSizeLongStack copy() {
        return new FixedSizeLongStack(this.data, this.size);
    }
}

