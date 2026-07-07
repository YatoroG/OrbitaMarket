package sys.kafka.inbox;

import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;
import sys.kafka.OrderEvent;

@UtilityClass
public class InboxEventUtils {
    public static InboxEvent createSuccessInboxEvent(OrderEvent event, Integer newBalance) {
        return InboxEvent.builder()
                .eventId(event.eventId())
                .orderId(event.orderId())
                .status(InboxEvent.InboxStatus.PROCESSED)
                .amount(event.amount())
                .newBalance(newBalance)
                .processedAt(LocalDateTime.now())
                .build();
    }

    public static InboxEvent createFailedInboxEvent(OrderEvent event, String errorMessage) {
        return InboxEvent.builder()
                .eventId(event.eventId())
                .orderId(event.orderId())
                .status(InboxEvent.InboxStatus.FAILED)
                .amount(event.amount())
                .errorMessage(errorMessage)
                .processedAt(LocalDateTime.now())
                .build();
    }
}
