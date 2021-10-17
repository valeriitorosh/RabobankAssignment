package nl.rabobank.account.exceptions;

public class InvalidAccountGrantorException extends RuntimeException{
    public InvalidAccountGrantorException(String message) {
        super(message);
    }
}
