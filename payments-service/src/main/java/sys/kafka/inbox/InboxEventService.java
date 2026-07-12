package sys.kafka.inbox;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sys.kafka.OrderEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class InboxEventService {
    private final InboxEventRepository inboxEventRepository;

    @Transactional(readOnly = true)
    public boolean existsByOrderId(UUID orderId) {
        return inboxEventRepository.existsByOrderId(orderId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSuccess(OrderEvent event, Integer currentBalance) {
        InboxEvent inboxEvent = InboxEvent.builder()
                .eventId(event.eventId())
                .orderId(event.orderId())
                .status("PROCESSED")
                .amount(event.amount())
                .newBalance(currentBalance)
                .processedAt(LocalDateTime.now())
                .build();
        inboxEventRepository.save(inboxEvent);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailed(OrderEvent event, String errorMessage) {
        InboxEvent inboxEvent = InboxEvent.builder()
                .eventId(event.eventId())
                .orderId(event.orderId())
                .status("FAILED")
                .amount(event.amount())
                .errorMessage(errorMessage)
                .processedAt(LocalDateTime.now())
                .build();
        inboxEventRepository.save(inboxEvent);
    }
}
