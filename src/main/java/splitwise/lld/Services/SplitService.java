package splitwise.lld.Services;

import splitwise.lld.DTOs.UserExpenseRequestDto;
import splitwise.lld.models.SplitType;

import java.util.List;

public interface SplitService {
    List<UserExpenseRequestDto> splitExpense(SplitType splitType, double totalAmount, List<Long> userIds, List<Double> values);
}
