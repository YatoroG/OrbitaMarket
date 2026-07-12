package sys.util.exception;

public class AccountAlreadyExistsException extends PaymentException {
    public AccountAlreadyExistsException() {
        super("ACCOUNT_ALREADY_EXISTS", "Account already exists for this user ID");
    }
}
