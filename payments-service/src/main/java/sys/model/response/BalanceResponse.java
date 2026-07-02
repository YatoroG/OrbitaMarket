package sys.model.response;

import java.math.BigDecimal;
import java.util.UUID;

public record BalanceResponse(UUID user_id, BigDecimal balance, String currency) {
}
