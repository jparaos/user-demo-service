package cl.jparaos.app.exception;

public class InvalidEmailException extends Exception{

    public InvalidEmailException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
