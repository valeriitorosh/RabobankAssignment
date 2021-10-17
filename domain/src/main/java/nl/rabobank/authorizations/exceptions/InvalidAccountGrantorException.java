package nl.rabobank.authorizations.exceptions;

public class InvalidAccountGrantorException extends RuntimeException{
    public InvalidAccountGrantorException(String message) {
        super(message);
    }
}
