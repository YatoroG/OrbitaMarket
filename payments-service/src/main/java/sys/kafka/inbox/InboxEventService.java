package sys.kafka.inbox;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InboxEventService {
    private final InboxEventRepository inboxEventRepository;

    @Transactional(readOnly = true)
    public boolean existsByEventId(UUID eventId) {
        return inboxEventRepository.existsByEventId(eventId);
    }

    @Transactional
    public void saveToInbox(InboxEvent inboxEvent) {
        inboxEventRepository.save(inboxEvent);
        log.info("[Kafka] Получено inbox-событие: {}", inboxEvent.getEventId());
    }
}