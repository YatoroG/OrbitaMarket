package sys.util.exception;

public class UnknownProductTypeException extends OrderException {
    public UnknownProductTypeException() {
        super("UNKNOWN_PRODUCT_TYPE", "Unsupported product type");
    }
}
