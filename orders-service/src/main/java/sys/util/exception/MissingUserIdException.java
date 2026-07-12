package sys.util.exception;

public class MissingUserIdException extends OrderException {
    public MissingUserIdException() {
        super("MISSING_USER_ID", "No user_id");
    }
}
