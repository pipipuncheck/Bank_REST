package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardQuery {

    private Integer id;
    private String number;
    private String fullName;
    private String validityPeriod;
    private CardStatus status;
    private BigDecimal balance;
}
