package sys.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sys.model.Order;
import sys.model.enums.OrderStatus;
import sys.model.enums.ProductType;
import sys.model.request.AddOrderRequest;
import sys.model.response.OrderResponse;
import sys.repository.OrderRepository;
import sys.util.exception.InvalidPayloadException;
import sys.util.exception.InvalidPriceException;
import sys.util.exception.OrderNotFoundException;
import sys.util.exception.UnknownProductTypeException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private static final int ARCHIVE_PRICE = 120;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);
        return mapToResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse addOrder(String userId, AddOrderRequest request) {
        if (request.productType() == null) {
            throw new UnknownProductTypeException();
        }

        int price = calculatePrice(request.productType(), request.payload());
        if (price <= 0) {
            throw new InvalidPriceException();
        }

        UUID orderId = UUID.randomUUID();
        Order order = Order.builder()
                .id(orderId)
                .userId(userId)
                .productType(request.productType())
                .payload(request.payload())
                .price(price)
                .status(OrderStatus.PAYMENT_PENDING)
                .build();

        orderRepository.save(order);
        return mapToResponse(order);
    }

    private int calculatePrice(ProductType type, Map<String, Object> payload) {
        if (payload == null || payload.isEmpty()) {
            throw new InvalidPayloadException();
        }

        return switch (type) {
            case ARCHIVE -> {
                if (!payload.containsKey("snapshot_id")) {
                    throw new InvalidPayloadException();
                }
                yield ARCHIVE_PRICE;
            }
            case TASKING, MONITORING -> 0;
        };
    }

    private OrderResponse mapToResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getProductType(),
                order.getPrice(),
                order.getStatus().name()
        );
    }
}
