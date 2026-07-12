package sys.util.exception;

public class InvalidAmountException extends PaymentException {
    public InvalidAmountException() {
        super("INVALID_AMOUNT", "Amount must be greater than zero");
    }
}
