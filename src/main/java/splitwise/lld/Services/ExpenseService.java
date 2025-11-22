package splitwise.lld.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

import splitwise.lld.DTOs.*;
import splitwise.lld.Exceptions.ExpenseNotFoundException;
import splitwise.lld.Exceptions.InvalidExpenseAmountException;
import splitwise.lld.Repositories.ExpenseRepository;
import splitwise.lld.Repositories.UserExpenseRepository;
import splitwise.lld.models.*;

import java.util.Date;
import java.util.List;

@Service
public class ExpenseService {
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private UserExpenseRepository userExpenseRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private GroupService groupService;
    
    public ExpenseResponseDto createExpense(ExpenseRequestDto expenseRequestDto) {
        // Validate total amounts
        double totalUserExpenseAmount = expenseRequestDto.getUserExpenses().stream()
                .mapToDouble(UserExpenseRequestDto::getAmount)
                .sum();
        
        if (Math.abs(totalUserExpenseAmount - expenseRequestDto.getAmount()) > 0.01) {
            throw new InvalidExpenseAmountException("Total user expense amounts must equal the expense amount");
        }
        
        // Create expense
        Expense expense = new Expense();
        expense.setExpenseNAme(expenseRequestDto.getExpenseName());
        expense.setDescription(expenseRequestDto.getDescription());
        expense.setExpenseType(expenseRequestDto.getExpenseType());
        expense.setAmount(expenseRequestDto.getAmount());
        
        User creator = userService.findById(expenseRequestDto.getCreatedById());
        expense.setCreatedBy(creator);
        
        Group group = groupService.findById(expenseRequestDto.getGroupId());
        expense.setGroup(group);
        
        // Validate that creator is a member of the group
        if (!group.getMember().contains(creator)) {
            throw new IllegalArgumentException("Creator must be a member of the group");
        }
        
        // Validate that all users in the expense are members of the group
        List<Long> groupMemberIds = group.getMember().stream()
                .map(User::getId)
                .toList();
        
        for (UserExpenseRequestDto userExpenseDto : expenseRequestDto.getUserExpenses()) {
            if (!groupMemberIds.contains(userExpenseDto.getUserId())) {
                throw new IllegalArgumentException("All users in expense must be members of the group");
            }
        }
        
        expense.setCreatedAt(new Date());
        expense.setUpdatedAt(new Date());
        
        Expense savedExpense = expenseRepository.save(expense);
        
        // Create user expenses
        List<UserExpense> userExpenses = expenseRequestDto.getUserExpenses().stream()
                .map(userExpenseDto -> {
                    UserExpense userExpense = new UserExpense();
                    userExpense.setExpense(savedExpense);
                    userExpense.setUser(userService.findById(userExpenseDto.getUserId()));
                    userExpense.setUserExpenseType(userExpenseDto.getUserExpenseType());
                    userExpense.setAmount(userExpenseDto.getAmount());
                    userExpense.setCreatedAt(new Date());
                    userExpense.setUpdatedAt(new Date());
                    return userExpense;
                })
                .toList();
        
        List<UserExpense> savedUserExpenses = userExpenseRepository.saveAll(userExpenses);
        savedExpense.setUserExpenses(savedUserExpenses);
        
        return convertToResponseDto(savedExpense);
    }
    
    public ExpenseResponseDto getExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found with id: " + id));
        return convertToResponseDto(expense);
    }
    
    public List<ExpenseResponseDto> getAllExpenses() {
        List<Expense> expenses = expenseRepository.findAll();
        return expenses.stream()
                .map(this::convertToResponseDto)
                .toList();
    }
    
    public List<ExpenseResponseDto> getExpensesByGroup(Long groupId) {
        Group group = groupService.findById(groupId); // Validate group exists
        List<Expense> expenses = expenseRepository.findAll().stream()
                .filter(expense -> expense.getGroup() != null && expense.getGroup().getId()==groupId)
                .toList();
        return expenses.stream()
                .map(this::convertToResponseDto)
                .toList();
    }
    
    public List<ExpenseResponseDto> getExpensesByUser(Long userId) {
        User user = userService.findById(userId); // Validate user exists
        List<Expense> expenses = expenseRepository.findAll().stream()
                .filter(expense -> expense.getUserExpenses().stream()
                        .anyMatch(ue -> ue.getUser().getId()==userId))
                .toList();
        return expenses.stream()
                .map(this::convertToResponseDto)
                .toList();
    }
    
    public GroupExpenseSummaryDto getGroupExpenseSummary(Long groupId) {
        Group group = groupService.findById(groupId);
        List<Expense> groupExpenses = expenseRepository.findAll().stream()
                .filter(expense -> expense.getGroup() != null && expense.getGroup().getId()==groupId)
                .toList();
        
        GroupExpenseSummaryDto summary = new GroupExpenseSummaryDto();
        summary.setGroupId(groupId);
        summary.setGroupName(group.getName());
        
        // Calculate total expenses and count
        double totalExpenses = groupExpenses.stream()
                .filter(expense -> expense.getExpenseType() == splitwise.lld.models.ExpenseType.REAL)
                .mapToDouble(Expense::getAmount)
                .sum();
        summary.setTotalExpenses(totalExpenses);
        summary.setExpenseCount(groupExpenses.size());
        
        // Convert expenses to DTOs
        List<ExpenseResponseDto> expenseDtos = groupExpenses.stream()
                .map(this::convertToResponseDto)
                .toList();
        summary.setExpenses(expenseDtos);
        
        // Calculate member balances
        Map<Long, Double> memberBalances = new HashMap<>();
        for (User member : group.getMember()) {
            memberBalances.put(member.getId(), 0.0);
        }
        
        // Calculate balances from all user expenses in the group
        for (Expense expense : groupExpenses) {
            for (UserExpense userExpense : expense.getUserExpenses()) {
                Long userId = userExpense.getUser().getId();
                double amount = userExpense.getAmount();
                
                if (userExpense.getUserExpenseType() == UserExpenseType.TO_GIVE) {
                    memberBalances.put(userId, memberBalances.getOrDefault(userId, 0.0) - amount);
                } else if (userExpense.getUserExpenseType() == UserExpenseType.TO_TAKE) {
                    memberBalances.put(userId, memberBalances.getOrDefault(userId, 0.0) + amount);
                }
            }
        }
        summary.setMemberBalances(memberBalances);
        
        // Convert group members to DTOs
        List<UserResponseDto> memberDtos = group.getMember().stream()
                .map(member -> {
                    UserResponseDto memberDto = new UserResponseDto();
                    memberDto.setId(member.getId());
                    memberDto.setName(member.getName());
                    memberDto.setPhoneNumber(member.getPhoneNumber());
                    memberDto.setEmail(member.getEmail());
                    return memberDto;
                })
                .toList();
        summary.setMembers(memberDtos);
        
        return summary;
    }
    
    public ExpenseResponseDto updateExpense(Long id, ExpenseRequestDto expenseRequestDto) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found with id: " + id));
        
        // Validate total amounts
        double totalUserExpenseAmount = expenseRequestDto.getUserExpenses().stream()
                .mapToDouble(UserExpenseRequestDto::getAmount)
                .sum();
        
        if (Math.abs(totalUserExpenseAmount - expenseRequestDto.getAmount()) > 0.01) {
            throw new InvalidExpenseAmountException("Total user expense amounts must equal the expense amount");
        }
        
        expense.setExpenseNAme(expenseRequestDto.getExpenseName());
        expense.setDescription(expenseRequestDto.getDescription());
        expense.setExpenseType(expenseRequestDto.getExpenseType());
        expense.setAmount(expenseRequestDto.getAmount());
        
        // Update group if provided
        if (expenseRequestDto.getGroupId() != null) {
            Group group = groupService.findById(expenseRequestDto.getGroupId());
            expense.setGroup(group);
            
            // Validate that creator is still a member of the group
            if (!group.getMember().contains(expense.getCreatedBy())) {
                throw new IllegalArgumentException("Creator must be a member of the group");
            }
        }
        
        expense.setUpdatedAt(new Date());
        
        Expense updatedExpense = expenseRepository.save(expense);
        
        // Delete existing user expenses and create new ones
        userExpenseRepository.deleteAll(expense.getUserExpenses());
        
        List<UserExpense> userExpenses = expenseRequestDto.getUserExpenses().stream()
                .map(userExpenseDto -> {
                    UserExpense userExpense = new UserExpense();
                    userExpense.setExpense(updatedExpense);
                    userExpense.setUser(userService.findById(userExpenseDto.getUserId()));
                    userExpense.setUserExpenseType(userExpenseDto.getUserExpenseType());
                    userExpense.setAmount(userExpenseDto.getAmount());
                    userExpense.setCreatedAt(new Date());
                    userExpense.setUpdatedAt(new Date());
                    return userExpense;
                })
                .toList();
        
        List<UserExpense> savedUserExpenses = userExpenseRepository.saveAll(userExpenses);
        updatedExpense.setUserExpenses(savedUserExpenses);
        
        return convertToResponseDto(updatedExpense);
    }
    
    public void deleteExpense(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found with id: " + id));
        
        // Delete associated user expenses first
        userExpenseRepository.deleteAll(expense.getUserExpenses());
        expenseRepository.deleteById(id);
    }
    
    public boolean settleExpense(SettleExpenseRequestDto settleRequest) {
        // Find user expenses between the two users
        List<UserExpense> userExpenses = userExpenseRepository.findAll().stream()
                .filter(ue -> 
                    (ue.getUser().getId()==settleRequest.getFromUserId() ||
                     ue.getUser().getId()==settleRequest.getToUserId()) &&
                    ue.getExpense().getCreatedBy().getId()==settleRequest.getFromUserId() ||
                    ue.getExpense().getCreatedBy().getId()==settleRequest.getToUserId()
                )
                .toList();
        
        // Create a dummy settlement expense
        Expense settlementExpense = new Expense();
        settlementExpense.setExpenseNAme("Settlement between users");
        settlementExpense.setDescription("Settlement expense");
        settlementExpense.setExpenseType(splitwise.lld.models.ExpenseType.DUMMY);
        settlementExpense.setAmount(settleRequest.getAmount());
        settlementExpense.setCreatedBy(userService.findById(settleRequest.getFromUserId()));
        settlementExpense.setCreatedAt(new Date());
        settlementExpense.setUpdatedAt(new Date());
        
        Expense savedSettlementExpense = expenseRepository.save(settlementExpense);
        
        // Create settlement user expenses
        UserExpense fromUserExpense = new UserExpense();
        fromUserExpense.setExpense(savedSettlementExpense);
        fromUserExpense.setUser(userService.findById(settleRequest.getFromUserId()));
        fromUserExpense.setUserExpenseType(splitwise.lld.models.UserExpenseType.TO_GIVE);
        fromUserExpense.setAmount(settleRequest.getAmount());
        fromUserExpense.setCreatedAt(new Date());
        fromUserExpense.setUpdatedAt(new Date());
        
        UserExpense toUserExpense = new UserExpense();
        toUserExpense.setExpense(savedSettlementExpense);
        toUserExpense.setUser(userService.findById(settleRequest.getToUserId()));
        toUserExpense.setUserExpenseType(splitwise.lld.models.UserExpenseType.TO_TAKE);
        toUserExpense.setAmount(settleRequest.getAmount());
        toUserExpense.setCreatedAt(new Date());
        toUserExpense.setUpdatedAt(new Date());
        
        userExpenseRepository.save(fromUserExpense);
        userExpenseRepository.save(toUserExpense);
        
        return true;
    }
    
    private ExpenseResponseDto convertToResponseDto(Expense expense) {
        ExpenseResponseDto dto = new ExpenseResponseDto();
        dto.setId(expense.getId());
        dto.setExpenseName(expense.getExpenseNAme());
        dto.setDescription(expense.getDescription());
        dto.setExpenseType(expense.getExpenseType());
        dto.setAmount(expense.getAmount());
        dto.setCreatedAt(expense.getCreatedAt());
        
        // Convert creator
        UserResponseDto creatorDto = new UserResponseDto();
        creatorDto.setId(expense.getCreatedBy().getId());
        creatorDto.setName(expense.getCreatedBy().getName());
        creatorDto.setPhoneNumber(expense.getCreatedBy().getPhoneNumber());
        creatorDto.setEmail(expense.getCreatedBy().getEmail());
        dto.setCreatedBy(creatorDto);
        
        // Convert group
        if (expense.getGroup() != null) {
            GroupResponseDto groupDto = new GroupResponseDto();
            groupDto.setId(expense.getGroup().getId());
            groupDto.setName(expense.getGroup().getName());
            
            // Convert group creator
            UserResponseDto groupCreatorDto = new UserResponseDto();
            groupCreatorDto.setId(expense.getGroup().getCreated_By().getId());
            groupCreatorDto.setName(expense.getGroup().getCreated_By().getName());
            groupCreatorDto.setPhoneNumber(expense.getGroup().getCreated_By().getPhoneNumber());
            groupCreatorDto.setEmail(expense.getGroup().getCreated_By().getEmail());
            groupDto.setCreatedBy(groupCreatorDto);
            
            // Convert group members
            List<UserResponseDto> memberDtos = expense.getGroup().getMember().stream()
                    .map(member -> {
                        UserResponseDto memberDto = new UserResponseDto();
                        memberDto.setId(member.getId());
                        memberDto.setName(member.getName());
                        memberDto.setPhoneNumber(member.getPhoneNumber());
                        memberDto.setEmail(member.getEmail());
                        return memberDto;
                    })
                    .toList();
            groupDto.setMembers(memberDtos);
            
            dto.setGroup(groupDto);
        }
        
        // Convert user expenses
        if (expense.getUserExpenses() != null) {
            List<UserExpenseResponseDto> userExpenseDtos = expense.getUserExpenses().stream()
                    .map(userExpense -> {
                        UserExpenseResponseDto userExpenseDto = new UserExpenseResponseDto();
                        userExpenseDto.setId(userExpense.getId());
                        userExpenseDto.setUserExpenseType(userExpense.getUserExpenseType());
                        userExpenseDto.setAmount(userExpense.getAmount());
                        
                        UserResponseDto userDto = new UserResponseDto();
                        userDto.setId(userExpense.getUser().getId());
                        userDto.setName(userExpense.getUser().getName());
                        userDto.setPhoneNumber(userExpense.getUser().getPhoneNumber());
                        userDto.setEmail(userExpense.getUser().getEmail());
                        userExpenseDto.setUser(userDto);
                        
                        return userExpenseDto;
                    })
                    .toList();
            dto.setUserExpenses(userExpenseDtos);
        }
        
        return dto;
    }
}
