package splitwise.lld.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class GroupExpenseSummaryDto {
    private Long groupId;
    private String groupName;
    private double totalExpenses;
    private int expenseCount;
    private List<ExpenseResponseDto> expenses;
    private Map<Long, Double> memberBalances; // userId -> net balance in group
    private List<UserResponseDto> members;
}
