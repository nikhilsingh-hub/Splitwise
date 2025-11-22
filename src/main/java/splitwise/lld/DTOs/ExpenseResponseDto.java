package splitwise.lld.DTOs;

import lombok.Getter;
import lombok.Setter;
import splitwise.lld.models.ExpenseType;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ExpenseResponseDto {
    private Long id;
    private String expenseName;
    private String description;
    private ExpenseType expenseType;
    private UserResponseDto createdBy;
    private GroupResponseDto group;
    private double amount;
    private Date createdAt;
    private List<UserExpenseResponseDto> userExpenses;
}
