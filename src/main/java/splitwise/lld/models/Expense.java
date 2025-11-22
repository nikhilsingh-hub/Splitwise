package splitwise.lld.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Entity
public class Expense  extends BaseClass{
    private String expenseNAme;
    private String description;

    @Enumerated(EnumType.STRING)
    private ExpenseType expenseType;

    @ManyToOne
    private User createdBy; //admin

    private double amount;

    @ManyToOne
    private Group group;

    @OneToMany(mappedBy = "expense")
    private List<UserExpense> userExpenses;
}
