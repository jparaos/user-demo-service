package cl.jparaos.app.controller;

import cl.jparaos.app.exception.DuplicatedUserException;
import cl.jparaos.app.exception.InvalidEmailException;
import cl.jparaos.app.exception.InvalidPasswordException;
import cl.jparaos.app.exception.InvalidTokenException;
import cl.jparaos.app.model.ErrorResponse;
import cl.jparaos.app.model.Message;
import cl.jparaos.app.model.User;
import cl.jparaos.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collections;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<Object> signUp(@RequestBody User user){
        try{
            User createdUser = userService.createUser(user);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        }catch (InvalidPasswordException | InvalidEmailException e){
            return new ResponseEntity<>(
                    new ErrorResponse(Collections.singletonList(new Message(HttpStatus.BAD_REQUEST.value(),LocalDateTime.now(),e.getMessage()))),
                    HttpStatus.BAD_REQUEST);
        } catch (DuplicatedUserException e){
            return new ResponseEntity<>(
                    new ErrorResponse(Collections.singletonList(new Message(HttpStatus.CONFLICT.value(),LocalDateTime.now(),e.getMessage()))),
                    HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ErrorResponse(Collections.singletonList(new Message(HttpStatus.INTERNAL_SERVER_ERROR.value(),LocalDateTime.now(),"Unexpected error. "+e.getMessage()))),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody User user){

        try{
            User output = userService.getUserByToken(user.getToken());
            return new ResponseEntity<>(output, HttpStatus.OK);
        }catch(InvalidTokenException e){
            return new ResponseEntity<>(
                    new ErrorResponse(Collections.singletonList(new Message(HttpStatus.UNAUTHORIZED.value(),LocalDateTime.now(),e.getMessage()))),
                    HttpStatus.UNAUTHORIZED);
        }catch(Exception e){
            return new ResponseEntity<>(
                    new ErrorResponse(Collections.singletonList(new Message(HttpStatus.INTERNAL_SERVER_ERROR.value(),LocalDateTime.now(),"Unexpected error. "+e.getMessage()))),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
