package splitwise.lld.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "users")
public class User extends BaseClass{
    private String name;
    private String phoneNumber;
    private String email;
}
