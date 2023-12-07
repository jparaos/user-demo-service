package cl.jparaos.app.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity(name = "phones")
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String number;
    private String cityCode;
    private String countryCode;
}
