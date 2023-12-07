package cl.jparaos.app.exception;

public class InvalidPasswordException extends Exception{

    public InvalidPasswordException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
