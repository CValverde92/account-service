package com.nttdatabank.account_service.validation;

import com.nttdatabank.account_service.dto.AccountRequest;
import com.nttdatabank.account_service.model.AccountType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class BusinessAcountValidation implements AccountValidation {

    @Override
    public Mono<Void> validate(AccountRequest request) {
        if (request.getType() != AccountType.CHECKING) {
            return Mono.error(new RuntimeException("Cliente solo puede tener cuentas corrientes"));
        }
        return Mono.empty();
    }
}
