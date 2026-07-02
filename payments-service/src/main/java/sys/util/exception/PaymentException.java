package sys.util.exception;

import lombok.Getter;

@Getter
public abstract class PaymentException extends RuntimeException {
    private final String errorCode;

    protected PaymentException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
