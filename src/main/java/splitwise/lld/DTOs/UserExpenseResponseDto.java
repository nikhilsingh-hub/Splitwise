package splitwise.lld.DTOs;

import lombok.Getter;
import lombok.Setter;
import splitwise.lld.models.UserExpenseType;

@Getter
@Setter
public class UserExpenseResponseDto {
    private Long id;
    private UserResponseDto user;
    private UserExpenseType userExpenseType;
    private double amount;
}
