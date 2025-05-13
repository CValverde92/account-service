package com.nttdatabank.account_service.service;

import com.nttdatabank.account_service.dto.AccountRequest;
import com.nttdatabank.account_service.dto.AccountResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface AccountService {
    Mono<AccountResponse> create(AccountRequest request);
    Flux<AccountResponse> findAll();
    Mono<AccountResponse> findById(String id);
    Mono<AccountResponse> update(String id, AccountRequest request);
    Mono<Void> delete(String id);
    Mono<AccountResponse> deposit(String accountId, BigDecimal amount);
    Mono<AccountResponse> withdraw(String accountId, BigDecimal amount);
    Mono<BigDecimal> getBalance(String accountId);

}
