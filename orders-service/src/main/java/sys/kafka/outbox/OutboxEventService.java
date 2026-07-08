package sys.kafka.outbox;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sys.kafka.KafkaService;
import sys.kafka.OrderEvent;

@Slf4j
@RequiredArgsConstructor
@Service
public class OutboxEventService {
    private final KafkaService kafkaService;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final OutboxEventProcessor outboxProcessor;

    private static final String ORDERS_EVENTS_TOPIC = "order-payment-requests";
    private static final int BATCH_SIZE = 50;

    @Transactional
    public void publishToOutbox(UUID orderId, OrderEvent event) {
        try {
            JsonNode jsonPayload = objectMapper.valueToTree(event);
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .orderId(orderId)
                    .eventType(event.eventType())
                    .payload(jsonPayload)
                    .createdAt(LocalDateTime.now())
                    .status(OutboxEvent.OutboxStatus.PENDING)
                    .retryCount(0)
                    .build();
            outboxEventRepository.save(outboxEvent);
        } catch (Exception e) {
            log.error("[Kafka] Не удалось сериализовать outbox-событие", e);
            throw new RuntimeException("Failed to serialize outbox event", e);
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {
        List<OutboxEvent> eventsToProcess = outboxProcessor.fetchAndLockEvents(BATCH_SIZE);
        if (eventsToProcess.isEmpty()) {
            return;
        }

        for (OutboxEvent event : eventsToProcess) {
            try {
                OrderEvent orderEvent = objectMapper.treeToValue(event.getPayload(), OrderEvent.class);
                kafkaService.sentToKafkaOrders(ORDERS_EVENTS_TOPIC, orderEvent);
                outboxProcessor.updateEventStatus(event.getEventId(), OutboxEvent.OutboxStatus.SENT, LocalDateTime.now());
                log.info("[Kafka] Outbox-событие {} отправлено", event.getEventId());
            } catch (Exception e) {
                log.error("Ошибка при отправке outbox-события {}: {}", event.getEventId(), e.getMessage());
                if (event.getRetryCount() >= 3) {
                    outboxProcessor.updateEventStatus(event.getEventId(), OutboxEvent.OutboxStatus.FAILED, LocalDateTime.now());
                } else {
                    outboxProcessor.updateEventRetry(event.getEventId(), OutboxEvent.OutboxStatus.PENDING);
                }
            }
        }
    }

    public void updateEventType(UUID orderId, String newEventType) {
        outboxEventRepository.updateEventType(orderId, newEventType);
    }

}
