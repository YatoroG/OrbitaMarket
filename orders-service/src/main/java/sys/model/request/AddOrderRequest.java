package sys.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import sys.model.enums.ProductType;

public record AddOrderRequest(@JsonProperty("product_type") ProductType productType,
                              Integer price, JsonNode payload) {
}