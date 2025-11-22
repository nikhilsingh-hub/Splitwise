package splitwise.lld.DTOs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import splitwise.lld.models.UserExpenseType;

@Getter
@Setter
public class UserExpenseRequestDto {
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "User expense type is required")
    private UserExpenseType userExpenseType;
    
    @Positive(message = "Amount must be positive")
    private double amount;
}
