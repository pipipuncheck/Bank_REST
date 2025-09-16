package com.example.bankcards;

import com.example.bankcards.dto.CardRequestQuery;
import com.example.bankcards.entity.*;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.exception.InvalidDataException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardRequestRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardRequestService;
import com.example.bankcards.util.CardOperation;
import com.example.bankcards.util.mapper.CardRequestMapper;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardRequestServiceTest {

    @Mock
    private CardRequestRepository cardRequestRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardRequestMapper cardRequestMapper;

    @Mock
    private CardOperation cardOperation;

    @Mock
    private UserDetails userDetails;

    @Mock
    private Pageable pageable;

    @InjectMocks
    private CardRequestService cardRequestService;

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
                .status(CardStatus.ACTIVE)
                .build();
    }

    @Test
    void createRequest_CreateRequest_Success() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRequestRepository.existsByUserIdAndCardIdAndRequestTypeAndStatus(any(), any(), any(), any()))
                .thenReturn(false);
        when(cardRequestRepository.save(any())).thenReturn(new CardRequest());

        assertDoesNotThrow(() ->
                cardRequestService.createRequest(userDetails, null, RequestType.CREATE)
        );

        verify(cardRequestRepository).save(any());
    }

    @Test
    void createRequest_BlockRequest_Success() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1)).thenReturn(Optional.of(card));
        when(cardRequestRepository.existsByUserIdAndCardIdAndRequestTypeAndStatus(any(), any(), any(), any()))
                .thenReturn(false);
        when(cardRequestRepository.save(any())).thenReturn(new CardRequest());

        assertDoesNotThrow(() ->
                cardRequestService.createRequest(userDetails, 1, RequestType.BLOCK)
        );

        verify(cardRequestRepository).save(any());
    }

    @Test
    void createRequest_UserNotFound_ThrowsException() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                cardRequestService.createRequest(userDetails, null, RequestType.CREATE)
        );
    }

    @Test
    void createRequest_CardIdRequiredButNull_ThrowsException() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));

        assertThrows(InvalidDataException.class, () ->
                cardRequestService.createRequest(userDetails, null, RequestType.BLOCK)
        );
    }

    @Test
    void createRequest_CardIdProvidedForCreate_ThrowsException() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));

        assertThrows(InvalidDataException.class, () ->
                cardRequestService.createRequest(userDetails, 1, RequestType.CREATE)
        );
    }

    @Test
    void createRequest_CardNotBelongsToUser_ThrowsException() {
        User differentUser = User.builder().id(2).build();
        Card differentCard = Card.builder().id(1).user(differentUser).build();

        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1)).thenReturn(Optional.of(differentCard));

        assertThrows(InvalidDataException.class, () ->
                cardRequestService.createRequest(userDetails, 1, RequestType.BLOCK)
        );
    }

    @Test
    void createRequest_SimilarRequestPending_ThrowsException() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1)).thenReturn(Optional.of(card));
        when(cardRequestRepository.existsByUserIdAndCardIdAndRequestTypeAndStatus(any(), any(), any(), any()))
                .thenReturn(true);

        assertThrows(InvalidDataException.class, () ->
                cardRequestService.createRequest(userDetails, 1, RequestType.BLOCK)
        );
    }

    @Test
    void getPendingRequests_Success() {
        CardRequest cardRequest = new CardRequest();
        Page<CardRequest> page = new PageImpl<>(List.of(cardRequest));
        when(cardRequestRepository.findByStatus(RequestStatus.PENDING, pageable)).thenReturn(page);
        when(cardRequestMapper.toDTO(any(CardRequest.class))).thenReturn(new CardRequestQuery());

        Page<CardRequestQuery> result = cardRequestService.getPendingRequests(pageable);

        assertNotNull(result);
        verify(cardRequestRepository).findByStatus(RequestStatus.PENDING, pageable);
    }

    @Test
    void rejectRequest_Success() {
        CardRequest request = CardRequest.builder()
                .status(RequestStatus.PENDING)
                .build();
        when(cardRequestRepository.findById(1)).thenReturn(Optional.of(request));

        cardRequestService.rejectRequest(1);

        assertEquals(RequestStatus.REJECTED, request.getStatus());
        verify(cardRequestRepository).save(request);
    }

    @Test
    void rejectRequest_RequestNotFound_ThrowsException() {
        when(cardRequestRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                cardRequestService.rejectRequest(1)
        );
    }

    @Test
    void approveRequest_Success() {
        CardRequest request = CardRequest.builder()
                .requestType(RequestType.BLOCK)
                .status(RequestStatus.PENDING)
                .card(card)
                .user(user)
                .build();
        when(cardRequestRepository.findById(1)).thenReturn(Optional.of(request));

        cardRequestService.approveRequest(1);

        assertEquals(RequestStatus.APPROVED, request.getStatus());
        verify(cardOperation).blockCard(card);
        verify(cardRequestRepository).save(request);
    }

    @Test
    void approveRequest_RequestNotFound_ThrowsException() {
        when(cardRequestRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                cardRequestService.approveRequest(1)
        );
    }

    @Test
    void approveRequest_AlreadyProcessed_ThrowsException() {
        CardRequest request = CardRequest.builder()
                .status(RequestStatus.APPROVED)
                .build();
        when(cardRequestRepository.findById(1)).thenReturn(Optional.of(request));

        assertThrows(InvalidDataException.class, () ->
                cardRequestService.approveRequest(1)
        );
    }
}