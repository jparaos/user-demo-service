package cl.jparaos.app.exception;

public class InvalidTokenException extends Exception{

    public InvalidTokenException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
