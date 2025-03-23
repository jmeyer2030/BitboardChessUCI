package engine.search;

import static engine.search.Search.MAX_SEARCH_DEPTH;

/**
* Represents a triangular PV table:
* for a search of depth N, for a leaf node we would store 0 PV moves (since there are no following moves)
* at the root node, we would store a PV of length N, since there are N moves made until a leaf node.
*
*
*
*
*
*
*/
public class TraingularPVTable {
    int[] triangularPV;



    /**
    *
    *
    */
    public int tableSize() {
        return (MAX_SEARCH_DEPTH * MAX_SEARCH_DEPTH + MAX_SEARCH_DEPTH) / 2;
    }
}
