package splitwise.lld.Strategies;

import org.springframework.stereotype.Component;
import splitwise.lld.DTOs.UserExpenseRequestDto;
import splitwise.lld.models.UserExpenseType;

import java.util.ArrayList;
import java.util.List;

@Component
public class PercentageSplitStrategy implements SplitStrategy {
    
    @Override
    public List<UserExpenseRequestDto> split(double totalAmount, List<Long> userIds, List<Double> percentages) {
        if (userIds == null || userIds.isEmpty() || percentages == null || percentages.isEmpty()) {
            throw new IllegalArgumentException("User IDs and percentages cannot be empty");
        }
        
        if (userIds.size() != percentages.size()) {
            throw new IllegalArgumentException("Number of users must match number of percentages");
        }
        
        double totalPercentage = percentages.stream().mapToDouble(Double::doubleValue).sum();
        if (Math.abs(totalPercentage - 100.0) > 0.01) {
            throw new IllegalArgumentException("Total percentage must equal 100%");
        }
        
        List<UserExpenseRequestDto> userExpenses = new ArrayList<>();
        
        for (int i = 0; i < userIds.size(); i++) {
            UserExpenseRequestDto userExpense = new UserExpenseRequestDto();
            userExpense.setUserId(userIds.get(i));
            userExpense.setUserExpenseType(UserExpenseType.TO_GIVE);
            userExpense.setAmount((totalAmount * percentages.get(i)) / 100.0);
            userExpenses.add(userExpense);
        }
        
        return userExpenses;
    }
}
