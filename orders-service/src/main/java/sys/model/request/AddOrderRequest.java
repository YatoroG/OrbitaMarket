package sys.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import sys.model.enums.ProductType;

public record AddOrderRequest(@JsonProperty("product_type")
                              @JsonFormat(with = JsonFormat.Feature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                              ProductType productType,
                              Integer price,
                              JsonNode payload) {
}