package sys.kafka;

import java.time.LocalDateTime;
import java.util.UUID;
import sys.kafka.enums.EventType;

public record PaymentEvent(UUID eventId, UUID orderId,
                           String userId, Integer amount,
                           EventType eventType, String reason,
                           LocalDateTime timestamp)
{}
