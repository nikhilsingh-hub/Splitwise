package splitwise.lld.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity(name = "groupss")
public class Group  extends BaseClass{
    private String name;
    @ManyToMany
    private List<User> member;
    @ManyToOne
    private User created_By;
}
