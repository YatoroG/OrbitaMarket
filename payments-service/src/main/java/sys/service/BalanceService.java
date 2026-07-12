package sys.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import sys.model.Account;
import sys.repository.AccountRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceService {
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    public Integer deductWithCasRetries(String userId, Integer amount) {
        int maxRetries = 3;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                accountService.deductBalance(userId, amount);

                return accountRepository.findByUserId(userId)
                        .map(Account::getBalance)
                        .orElse(0);
            } catch (ObjectOptimisticLockingFailureException e) {
                log.warn("Конфликт версий для пользователя {}. Попытка {} из {}", userId, attempt, maxRetries);
                if (attempt == maxRetries) {
                    throw new RuntimeException("Не удалось списать баланс: высокая конкуренция данных", e);
                }
                waitForNextAttempt();
            }
        }
        throw new RuntimeException("Непредвиденный сбой");
    }

    private void waitForNextAttempt() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
