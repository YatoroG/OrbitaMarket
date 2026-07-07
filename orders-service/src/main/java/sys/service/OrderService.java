package sys.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sys.kafka.OrderEvent;
import sys.kafka.enums.EventType;
import sys.kafka.outbox.OutboxEventService;
import sys.model.Order;
import sys.model.enums.OrderStatus;
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
    private final OrderRepository orderRepository;
    private final OutboxEventService outboxEventService;

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(order -> new OrderResponse(
                        order.getId(),
                        order.getUserId(),
                        order.getProductType(),
                        order.getPrice(),
                        order.getStatus().name()
                ))
                .orElseThrow(OrderNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(order -> new OrderResponse(
                        order.getId(),
                        order.getUserId(),
                        order.getProductType(),
                        order.getPrice(),
                        order.getStatus().name()
                ))
                .collect(Collectors.toList());
    }

    public OrderResponse addOrder(String userId, AddOrderRequest request) {
        if (request.productType() == null) {
            throw new UnknownProductTypeException();
        }

        if (request.payload() == null || request.payload().isMissingNode()) {
            throw new InvalidPayloadException();
        }

        if (request.price() == null || request.price() <= 0) {
            throw new InvalidPriceException();
        }

        UUID orderId = UUID.randomUUID();

        Order order = Order.builder()
                .id(orderId)
                .userId(userId)
                .productType(request.productType())
                .payload(request.payload())
                .price(request.price())
                .status(OrderStatus.PAYMENT_PENDING)
                .build();
        orderRepository.save(order);

        OrderEvent orderEvent = new OrderEvent(
                UUID.randomUUID(),
                orderId,
                userId,
                request.price(),
                EventType.OrderPaymentRequested.toString(),
                LocalDateTime.now()
        );

        outboxEventService.publishToOutbox(orderId, orderEvent);
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getProductType(),
                order.getPrice(),
                order.getStatus().name()
        );
    }

    @Transactional
    public void setOrderPaid(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
        if (order.getStatus() == OrderStatus.PAYMENT_PENDING) {
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);
        }
    }

    @Transactional
    public void setOrderPaymentFailed(UUID orderId, String reason) {
        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
        if (order.getStatus() == OrderStatus.PAYMENT_PENDING) {
            order.setStatus(OrderStatus.PAYMENT_FAILED);
            order.setFailureReason(reason);
            orderRepository.save(order);
        }
    }
}
