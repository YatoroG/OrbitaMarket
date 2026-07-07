package sys.util.exception;

public class InsufficientBalanceException extends PaymentException {
    public InsufficientBalanceException() {
        super("INSUFFICIENT_BALANCE", "Account balance must be equal or bigger than amount");
    }
}
