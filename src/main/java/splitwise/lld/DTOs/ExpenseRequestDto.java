package splitwise.lld.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import splitwise.lld.models.ExpenseType;

import java.util.List;

@Getter
@Setter
public class ExpenseRequestDto {
    @NotBlank(message = "Expense name is required")
    private String expenseName;
    
    private String description;
    
    @NotNull(message = "Expense type is required")
    private ExpenseType expenseType;
    
    @NotNull(message = "Creator ID is required")
    private Long createdById;
    
    @NotNull(message = "Group ID is required")
    private Long groupId;
    
    @Positive(message = "Amount must be positive")
    private double amount;
    
    @NotNull(message = "User expenses are required")
    private List<UserExpenseRequestDto> userExpenses;
}
