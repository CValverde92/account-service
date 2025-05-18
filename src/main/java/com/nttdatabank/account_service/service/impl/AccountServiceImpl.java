package com.nttdatabank.account_service.service.impl;

import com.nttdatabank.account_service.exception.BusinessException;
import com.nttdatabank.account_service.model.Account;
import com.nttdatabank.account_service.model.AccountType;
import com.nttdatabank.account_service.repository.AccountRepository;
import com.nttdatabank.account_service.service.AccountService;
import com.nttdatabank.model.AccountRequest;
import com.nttdatabank.model.AccountResponse;
import com.nttdatabank.model.AccountUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private static final int MAX_MONTHLY_MOVEMENTS = 3;

    @Override
    public Mono<AccountResponse> create(AccountRequest request) {
        return validateAccountCreation(request)
                .flatMap(validatedRequest -> {
                    Account account = modelMapper.map(validatedRequest, Account.class);
                    applyAccountRules(account);
                    return accountRepository.save(account);
                })
                .map(savedAccount -> modelMapper.map(savedAccount, AccountResponse.class))
                .onErrorMap(ex -> new BusinessException(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @Override
    public Flux<AccountResponse> findAll() {
        return accountRepository.findAll().map(account -> modelMapper.map(account, AccountResponse.class));
    }

    @Override
    public Mono<AccountResponse> findById(String id) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException("Cuenta no encontrada", HttpStatus.NOT_FOUND)))
                .map(account -> modelMapper.map(account, AccountResponse.class));
    }

    @Override
    public Mono<AccountResponse> update(String id, AccountUpdateRequest request) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException("Cuenta no encontrada", HttpStatus.NOT_FOUND)))
                .flatMap(existeAccount -> {
                    modelMapper.map(request, existeAccount);
                    return accountRepository.save(existeAccount);
                })
                .map(updatedAccount -> modelMapper.map(updatedAccount, AccountResponse.class));
    }

    @Override
    public Mono<Void> delete(String id) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException("Cuenta no encontrada", HttpStatus.NOT_FOUND)))
                .flatMap(accountRepository::delete);
    }

    @Override
    public Mono<AccountResponse> deposit(String accountId, BigDecimal amount) {
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new BusinessException("Cuenta no encontrada", HttpStatus.NOT_FOUND)))
                .flatMap(account -> {
                    account.setBalance(account.getBalance().add(amount));
                    if (account.getType() == AccountType.SAVINGS) {
                        account.setMonthlyMovements(account.getMonthlyMovements() + 1);
                    }
                    return accountRepository.save(account);
                }).map(updatedAccount -> modelMapper.map(updatedAccount, AccountResponse.class));
    }

    @Override
    public Mono<AccountResponse> withdraw(String accountId, BigDecimal amount) {
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new BusinessException("Cuenta no encontrada", HttpStatus.NOT_FOUND)))
                .flatMap(account -> {
                    validateWitthdrawal(account, amount);
                    account.setBalance(account.getBalance().subtract(amount));
                    if (account.getType() == AccountType.SAVINGS) {
                        account.setMonthlyMovements(account.getMonthlyMovements() + 1);
                    }
                    return accountRepository.save(account);
                })
                .map(updatedAccount -> modelMapper.map(updatedAccount, AccountResponse.class));
    }

    @Override
    public Mono<BigDecimal> getBalance(String accountId) {
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new BusinessException("Cuenta no encontrada", HttpStatus.NOT_FOUND)))
                .map(Account::getBalance);
    }

    /**
     * Validation
     */

    private Mono<AccountRequest> validateAccountCreation(AccountRequest request) {
        return accountRepository.existsByCustomerIdAndType(request.getCustomerId().toString(),
                        AccountType.valueOf(request.getType().name()))
                .flatMap(existe -> {
                    if (existe && AccountType.valueOf(request.getType().name()) != AccountType.CHECKING) {
                        return Mono.error(new BusinessException("Cliente ya tiene una cuenta de este tipo", HttpStatus.BAD_REQUEST));
                    }
                    return Mono.just(request);
                });
    }

    private void applyAccountRules(Account account) {
        switch (account.getType()) {
            case SAVINGS:
                account.setMonthlyMovements(0);
                account.setMaintenanceFee(BigDecimal.ZERO);
                break;
            case CHECKING:
                account.setMaintenanceFee(new BigDecimal("2.50"));
                break;
            case FIXED_TERM:
                account.setFixedTermWithdrawalDate(LocalDate.now().plusMonths(1));
                account.setMaintenanceFee(BigDecimal.ZERO);
                break;
        }
    }

    private void validateWitthdrawal(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("Saldo insuficiente", HttpStatus.BAD_REQUEST);
        }
        switch (account.getType()) {
            case SAVINGS:
                if (account.getMonthlyMovements() >= MAX_MONTHLY_MOVEMENTS) {
                    throw new BusinessException("Limite de movimientos mensuales excedido", HttpStatus.BAD_REQUEST);
                }
                break;
            case FIXED_TERM:
                if (!LocalDate.now().equals(account.getFixedTermWithdrawalDate())) {
                    throw new BusinessException("Solo puede retirar en la fecha de vencimiento", HttpStatus.BAD_REQUEST);
                }
                break;
        }
    }
}
