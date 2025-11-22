package splitwise.lld.Strategies;

import splitwise.lld.DTOs.UserExpenseRequestDto;

import java.util.List;

public interface SplitStrategy {
    List<UserExpenseRequestDto> split(double totalAmount, List<Long> userIds, List<Double> values);
}
