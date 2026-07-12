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
                .status("PROCESSED")
                .amount(event.amount())
                .newBalance(newBalance)
                .processedAt(LocalDateTime.now())
                .build();
    }

    public static InboxEvent createFailedInboxEvent(OrderEvent event, String errorMessage) {
        return InboxEvent.builder()
                .eventId(event.eventId())
                .orderId(event.orderId())
                .status("FAILED")
                .amount(event.amount())
                .errorMessage(errorMessage)
                .processedAt(LocalDateTime.now())
                .build();
    }
}
