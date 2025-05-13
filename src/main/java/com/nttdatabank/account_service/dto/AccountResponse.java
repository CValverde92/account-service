package com.nttdatabank.account_service.dto;

import com.nttdatabank.account_service.model.AccountType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountResponse {
    private String id;
    private AccountType type;
    private BigDecimal balance;
    private String customerId;
}
