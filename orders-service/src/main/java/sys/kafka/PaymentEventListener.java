package sys.kafka;

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
    private static final String TOPIC = "payment-results";

    @KafkaListener(topics = TOPIC, groupId = "orders-backend-group")
    public void handlePaymentResult(OrderEvent result) {
        if (result == null) return;

        log.info("[Kafka] Получен результат обработки платежа для заказа: {}", result.orderId());

        try {
            String type = result.eventType();

            if ("OrderPaymentCompleted".equalsIgnoreCase(type)) {
                orderService.setOrderPaid(result.orderId());
                log.info("[Business] Статус заказа {} успешно обновлен на PAID", result.orderId());
            } else if ("OrderPaymentFailed".equalsIgnoreCase(type)) {
                orderService.setOrderPaymentFailed(result.orderId(), "INSUFFICIENT_BALANCE");
                log.info("[Business] Заказ {} отклонен: недостаточно средств", result.orderId());
            } else {
                log.error("[Kafka] Получен неизвестный тип события: {}", type);
            }
        } catch (Exception e) {
            log.error("[Kafka] Критическая ошибка при обработке результата платежа для заказа {}", result.orderId(), e);
        }
    }
}
