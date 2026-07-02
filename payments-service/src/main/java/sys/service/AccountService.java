package sys.service;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sys.model.Account;
import sys.repository.AccountRepository;
import sys.util.exception.AccountAlreadyExistsException;
import sys.util.exception.AccountNotFoundException;
import sys.util.exception.InvalidAmountException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public Account getAccountById(UUID userId) {
        return accountRepository.findByUserId(userId)
                .orElseThrow(AccountNotFoundException::new);
    }

    public Account addAccount(UUID userId) {
        if (accountRepository.findByUserId(userId).isPresent()) {
            throw new AccountAlreadyExistsException();
        }

        Account account = Account.builder().userId(userId).balance(BigDecimal.ZERO).version(0L).build();
        return accountRepository.save(account);
    }

    public BigDecimal deposit(UUID userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0L) {
            throw new InvalidAmountException();
        }

        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(AccountNotFoundException::new);

        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);
        return account.getBalance();
    }

    public BigDecimal getBalance(UUID userId) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(AccountNotFoundException::new);
        return account.getBalance();
    }
}
