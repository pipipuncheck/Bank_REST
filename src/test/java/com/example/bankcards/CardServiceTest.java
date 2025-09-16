package com.example.bankcards;

import com.example.bankcards.dto.CardQuery;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.exception.InvalidDataException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardOperation;
import com.example.bankcards.util.mapper.CardMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private CardOperation cardOperation;

    @Mock
    private UserDetails userDetails;

    @Mock
    private Pageable pageable;

    @InjectMocks
    private CardService cardService;

    private User user;
    private Card card;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1)
                .username("testuser")
                .build();

        card = Card.builder()
                .id(1)
                .user(user)
                .balance(BigDecimal.valueOf(1000))
                .status(CardStatus.ACTIVE)
                .build();
    }

    @Test
    void getAllCards_Success() {
        Page<Card> cardPage = new PageImpl<>(List.of(card));
        when(cardRepository.findAll(pageable)).thenReturn(cardPage);
        when(cardMapper.toDTO(any(Card.class))).thenReturn(new CardQuery());

        Page<CardQuery> result = cardService.getAllCards(pageable);

        assertNotNull(result);
        verify(cardRepository).findAll(pageable);
    }

    @Test
    void getUserCards_Success() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));

        Page<Card> cardPage = new PageImpl<>(List.of(card));
        when(cardRepository.findByUserId(1, pageable)).thenReturn(cardPage);
        when(cardMapper.toDTO(any(Card.class))).thenReturn(new CardQuery());

        Page<CardQuery> result = cardService.getUserCards(userDetails, pageable);

        assertNotNull(result);
        verify(cardRepository).findByUserId(1, pageable);
    }

    @Test
    void getUserCards_UserNotFound_ThrowsException() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                cardService.getUserCards(userDetails, pageable)
        );
    }

    @Test
    void searchUserCards_Success() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));

        Page<Card> cardPage = new PageImpl<>(List.of(card));
        when(cardRepository.findByUserIdAndNumberContaining(1, "1234", pageable)).thenReturn(cardPage);
        when(cardMapper.toDTO(any(Card.class))).thenReturn(new CardQuery());

        Page<CardQuery> result = cardService.searchUserCards(userDetails, "1234", pageable);

        assertNotNull(result);
        verify(cardRepository).findByUserIdAndNumberContaining(1, "1234", pageable);
    }

    @Test
    void getBalance_Success() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1)).thenReturn(Optional.of(card));

        BigDecimal result = cardService.getBalance(userDetails, 1);

        assertEquals(BigDecimal.valueOf(1000), result);
    }

    @Test
    void getBalance_CardNotBelongsToUser_ThrowsException() {
        User differentUser = User.builder().id(2).build();
        Card differentCard = Card.builder().id(1).user(differentUser).build();

        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1)).thenReturn(Optional.of(differentCard));

        assertThrows(InvalidDataException.class, () ->
                cardService.getBalance(userDetails, 1)
        );
    }

    @Test
    void getById_Success() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1)).thenReturn(Optional.of(card));
        when(cardMapper.toDTO(any(Card.class))).thenReturn(new CardQuery());

        CardQuery result = cardService.getById(userDetails, 1);

        assertNotNull(result);
        verify(cardMapper).toDTO(card);
    }

    @Test
    void createCard_Success() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));

        cardService.createCard(userDetails);

        verify(cardOperation).createCard(user);
    }

    @Test
    void deleteCard_Success() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1)).thenReturn(Optional.of(card));

        cardService.deleteCard(userDetails, 1);

        verify(cardOperation).deleteCard(card);
    }

    @Test
    void activateCard_Success() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1)).thenReturn(Optional.of(card));

        cardService.activateCard(userDetails, 1);

        verify(cardOperation).unblockCard(card);
    }

    @Test
    void blockCard_Success() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1)).thenReturn(Optional.of(card));

        cardService.blockCard(userDetails, 1);

        verify(cardOperation).blockCard(card);
    }
}
