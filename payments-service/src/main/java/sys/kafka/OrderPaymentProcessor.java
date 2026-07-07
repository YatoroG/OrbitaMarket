package sys.kafka;

import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sys.kafka.enums.EventType;
import sys.kafka.inbox.InboxEventService;
import sys.service.BalanceService;


import static sys.kafka.inbox.InboxEventUtils.createFailedInboxEvent;
import static sys.kafka.inbox.InboxEventUtils.createSuccessInboxEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderPaymentProcessor {
    private final InboxEventService inboxEventService;
    private final BalanceService balanceService;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void process(OrderEvent event) {
        if (event == null) return;

        if (inboxEventService.existsByOrderId(event.orderId())) {
            log.warn("[Kafka] Повторный запрос для заказа {} пропущен", event.orderId());
            sendResult(event, EventType.OrderPaymentCompleted.toString());
            return;
        }

        try {
            Integer currentBalance = balanceService.deductWithCasRetries(event.userId(), event.amount());
            inboxEventService.saveToInbox(createSuccessInboxEvent(event, currentBalance));
            sendResult(event, EventType.OrderPaymentCompleted.toString());
            log.info("[Kafka] Платеж по заказу {} успешно обработан", event.orderId());
        } catch (Exception e) {
            log.error("[Kafka] Ошибка при обработке платежа заказа {}", event.orderId(), e);
            inboxEventService.saveToInbox(createFailedInboxEvent(event, e.getMessage()));
            sendResult(event, EventType.OrderPaymentFailed.toString());
        }
    }

    private void sendResult(OrderEvent event, String eventType) {
        try {
            OrderEvent response = new OrderEvent(
                    UUID.randomUUID(),
                    event.orderId(),
                    event.userId(),
                    event.amount(),
                    eventType,
                    LocalDateTime.now()
            );
            kafkaTemplate.send("payment-results", event.orderId().toString(), response);
            log.info("[Kafka] Результат платежа {} отправлен", event.orderId());
        } catch (Exception e) {
            log.error("[Kafka] Не удалось отправить ответ {}", eventType, e);
        }
    }
}
