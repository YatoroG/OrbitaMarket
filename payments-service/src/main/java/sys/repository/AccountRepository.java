package sys.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sys.model.Account;

public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByUserId(String userId);
}
