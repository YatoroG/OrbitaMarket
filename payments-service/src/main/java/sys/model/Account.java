package sys.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "accounts", schema = "payments_schema")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Account {
    @Id
    @Column(name = "user_id", unique = true, nullable = false, length = 64)
    private String userId;

    @Column(name = "balance", nullable = false)
    private Integer balance;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}
