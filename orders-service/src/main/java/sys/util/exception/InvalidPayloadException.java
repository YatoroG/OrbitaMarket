package sys.util.exception;

public class InvalidPayloadException extends OrderException {
    public InvalidPayloadException() {
        super("INVALID_PAYLOAD", "Required fields in payload are missing");
    }
}
