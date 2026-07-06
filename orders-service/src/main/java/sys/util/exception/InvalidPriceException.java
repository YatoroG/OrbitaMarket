package sys.util.exception;

public class InvalidPriceException extends OrderException {
    public InvalidPriceException() {
        super("INVALID_PRICE", "Price must be greater than zero");
    }
}
