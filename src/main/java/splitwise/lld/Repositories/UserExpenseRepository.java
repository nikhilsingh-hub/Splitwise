package splitwise.lld.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import splitwise.lld.models.UserExpense;

public interface UserExpenseRepository extends JpaRepository<UserExpense, Long> {
}
