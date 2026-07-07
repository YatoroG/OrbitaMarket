package sys.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import sys.service.OrderService;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "payment-results";

    @KafkaListener(topics = TOPIC, groupId = "orders-backend-group")
    public void handlePaymentResult(OrderEvent result) {
        log.info("[Kafka] Получен результат обработки платежа: {}", result.orderId());

        try {
            switch (result.eventType()) {
                case OrderPaymentCompleted -> {
                    orderService.setOrderPaid(result.orderId());
                }
                case OrderPaymentFailed -> {
                    orderService.setOrderPaymentFailed(result.orderId(), "INSUFFICIENT_BALANCE");
                }
                default -> log.error("[Kafka] Получен неизвестный тип события: {}", result.eventType());
            }

        } catch (Exception e) {
            log.error("[Kafka] Ошибка при обработке результата платежа", e);
        }
    }
}
