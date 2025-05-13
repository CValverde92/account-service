package com.nttdatabank.account_service.validation;

import com.nttdatabank.account_service.dto.AccountRequest;
import com.nttdatabank.account_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PersonalAccountValidation implements AccountValidation{

    private final AccountRepository accountRepository;

    @Override
    public Mono<Void> validate(AccountRequest request) {
        return accountRepository.existsByCustomerIdAndType(request.getCustomerId(), request.getType())
                .flatMap(existe -> existe ? Mono.error(new RuntimeException("Cliente ya tiene cuenta de este tipo")) : Mono.empty());

    }
}
