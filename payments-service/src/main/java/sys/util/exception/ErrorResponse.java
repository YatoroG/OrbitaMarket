package sys.util.exception;

public record ErrorResponse(
        String errorCode,
        String message,
        String timestamp
) {}
