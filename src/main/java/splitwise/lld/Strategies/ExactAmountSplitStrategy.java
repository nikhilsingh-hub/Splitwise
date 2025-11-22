package splitwise.lld.Strategies;

import org.springframework.stereotype.Component;
import splitwise.lld.DTOs.UserExpenseRequestDto;
import splitwise.lld.models.UserExpenseType;

import java.util.ArrayList;
import java.util.List;

@Component
public class ExactAmountSplitStrategy implements SplitStrategy {
    
    @Override
    public List<UserExpenseRequestDto> split(double totalAmount, List<Long> userIds, List<Double> amounts) {
        if (userIds == null || userIds.isEmpty() || amounts == null || amounts.isEmpty()) {
            throw new IllegalArgumentException("User IDs and amounts cannot be empty");
        }
        
        if (userIds.size() != amounts.size()) {
            throw new IllegalArgumentException("Number of users must match number of amounts");
        }
        
        double totalAmountSum = amounts.stream().mapToDouble(Double::doubleValue).sum();
        if (Math.abs(totalAmountSum - totalAmount) > 0.01) {
            throw new IllegalArgumentException("Sum of individual amounts must equal total amount");
        }
        
        List<UserExpenseRequestDto> userExpenses = new ArrayList<>();
        
        for (int i = 0; i < userIds.size(); i++) {
            UserExpenseRequestDto userExpense = new UserExpenseRequestDto();
            userExpense.setUserId(userIds.get(i));
            userExpense.setUserExpenseType(UserExpenseType.TO_GIVE);
            userExpense.setAmount(amounts.get(i));
            userExpenses.add(userExpense);
        }
        
        return userExpenses;
    }
}
