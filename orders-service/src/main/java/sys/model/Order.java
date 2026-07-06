package sys.model;

import java.util.UUID;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import sys.model.enums.OrderStatus;
import sys.model.enums.ProductType;

@Entity
@Table(name = "orders", schema = "orders_schema") // Явно указана схема
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Order {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false, length = 20)
    private ProductType productType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false)
    private JsonNode payload;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "failure_reason", length = 50)
    private String failureReason;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}
