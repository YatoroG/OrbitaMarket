package sys.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sys.model.request.TopUpRequest;
import sys.model.response.BalanceResponse;
import sys.service.AccountService;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addAccount(@RequestHeader("X-User-Id") String userId) {
        accountService.addAccount(userId);
    }

    @PostMapping("/top-up")
    @ResponseStatus(HttpStatus.OK)
    public void deposit(@RequestHeader("X-User-Id") String userId, @RequestBody TopUpRequest request) {
        accountService.deposit(userId, request.amount());
    }

    @GetMapping("/balance")
    @ResponseStatus(HttpStatus.OK)
    public BalanceResponse getBalance(@RequestHeader("X-User-Id") String userId) {
        Integer currentBalance = accountService.getBalance(userId);
        return new BalanceResponse(userId, currentBalance, "geocredits");
    }
}
