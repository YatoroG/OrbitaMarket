package sys.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sys.model.Account;
import sys.repository.AccountRepository;
import sys.util.exception.AccountAlreadyExistsException;
import sys.util.exception.AccountNotFoundException;
import sys.util.exception.InsufficientBalanceException;
import sys.util.exception.InvalidAmountException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public Account getAccountById(String userId) {
        return accountRepository.findByUserId(userId)
                .orElseThrow(AccountNotFoundException::new);
    }

    public Account addAccount(String userId) {
        if (accountRepository.findByUserId(userId).isPresent()) {
            throw new AccountAlreadyExistsException();
        }

        Account account = Account.builder().userId(userId).balance(0).build();
        return accountRepository.save(account);
    }

    public Integer deposit(String userId, Integer amount) {
        if (amount <= 0L) {
            throw new InvalidAmountException();
        }

        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(AccountNotFoundException::new);

        Integer newBalance = account.getBalance() + amount;
        account.setBalance(newBalance);
        accountRepository.save(account);
        return account.getBalance();
    }

    public Integer getBalance(String userId) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(AccountNotFoundException::new);
        return account.getBalance();
    }

    public void deductBalance(String userId, Integer amount) {
        if (amount <= 0) {
            throw new InvalidAmountException();
        }

        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(AccountNotFoundException::new);

        if (account.getBalance() < amount) {
            throw new InsufficientBalanceException();
        }

        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);
    }
}
