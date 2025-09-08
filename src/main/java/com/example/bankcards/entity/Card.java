package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "cards")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String number;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    private String validityPeriod;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(precision = 15, scale = 2)
    private BigDecimal balance;


}
