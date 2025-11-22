package splitwise.lld.DTOs;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import splitwise.lld.models.SplitType;

import java.util.List;

@Getter
@Setter
public class SplitRequestDto {
    @NotNull(message = "Split type is required")
    private SplitType splitType;
    
    @Positive(message = "Total amount must be positive")
    private double totalAmount;
    
    @NotEmpty(message = "User IDs cannot be empty")
    private List<Long> userIds;
    
    // Optional: Used for percentage splits (percentages) or exact amount splits (amounts)
    private List<Double> values;
}
