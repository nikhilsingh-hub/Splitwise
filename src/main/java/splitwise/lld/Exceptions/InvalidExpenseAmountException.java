package splitwise.lld.Exceptions;

public class InvalidExpenseAmountException extends RuntimeException {
    public InvalidExpenseAmountException(String message) {
        super(message);
    }
}
