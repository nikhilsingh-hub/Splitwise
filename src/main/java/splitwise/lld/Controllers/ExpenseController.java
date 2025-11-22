package splitwise.lld.Controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import splitwise.lld.DTOs.*;
import splitwise.lld.Services.ExpenseService;
import splitwise.lld.Services.SplitService;
import splitwise.lld.models.SplitType;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    
    @Autowired
    private ExpenseService expenseService;
    
    @Autowired
    private SplitService splitService;
    
    @PostMapping
    public ResponseEntity<ExpenseResponseDto> createExpense(@Valid @RequestBody ExpenseRequestDto expenseRequestDto) {
        ExpenseResponseDto createdExpense = expenseService.createExpense(expenseRequestDto);
        return new ResponseEntity<>(createdExpense, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponseDto> getExpenseById(@PathVariable Long id) {
        ExpenseResponseDto expense = expenseService.getExpenseById(id);
        return ResponseEntity.ok(expense);
    }
    
    @GetMapping
    public ResponseEntity<List<ExpenseResponseDto>> getAllExpenses() {
        List<ExpenseResponseDto> expenses = expenseService.getAllExpenses();
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<ExpenseResponseDto>> getExpensesByGroup(@PathVariable Long groupId) {
        List<ExpenseResponseDto> expenses = expenseService.getExpensesByGroup(groupId);
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExpenseResponseDto>> getExpensesByUser(@PathVariable Long userId) {
        List<ExpenseResponseDto> expenses = expenseService.getExpensesByUser(userId);
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/group/{groupId}/summary")
    public ResponseEntity<GroupExpenseSummaryDto> getGroupExpenseSummary(@PathVariable Long groupId) {
        GroupExpenseSummaryDto summary = expenseService.getGroupExpenseSummary(groupId);
        return ResponseEntity.ok(summary);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponseDto> updateExpense(
            @PathVariable Long id, 
            @Valid @RequestBody ExpenseRequestDto expenseRequestDto) {
        ExpenseResponseDto updatedExpense = expenseService.updateExpense(id, expenseRequestDto);
        return ResponseEntity.ok(updatedExpense);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/settle")
    public ResponseEntity<String> settleExpense(@Valid @RequestBody SettleExpenseRequestDto settleRequest) {
        boolean settled = expenseService.settleExpense(settleRequest);
        if (settled) {
            return ResponseEntity.ok("Expense settled successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to settle expense");
        }
    }
    
    // Unified split endpoint using Strategy pattern
    @PostMapping("/split")
    public ResponseEntity<List<UserExpenseRequestDto>> splitExpense(
            @Valid @RequestBody SplitRequestDto splitRequest) {
        List<UserExpenseRequestDto> userExpenses = splitService.splitExpense(
                splitRequest.getSplitType(),
                splitRequest.getTotalAmount(),
                splitRequest.getUserIds(),
                splitRequest.getValues()
        );
        return ResponseEntity.ok(userExpenses);
    }
    
    // Legacy endpoints for backward compatibility
    @PostMapping("/split/equally")
    public ResponseEntity<List<UserExpenseRequestDto>> splitEqually(
            @RequestParam double amount,
            @RequestBody List<Long> userIds) {
        List<UserExpenseRequestDto> userExpenses = splitService.splitExpense(
                SplitType.EQUAL, amount, userIds, null);
        return ResponseEntity.ok(userExpenses);
    }
    
    @PostMapping("/split/percentage")
    public ResponseEntity<List<UserExpenseRequestDto>> splitByPercentage(
            @RequestParam double amount,
            @RequestBody SplitByPercentageRequest request) {
        List<UserExpenseRequestDto> userExpenses = splitService.splitExpense(
                SplitType.PERCENTAGE, amount, request.getUserIds(), request.getPercentages());
        return ResponseEntity.ok(userExpenses);
    }
    
    @PostMapping("/split/exact")
    public ResponseEntity<List<UserExpenseRequestDto>> splitByExactAmount(
            @RequestBody SplitByExactAmountRequest request) {
        double totalAmount = request.getAmounts().stream().mapToDouble(Double::doubleValue).sum();
        List<UserExpenseRequestDto> userExpenses = splitService.splitExpense(
                SplitType.EXACT_AMOUNT, totalAmount, request.getUserIds(), request.getAmounts());
        return ResponseEntity.ok(userExpenses);
    }
    
    // Inner classes for split requests
    public static class SplitByPercentageRequest {
        private List<Long> userIds;
        private List<Double> percentages;
        
        public List<Long> getUserIds() { return userIds; }
        public void setUserIds(List<Long> userIds) { this.userIds = userIds; }
        public List<Double> getPercentages() { return percentages; }
        public void setPercentages(List<Double> percentages) { this.percentages = percentages; }
    }
    
    public static class SplitByExactAmountRequest {
        private List<Long> userIds;
        private List<Double> amounts;
        
        public List<Long> getUserIds() { return userIds; }
        public void setUserIds(List<Long> userIds) { this.userIds = userIds; }
        public List<Double> getAmounts() { return amounts; }
        public void setAmounts(List<Double> amounts) { this.amounts = amounts; }
    }
}
