package com.nttdatabank.account_service.validation;

import com.nttdatabank.account_service.dto.AccountRequest;
import reactor.core.publisher.Mono;

public interface AccountValidation {
    Mono<Void> validate(AccountRequest request);
}
