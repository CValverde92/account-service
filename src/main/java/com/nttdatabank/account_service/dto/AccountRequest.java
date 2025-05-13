package com.nttdatabank.account_service.dto;

import com.nttdatabank.account_service.model.AccountType;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class AccountRequest {

    @NotNull
    private AccountType type;

    @DecimalMin("0.00")
    private BigDecimal initialBalance;

    @NotBlank
    private String customerId;
}
