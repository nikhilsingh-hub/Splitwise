package splitwise.lld.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import splitwise.lld.DTOs.UserExpenseRequestDto;
import splitwise.lld.Strategies.EqualSplitStrategy;
import splitwise.lld.Strategies.ExactAmountSplitStrategy;
import splitwise.lld.Strategies.PercentageSplitStrategy;
import splitwise.lld.Strategies.SplitStrategy;
import splitwise.lld.models.SplitType;

import java.util.List;

@Service
public class SplitServiceImpl implements SplitService {
    
    @Autowired
    private EqualSplitStrategy equalSplitStrategy;
    
    @Autowired
    private PercentageSplitStrategy percentageSplitStrategy;
    
    @Autowired
    private ExactAmountSplitStrategy exactAmountSplitStrategy;
    
    @Override
    public List<UserExpenseRequestDto> splitExpense(SplitType splitType, double totalAmount, List<Long> userIds, List<Double> values) {
        SplitStrategy strategy = getSplitStrategy(splitType);
        return strategy.split(totalAmount, userIds, values);
    }
    
    private SplitStrategy getSplitStrategy(SplitType splitType) {
        return switch (splitType) {
            case EQUAL -> equalSplitStrategy;
            case PERCENTAGE -> percentageSplitStrategy;
            case EXACT_AMOUNT -> exactAmountSplitStrategy;
        };
    }
}
