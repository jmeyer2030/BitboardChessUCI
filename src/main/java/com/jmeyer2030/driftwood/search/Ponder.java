package com.jmeyer2030.driftwood.search;

import com.jmeyer2030.driftwood.board.Position;
import com.jmeyer2030.driftwood.board.SharedTables;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.jmeyer2030.driftwood.search.Search.MAX_SEARCH_DEPTH;
import static com.jmeyer2030.driftwood.search.Search.getSearchCallable;

/**
 * Ponders the position using the given search context and shared tables
 */
public class Ponder {

    private static volatile boolean pondering = false;

    public static boolean isPondering() {
        return pondering;
    }


    public static void startPondering(Position position, SearchContext searchContext, SharedTables sharedTables) {
        pondering = true;
        ExecutorService executor = Executors.newSingleThreadExecutor();

        new Thread(() -> {
            int depth = 0;
            while (pondering && depth < MAX_SEARCH_DEPTH) {
                depth++;

                Callable<Search.MoveValue> task = getSearchCallable(position, depth, searchContext, sharedTables);

                Future<Search.MoveValue> future = executor.submit(task);


                while (!future.isDone() && pondering) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }

                if (!pondering) {
                    future.cancel(true);
                    break;
                }

            }

            executor.shutdownNow();
        }).start();
    }


    public static void stopPondering() {
        pondering = false;
    }

}
