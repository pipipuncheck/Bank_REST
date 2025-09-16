package com.example.bankcards;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardOperation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardOperationTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardOperation cardOperation;

    @Test
    void blockCard_Success() {
        Card card = Card.builder()
                .status(CardStatus.ACTIVE)
                .build();

        cardOperation.blockCard(card);

        assertEquals(CardStatus.BLOCKED, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void createCard_Success() {
        User user = User.builder().id(1).build();

        cardOperation.createCard(user);

        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void unblockCard_Success() {
        Card card = Card.builder()
                .status(CardStatus.BLOCKED)
                .build();

        cardOperation.unblockCard(card);

        assertEquals(CardStatus.ACTIVE, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void deleteCard_Success() {
        Card card = new Card();

        cardOperation.deleteCard(card);

        verify(cardRepository).delete(card);
    }
}
