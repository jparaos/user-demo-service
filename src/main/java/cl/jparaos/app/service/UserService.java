package cl.jparaos.app.service;

import cl.jparaos.app.config.JwtTokenUtil;
import cl.jparaos.app.exception.DuplicatedUserException;
import cl.jparaos.app.exception.InvalidEmailException;
import cl.jparaos.app.exception.InvalidPasswordException;
import cl.jparaos.app.exception.InvalidTokenException;
import cl.jparaos.app.model.Phone;
import cl.jparaos.app.model.User;
import cl.jparaos.app.repository.PhoneRepository;
import cl.jparaos.app.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Service
public class UserService {

    @Value("${pass.regex}")
    String passRegex;

    @Value("${email.regex}")
    String emailRegex;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhoneRepository phoneRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtTokenUtil tokenUtil;

    public User createUser(User user) throws Exception{
        User output;
        List<Phone> phones = user.getPhones();

        if(!validatePassword(user.getPassword()))
            throw new InvalidPasswordException("Error registering user. Invalid password.",new Exception());

        if(!validateEmail(user.getEmail()))
            throw new InvalidEmailException("Error registering user. Invalid email.",new Exception());

        if(getUserByEmail(user.getEmail())!=null)
            throw new DuplicatedUserException("Error registering user. User email already exist.",new Exception());

        user.setCreated(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setActive(true);
        user.setPassword(encoder.encode(user.getPassword()));
        String token = tokenUtil.doGenerateToken(user.getEmail());
        user.setToken(token);

        output = userRepository.save(user);
        phoneRepository.saveAll(phones);

        return output;
    }

    public User getUserByEmail(String userEmail){
        Optional<User> user = Optional.ofNullable(userRepository.getUserByEmail(userEmail));
        return user.orElse(null);
    }

    public User getUserByEmailAndToken(String userEmail, String token){
        Optional<User> user = Optional.ofNullable(userRepository.getUserByEmailAndToken(userEmail,token));
        return user.orElse(null);
    }

    public boolean validatePassword(String password){
        Pattern pattern = Pattern.compile(passRegex);
        return pattern.matcher(password).matches();
    }

    public boolean validateEmail(String email){
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    public User getUserByToken(String token) throws Exception{

        if (token == null || !tokenUtil.validateToken(token) || tokenUtil.isTokenExpired(token))
            throw new InvalidTokenException("Error consulting user. Invalid token.",new Exception());

        String email = tokenUtil.getUsernameFromToken(token);
        log.info("user email = "+email);
        User user=getUserByEmailAndToken(email,token);
        if(user==null)
            throw new InvalidTokenException("Error consulting user. Invalid token information.",new Exception());

        user.setToken(tokenUtil.doGenerateToken(email));
        user.setLastLogin(LocalDateTime.now());

        return userRepository.save(user);
    }
}