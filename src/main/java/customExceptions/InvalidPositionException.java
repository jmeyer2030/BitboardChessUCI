package customExceptions;

/**
* When the position is not possible (e.g. two pieces on the same square)
*/
public class InvalidPositionException extends RuntimeException {
    public InvalidPositionException(String message) {
        super(message);
    }
}
