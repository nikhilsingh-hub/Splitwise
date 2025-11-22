package splitwise.lld.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class BalanceResponseDto {
    private Long userId;
    private String userName;
    private Map<Long, Double> balances; // userId -> amount (positive means they owe you, negative means you owe them)
    private double totalBalance; // net balance
}
