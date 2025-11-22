package splitwise.lld.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import splitwise.lld.models.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
