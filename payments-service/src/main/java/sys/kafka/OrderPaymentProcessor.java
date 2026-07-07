package sys.kafka;

import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sys.kafka.inbox.InboxEventService;
import sys.kafka.inbox.InboxEventUtils;
import sys.service.BalanceService;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderPaymentProcessor {
    private final InboxEventService inboxEventService;
    private final BalanceService balanceService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void process(String message) {
        OrderEvent event = parseEvent(message);
        if (event == null) return;

        if (inboxEventService.existsByEventId(event.eventId())) {
            log.warn("[Kafka] Повтор сообщения {} пропущен", event.eventId());
            return;
        }

        try {
            Integer currentBalance = balanceService.deductWithCasRetries(event.userId(), event.amount());
            inboxEventService.saveToInbox(InboxEventUtils.createSuccessInboxEvent(event, currentBalance));
            sendResult(event.orderId(), "OrderPaymentCompleted");

        } catch (Exception e) {
            log.error("[Kafka] Ошибка при обработке платежа заказа {}", event.orderId(), e);
            inboxEventService.saveToInbox(InboxEventUtils.createFailedInboxEvent(event, e.getMessage()));
            sendResult(event.orderId(), "OrderPaymentFailed");
        }
    }

    private OrderEvent parseEvent(String message) {
        try {
            return objectMapper.readValue(message, OrderEvent.class);
        } catch (Exception e) {
            log.error("[Kafka] Критическая ошибка десериализации JSON", e);
            return null;
        }
    }

    private void sendResult(UUID orderId, String eventType) {
        try {
            ObjectNode nodes = objectMapper.createObjectNode();
            nodes.put("order_id", orderId.toString());
            nodes.put("event_type", eventType);
            nodes.put("timestamp", LocalDateTime.now().toString());
            kafkaTemplate.send("payment-results", orderId.toString(), nodes.toString());
        } catch (Exception e) {
            log.error("[Kafka] Не удалось отправить ответ {}", eventType, e);
        }
    }
}
