package splitwise.lld.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import splitwise.lld.DTOs.BalanceResponseDto;
import splitwise.lld.DTOs.UserRequestDto;
import splitwise.lld.DTOs.UserResponseDto;
import splitwise.lld.Exceptions.UserNotFoundException;
import splitwise.lld.Repositories.UserExpenseRepository;
import splitwise.lld.Repositories.UserRepository;
import splitwise.lld.models.User;
import splitwise.lld.models.UserExpense;
import splitwise.lld.models.UserExpenseType;

import java.util.*;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserExpenseRepository userExpenseRepository;
    
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User user = new User();
        user.setName(userRequestDto.getName());
        user.setPhoneNumber(userRequestDto.getPhoneNumber());
        user.setEmail(userRequestDto.getEmail());
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        
        User savedUser = userRepository.save(user);
        return convertToResponseDto(savedUser);
    }
    
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return convertToResponseDto(user);
    }
    
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToResponseDto)
                .toList();
    }
    
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        
        user.setName(userRequestDto.getName());
        user.setPhoneNumber(userRequestDto.getPhoneNumber());
        user.setEmail(userRequestDto.getEmail());
        user.setUpdatedAt(new Date());
        
        User updatedUser = userRepository.save(user);
        return convertToResponseDto(updatedUser);
    }
    
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
    
    public BalanceResponseDto getUserBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        
        List<UserExpense> userExpenses = userExpenseRepository.findAll()
                .stream()
                .filter(ue -> ue.getUser().getId() == userId)
                .toList();
        
        Map<Long, Double> balances = new HashMap<>();
        double totalBalance = 0.0;
        
        for (UserExpense userExpense : userExpenses) {
            Long expenseCreatorId = userExpense.getExpense().getCreatedBy().getId();
            double amount = userExpense.getAmount();
            
            if (userExpense.getUserExpenseType() == UserExpenseType.TO_GIVE) {
                // User owes money to expense creator
                balances.put(expenseCreatorId, balances.getOrDefault(expenseCreatorId, 0.0) - amount);
                totalBalance -= amount;
            } else if (userExpense.getUserExpenseType() == UserExpenseType.TO_TAKE) {
                // Expense creator owes money to user
                balances.put(expenseCreatorId, balances.getOrDefault(expenseCreatorId, 0.0) + amount);
                totalBalance += amount;
            }
        }
        
        BalanceResponseDto balanceResponse = new BalanceResponseDto();
        balanceResponse.setUserId(userId);
        balanceResponse.setUserName(user.getName());
        balanceResponse.setBalances(balances);
        balanceResponse.setTotalBalance(totalBalance);
        
        return balanceResponse;
    }
    
    private UserResponseDto convertToResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setEmail(user.getEmail());
        return dto;
    }
    
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }
}
