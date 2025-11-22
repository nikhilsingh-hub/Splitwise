package splitwise.lld.models;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Expense  extends BaseClass{
    private String expenseNAme;
    private String description;

    @Enumerated(EnumType.STRING)
    private ExpenseType expenseType;

    @ManyToOne
    private User createdBy; //admin

    private double amount;

    @OneToMany(mappedBy = "expense")
    private List<UserExpense> userExpenses;
}
