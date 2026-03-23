package com.jmeyer2030.driftwood.userfeatures.perft;

public record PerftElement(long zobristHash, int depth, long perftResult) {
}
