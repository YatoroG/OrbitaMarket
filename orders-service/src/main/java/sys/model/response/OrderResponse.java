package sys.model.response;

import java.util.UUID;
import sys.model.enums.ProductType;

public record OrderResponse(UUID id, String userId, ProductType productType, Integer price, String status) {
}
