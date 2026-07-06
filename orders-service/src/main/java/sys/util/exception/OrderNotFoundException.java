package sys.util.exception;

public class OrderNotFoundException extends OrderException {
    public OrderNotFoundException() {
        super("ORDER_NOT_FOUND",
                "Order not found or user_id belongs to someone else");
    }
}
