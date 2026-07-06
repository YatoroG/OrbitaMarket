package sys.model.request;

import java.util.Map;
import sys.model.enums.ProductType;

public record AddOrderRequest(ProductType productType, Map<String, Object> payload) {
}
