package sys.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import sys.model.Order;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserId(String userId);
}
