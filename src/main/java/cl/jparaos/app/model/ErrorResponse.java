package cl.jparaos.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class ErrorResponse {
    private List<Message> error;
}
