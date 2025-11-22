package splitwise.lld.DTOs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettleExpenseRequestDto {
    @NotNull(message = "From user ID is required")
    private Long fromUserId;
    
    @NotNull(message = "To user ID is required")
    private Long toUserId;
    
    @Positive(message = "Amount must be positive")
    private double amount;
}
