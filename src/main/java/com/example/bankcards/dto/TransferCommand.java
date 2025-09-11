package com.example.bankcards.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferCommand {
    @DecimalMin(value = "0.01", message = "The transfer amount must be greater than 0")
    private BigDecimal amount;

}
