package splitwise.lld.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class UserExpense extends BaseClass{
    @ManyToOne
    private User user;

    @ManyToOne
    private Expense expense;

    @Enumerated(EnumType.STRING)
    private UserExpenseType userExpenseType;

    private double amount;
}
