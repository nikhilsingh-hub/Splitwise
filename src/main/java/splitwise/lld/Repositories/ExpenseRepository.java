package splitwise.lld.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import splitwise.lld.models.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}
