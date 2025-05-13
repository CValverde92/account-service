package com.nttdatabank.account_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Document(collection = "acounts")
public class Account {
    @Id
    private String id;

    @NotNull
    private AccountType type;

    @DecimalMin("0.00")
    private BigDecimal balance;

    @NotBlank
    private String customerId;

    private LocalDate fixedTermWithdrawalDate;
    private Integer monthlyMovements;
    private BigDecimal maintenanceFee;

}
