package sys.kafka;

import java.time.LocalDateTime;
import java.util.UUID;
import sys.kafka.enums.EventType;

public record OrderEvent(UUID eventId, UUID orderId,
                         String userId, Integer amount,
                         String eventType, LocalDateTime timestamp)
{}