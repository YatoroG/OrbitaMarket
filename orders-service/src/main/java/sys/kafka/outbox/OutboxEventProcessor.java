package sys.kafka.outbox;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxEventProcessor {
    private final OutboxEventRepository outboxEventRepository;

    public OutboxEventProcessor(OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = outboxEventRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<OutboxEvent> fetchAndLockEvents(int batchSize) {
        List<OutboxEvent> pending = outboxEventRepository.findByStatusOrderByCreatedAtAsc(
                OutboxEvent.OutboxStatus.PENDING, PageRequest.of(0, batchSize));

        if (pending.isEmpty()) {
            return List.of();
        }

        List<UUID> eventIds = pending.stream()
                .map(OutboxEvent::getEventId)
                .collect(Collectors.toList());

        outboxEventRepository.updateStatusForIds(eventIds, OutboxEvent.OutboxStatus.PROCESSING);
        return pending;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateEventStatus(UUID id, OutboxEvent.OutboxStatus status, LocalDateTime time) {
        outboxEventRepository.updateStatus(id, status, time);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateEventRetry(UUID id, OutboxEvent.OutboxStatus status) {
        outboxEventRepository.incrementRetry(id, status);
    }
}
