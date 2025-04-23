package engine.search;

import board.MoveEncoding;

import static engine.search.Search.MAX_SEARCH_DEPTH;

/**
* Represents a triangular PV table:
* for a search of depth N, for a leaf node we would store 0 PV moves (since there are no following moves)
* at the root node, we would store a PV of length N, since there are N moves made until a leaf node.
*
* Thus, to represent this triangular array in 1-D, it's size should be 256 + 255 + 254 + ... + 1
*
* This is known as the triangular number of N, computed as N(N+1)/2
*
*
* if search depth is n, triangularPV[n] stores nothing
*
*
*
*/
public class TriangularPVTable {

    public static final int TABLE_SIZE = 256;
/*
    static {
        TABLE_SIZE = computeTableSize();
    }
    */

    //private int[] triangularPV;
    private int[][] triangularPV;
    private int[] pvLength;


    public TriangularPVTable() {
        this.triangularPV = new int[TABLE_SIZE][TABLE_SIZE];

        pvLength = new int[MAX_SEARCH_DEPTH];
    }

    public void storePV(int move, int depth) {
       triangularPV[depth][0] = move;

    }

    public void setPVLength(int ply) {
        this.pvLength[ply] = ply;
    }

    public void writePVMove(int move, int ply) {
        triangularPV[ply][ply] = move;

        for (int nextPly = ply + 1; nextPly < pvLength[ply + 1]; nextPly++) {
            triangularPV[ply][nextPly] = triangularPV[ply + 1][nextPly];
        }

        pvLength[ply] = pvLength[ply + 1];
    }

    public int getPVMove() {
        return triangularPV[0][0];
    }

    public int getBestResponse() {
        return triangularPV[0][1];
    }


    public String getPVLine() {
        StringBuilder bldr = new StringBuilder();
        for (int i = 0; i < pvLength[0]; i++) {
            bldr.append(MoveEncoding.getLAN(triangularPV[0][i]));
            if (i != pvLength[0] - 1) {
                bldr.append(" ");
            }
        }

        return bldr.toString();
    }

    public void grow(int depth) {

    }


    private void grow() {

    }


    /**
    * Returns the triangular number of the max search depth
    */
    private static int computeTableSize() {
        return (MAX_SEARCH_DEPTH * MAX_SEARCH_DEPTH + MAX_SEARCH_DEPTH) / 2;
    }
}
