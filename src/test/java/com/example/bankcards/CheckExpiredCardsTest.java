package com.example.bankcards;


import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CheckExpiredCards;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckExpiredCardsTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CheckExpiredCards checkExpiredCards;

    @Test
    void checkExpiredCards_ExpiredCardsFound_UpdatesStatus() {
        Card expiredCard = Card.builder()
                .status(CardStatus.ACTIVE)
                .validityPeriod(LocalDate.now().minusDays(1))
                .build();

        when(cardRepository.findByValidityPeriodBeforeAndStatusNot(any(), any()))
                .thenReturn(List.of(expiredCard));

        checkExpiredCards.checkExpiredCards();

        assertEquals(CardStatus.EXPIRED, expiredCard.getStatus());
        verify(cardRepository).save(expiredCard);
    }

    @Test
    void checkExpiredCards_NoExpiredCards_NoUpdates() {
        when(cardRepository.findByValidityPeriodBeforeAndStatusNot(any(), any()))
                .thenReturn(List.of());

        checkExpiredCards.checkExpiredCards();

        verify(cardRepository, never()).save(any());
    }
}
