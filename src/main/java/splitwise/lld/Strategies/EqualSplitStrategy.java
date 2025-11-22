package splitwise.lld.Strategies;

import org.springframework.stereotype.Component;
import splitwise.lld.DTOs.UserExpenseRequestDto;
import splitwise.lld.models.UserExpenseType;

import java.util.ArrayList;
import java.util.List;

@Component
public class EqualSplitStrategy implements SplitStrategy {
    
    @Override
    public List<UserExpenseRequestDto> split(double totalAmount, List<Long> userIds, List<Double> values) {
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("User IDs cannot be empty");
        }
        
        double amountPerUser = totalAmount / userIds.size();
        List<UserExpenseRequestDto> userExpenses = new ArrayList<>();
        
        for (Long userId : userIds) {
            UserExpenseRequestDto userExpense = new UserExpenseRequestDto();
            userExpense.setUserId(userId);
            userExpense.setUserExpenseType(UserExpenseType.TO_GIVE);
            userExpense.setAmount(amountPerUser);
            userExpenses.add(userExpense);
        }
        
        return userExpenses;
    }
}
