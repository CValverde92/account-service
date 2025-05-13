package com.nttdatabank.account_service.repository;

import com.nttdatabank.account_service.model.Account;
import com.nttdatabank.account_service.model.AccountType;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface AccountRepository extends ReactiveMongoRepository<Account, String> {

    Flux<Account> findByCustomerId(Long customerId);
    Mono<Boolean> existsByCustomerIdAndType(String customerId, AccountType type);
    Mono<Long> countByCustomerIdAndType(String customerId,AccountType type);
    Flux<Account> findByTypeAndBalanceGreaterThanEqual(AccountType type, BigDecimal minBalance);
    Flux<Account> findByTypeAndMonthlyMovementsGreaterThanEqual(AccountType type, Integer movementLimit);

}
