package com.jmeyer2030.driftwood.board;

import com.jmeyer2030.driftwood.config.GlobalConstants;

/**
 * A fixed-size, allocation-free int stack backed by a primitive array.
 * Used to replace {@code Stack<Integer>} and {@code Stack<Byte>} in hot paths
 * (make/unmake) to eliminate boxing and GC pressure.
 *
 * <p>Capacity is governed by {@link GlobalConstants#MAX_GAME_MOVES}.</p>
 */
public final class FixedSizeIntStack {
    private final int[] data;
    private int size;

    public FixedSizeIntStack() {
        this.data = new int[GlobalConstants.MAX_GAME_MOVES];
        this.size = 0;
    }

    /** Private constructor used by {@link #copy()}. */
    private FixedSizeIntStack(int[] source, int size) {
        this.data = new int[source.length];
        System.arraycopy(source, 0, this.data, 0, size);
        this.size = size;
    }

    /** Push a value onto the stack. */
    public void push(int value) {
        data[size++] = value;
    }

    /** Pop the top value from the stack and return it. */
    public int pop() {
        return data[--size];
    }

    /** Returns a shallow copy with the same logical contents. */
    public FixedSizeIntStack copy() {
        return new FixedSizeIntStack(this.data, this.size);
    }
}

