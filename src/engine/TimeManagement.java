package engine;

public class TimeManagement {
    public static long millisForMove(long millisRemaining, long millisIncrement) {
       if (millisRemaining < 0) {
           return 0;
       }
       return millisRemaining/20;
    }
}
