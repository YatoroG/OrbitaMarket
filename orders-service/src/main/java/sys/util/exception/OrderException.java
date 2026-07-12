package sys.util.exception;

import lombok.Getter;

@Getter
public abstract class OrderException extends RuntimeException {
    private final String errorCode;

    protected OrderException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
