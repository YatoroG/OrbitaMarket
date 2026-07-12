package sys.kafka.inbox;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "payments_inbox", schema = "payments_schema")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InboxEvent {
    @Id
    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "status")
    private String status;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "new_balance")
    private Integer newBalance;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;
}
