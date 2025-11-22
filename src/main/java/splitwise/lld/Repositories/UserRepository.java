package splitwise.lld.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import splitwise.lld.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
