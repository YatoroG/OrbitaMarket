package sys.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sys.kafka.outbox.OutboxEventService;
import sys.service.OrderService;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {
    private final OrderService orderService;
    private final OutboxEventService outboxEventService;

    private static final String TOPIC = "payment-results";

    @Transactional
    @KafkaListener(topics = TOPIC, groupId = "orders-backend-group")
    public void handlePaymentResult(OrderEvent result) {
        if (result == null) return;

        log.info("[Kafka] Получен результат обработки платежа для заказа: {}", result.orderId());

        try {
            String type = result.eventType();

            if ("OrderPaymentCompleted".equalsIgnoreCase(type)) {
                orderService.setOrderPaid(result.orderId());
                outboxEventService.updateEventType(result.orderId(), type);
                log.info("[Kafka] Статус заказа {} успешно обновлен на PAID", result.orderId());
            } else if ("OrderPaymentFailed".equalsIgnoreCase(type)) {
                orderService.setOrderPaymentFailed(result.orderId(), "INSUFFICIENT_BALANCE");
                outboxEventService.updateEventType(result.orderId(), type);
                log.info("[Kafka] Заказ {} отклонен: недостаточно средств", result.orderId());
            } else {
                log.error("[Kafka] Получен неизвестный тип события: {}", type);
            }
        } catch (Exception e) {
            log.error("[Kafka] Критическая ошибка при обработке результата платежа для заказа {}", result.orderId(), e);
        }
    }
}
