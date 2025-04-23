package engine;

public class TimeManagement {

    /**
    * Dead simple time management scheme that just returns time / 20
    * Probably not optimal, but works well enough.
    */
    public static long millisForMove(long millisRemaining, long millisIncrement) {
       if (millisRemaining < 0) {
           return 0;
       }
       return millisRemaining/20;
    }
}
