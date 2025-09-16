package com.example.bankcards;

import com.example.bankcards.dto.TransferCommand;
import com.example.bankcards.dto.UserQuery;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.exception.InvalidDataException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private UserService userService;

    private User user;
    private Card fromCard;
    private Card toCard;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1)
                .username("testuser")
                .build();

        fromCard = Card.builder()
                .id(1)
                .user(user)
                .balance(BigDecimal.valueOf(1000))
                .status(CardStatus.ACTIVE)
                .build();

        toCard = Card.builder()
                .id(2)
                .user(user)
                .balance(BigDecimal.valueOf(500))
                .status(CardStatus.ACTIVE)
                .build();
    }

    @Test
    void getAll_Success() {
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDTO(eq(users))).thenReturn(List.of(new UserQuery()));

        List<UserQuery> result = userService.getAll();

        assertNotNull(result);
        verify(userRepository).findAll();
        verify(userMapper).toDTO(users);
    }

    @Test
    void transfer_Success() {
        TransferCommand command = new TransferCommand(BigDecimal.valueOf(100));

        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2)).thenReturn(Optional.of(toCard));

        userService.transfer(userDetails, 1, 2, command);

        assertEquals(BigDecimal.valueOf(900), fromCard.getBalance());
        assertEquals(BigDecimal.valueOf(600), toCard.getBalance());
    }

    @Test
    void transfer_CardNotBelongsToUser_ThrowsException() {
        User differentUser = User.builder().id(2).build();
        Card differentCard = Card.builder().id(1).user(differentUser).build();
        TransferCommand command = new TransferCommand(BigDecimal.valueOf(100));

        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1)).thenReturn(Optional.of(differentCard));
        when(cardRepository.findById(2)).thenReturn(Optional.of(toCard));

        assertThrows(InvalidDataException.class, () ->
                userService.transfer(userDetails, 1, 2, command)
        );
    }

    @Test
    void transfer_CardBlocked_ThrowsException() {
        Card blockedCard = Card.builder()
                .id(1)
                .user(user)
                .status(CardStatus.BLOCKED)
                .build();
        TransferCommand command = new TransferCommand(BigDecimal.valueOf(100));

        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1)).thenReturn(Optional.of(blockedCard));
        when(cardRepository.findById(2)).thenReturn(Optional.of(toCard));

        assertThrows(InvalidDataException.class, () ->
                userService.transfer(userDetails, 1, 2, command)
        );
    }

    @Test
    void transfer_SameCard_ThrowsException() {
        TransferCommand command = new TransferCommand(BigDecimal.valueOf(100));

        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1)).thenReturn(Optional.of(fromCard));

        assertThrows(InvalidDataException.class, () ->
                userService.transfer(userDetails, 1, 1, command)
        );
    }

    @Test
    void transfer_InsufficientFunds_ThrowsException() {
        TransferCommand command = new TransferCommand(BigDecimal.valueOf(2000));

        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2)).thenReturn(Optional.of(toCard));

        assertThrows(InvalidDataException.class, () ->
                userService.transfer(userDetails, 1, 2, command)
        );
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        userService.deleteUser(1);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_UserNotFound_ThrowsException() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                userService.deleteUser(1)
        );
    }
}
