package cl.jparaos.app.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super("El correo ya registrado: " + email);
    }
}
