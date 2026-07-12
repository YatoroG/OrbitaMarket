package sys.util.exception;

public class AccountNotFoundException extends PaymentException {
    public AccountNotFoundException() {
        super("ACCOUNT_NOT_FOUND", "Account not found");
    }
}
