package com.nttdatabank.account_service.controller;

import com.nttdatabank.account_service.api.AccountsApi;
import com.nttdatabank.account_service.model.AccountRequest;
import com.nttdatabank.account_service.model.AccountResponse;
import com.nttdatabank.account_service.model.AccountUpdateRequest;
import com.nttdatabank.account_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AccountController implements AccountsApi {
    private final AccountService accountService;

    @Override
    public Mono<ResponseEntity<AccountResponse>> createAccount(
            @Valid @RequestBody Mono<AccountRequest> accountRequest,
            final ServerWebExchange exchange) {
        return accountRequest
                .flatMap(accountService::create)
                .map(response -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(response))
                .onErrorResume(e -> {
                    if (e instanceof ValidationException) {
                        return Mono.just(ResponseEntity.badRequest().build());
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }


    @Override
    public Mono<ResponseEntity<Void>> deleteAccount(
            @PathVariable("id") UUID id,
            final ServerWebExchange exchange) {
        return accountService.delete(id.toString())
                .thenReturn(ResponseEntity.noContent().build());
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> getAccountById(
            @PathVariable("id") UUID id,
            final ServerWebExchange exchange) {
        return accountService.findById(id.toString())
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Flux<AccountResponse>>> listAccounts(
            final ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(accountService.findAll()));
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> updateAccount(
            @PathVariable("id") UUID id,
            @Valid @RequestBody Mono<AccountUpdateRequest> accountUpdateRequest,
            final ServerWebExchange exchange) {
        return accountUpdateRequest
                .flatMap(request -> {
                    if (request.getBalance() != null && request.getBalance() < 0) {
                        return Mono.error(new IllegalArgumentException("Balance cannot be negative"));
                    }
                    return accountService.update(id.toString(), request);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(IllegalArgumentException.class, e ->
                        Mono.just(ResponseEntity.badRequest().build()));
    }
}
