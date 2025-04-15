package main.java.engine.search;

import main.java.board.Position;
import main.java.board.PositionState;

import java.util.concurrent.*;

import static main.java.engine.search.Search.MAX_SEARCH_DEPTH;
import static main.java.engine.search.Search.getMoveValueCallable;

/**
 * Ponders the position in position state
 */
public class Ponder {

   private static volatile boolean pondering = false;

    public static boolean isPondering() {
        return pondering;
    }


   public static void startPondering(Position position, PositionState positionState) {
       pondering = true;
       ExecutorService executor = Executors.newSingleThreadExecutor();

       new Thread(() -> {
           int depth = 0;
           while (pondering && depth < MAX_SEARCH_DEPTH) {
               depth++;

               Callable<Search.MoveValue> task = getMoveValueCallable(position, depth, positionState);

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
