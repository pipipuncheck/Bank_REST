package com.example.bankcards.util;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.repository.CardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CheckExpiredCards {

    private final CardRepository cardRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void checkExpiredCards() {
        List<Card> expiredCards = cardRepository.findByValidityPeriodBeforeAndStatusNot(LocalDate.now(), CardStatus.EXPIRED);

        expiredCards.forEach(card -> {
            card.setStatus(CardStatus.EXPIRED);
            cardRepository.save(card);
        });
    }
}
