package com.jmeyer2030.driftwood.quickDebugTests;

import com.jmeyer2030.driftwood.board.Position;
import com.jmeyer2030.driftwood.board.SharedTables;
import com.jmeyer2030.driftwood.search.Search;
import com.jmeyer2030.driftwood.search.SearchContext;

public class testSearch {
    public static void main(String[] args) {
        SearchContext searchContext = new SearchContext();
        SharedTables sharedTables = new SharedTables(18);
        Position position = new Position();

        Search.iterativeDeepening(position, 100_000_000, searchContext, sharedTables);
    }
}
