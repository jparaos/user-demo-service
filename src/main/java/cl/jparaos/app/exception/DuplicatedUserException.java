package cl.jparaos.app.exception;

public class DuplicatedUserException extends Exception{

    public DuplicatedUserException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
