package com.nttdatabank.account_service.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class TransactionRequest {
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;
}
