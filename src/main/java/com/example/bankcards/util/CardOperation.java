package com.example.bankcards.util;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
@Component
public class CardOperation {

    private final CardRepository cardRepository;

    public void blockCard(Card card) {
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
    }

    public void createCard(User user) {
        LocalDate validityPeriod = LocalDate.now().plusYears(10);

        Card card = Card.builder()
                .user(user)
                .number(MakeCardNumber.generateCardNumber())
                .validityPeriod(validityPeriod)
                .status(CardStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .build();

        cardRepository.save(card);
    }

    public void unblockCard(Card card){
        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
    }

    public void deleteCard(Card card){
        cardRepository.delete(card);
    }
}
