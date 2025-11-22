package splitwise.lld.Exceptions;

public class ExpenseNotFoundException extends RuntimeException {
    public ExpenseNotFoundException(String message) {
        super(message);
    }
}
