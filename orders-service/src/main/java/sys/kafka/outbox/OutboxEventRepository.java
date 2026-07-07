package sys.kafka.outbox;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    @Query("SELECT o FROM OutboxEvent o WHERE o.status = :status ORDER BY o.createdAt ASC")
    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(@Param("status") OutboxEvent.OutboxStatus status, Pageable pageable);

    @Modifying
    @Query("UPDATE OutboxEvent o SET o.status = :status, o.processedAt = :processedAt WHERE o.eventId = :id")
    void updateStatus(@Param("id") UUID id, @Param("status") OutboxEvent.OutboxStatus status,
                      @Param("processedAt") LocalDateTime processedAt);

    @Modifying
    @Query("UPDATE OutboxEvent o SET o.status = :status, o.retryCount = o.retryCount + 1 WHERE o.eventId = :id")
    void incrementRetry(@Param("id") UUID id, @Param("status") OutboxEvent.OutboxStatus status);
}
