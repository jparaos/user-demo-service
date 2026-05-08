package cl.jparaos.app.dto;

import lombok.Data;
import java.util.List;

@Data
public class SignUpRequest {
    private String name;
    private String email;
    private String password;
    private List<PhoneDto> phones;
}