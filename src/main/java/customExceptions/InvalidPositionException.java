package main.java.customExceptions;

public class InvalidPositionException extends Exception{
    public InvalidPositionException(String message) {
        super(message);
    }
}
