package com.example.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardRequestQuery {

    private Integer id;
    private String fullName;
    private String cardNumber;
    private String requestType;
    private String status;
}
