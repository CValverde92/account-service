package com.nttdatabank.account_service.controller;

import com.nttdatabank.account_service.dto.AccountRequest;
import com.nttdatabank.account_service.dto.AccountResponse;
import com.nttdatabank.account_service.dto.TransactionRequest;
import com.nttdatabank.account_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    //Dependency injection
    private final AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AccountResponse> create(
            @Valid @RequestBody AccountRequest request) {
        return accountService.create(request);
    }

    @GetMapping
    public Flux<AccountResponse> getAll() {
        return accountService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<AccountResponse> getById(@PathVariable String id) {
        return accountService.findById(id);
    }

    @PutMapping("/{id}")
    public Mono<AccountResponse> update(
            @PathVariable String id,
            @Valid @RequestBody AccountRequest request) {
        return accountService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return accountService.delete(id);
    }

    /**
     * Banking operations
     * */

    @PostMapping("/{id}/deposit")
    public Mono<AccountResponse> deposit(
            @PathVariable String id,
            @Valid @RequestBody TransactionRequest request) {
        return accountService.deposit(id, request.getAmount());
    }

    @PostMapping("/{id}/withdraw")
    public Mono<AccountResponse> withdraw(
            @PathVariable String id,
            @Valid @RequestBody TransactionRequest request) {
        return accountService.withdraw(id, request.getAmount());
    }

    @GetMapping("/{id}/balance")
    public Mono<BigDecimal> getBalance(@PathVariable String id) {
        return accountService.getBalance(id);
    }
}
