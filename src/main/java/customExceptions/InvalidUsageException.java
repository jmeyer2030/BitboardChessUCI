package customExceptions;

/**
 * When the usage of a method is unexpected (e.g. a method is called under some assumption that is not valid)
 */
public class InvalidUsageException extends RuntimeException {
    public InvalidUsageException(String message) {
        super(message);
    }
}
