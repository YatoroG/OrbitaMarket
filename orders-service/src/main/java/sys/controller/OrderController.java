package sys.controller;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sys.model.request.AddOrderRequest;
import sys.model.response.OrderResponse;
import sys.service.OrderService;

@RestController
@RequestMapping("/api/v1/orders/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestHeader(value = "X-User-Id") String userId,
                                     @RequestBody AddOrderRequest request) {
        return orderService.addOrder(userId, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getUserOrders(@RequestHeader(value = "X-User-Id") String userId) {
        return orderService.getOrdersByUserId(userId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse getOrderById(@PathVariable("id") UUID orderId) {
        return orderService.getOrderById(orderId);
    }
}
