package com.example.bankcards.service;

import com.example.bankcards.dto.CardRequestQuery;
import com.example.bankcards.entity.*;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.exception.InvalidDataException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardRequestRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardOperation;
import com.example.bankcards.util.RequestValidation;
import com.example.bankcards.util.mapper.CardRequestMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CardRequestService {

    private final CardRequestRepository cardRequestRepository;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardRequestMapper cardRequestMapper;
    private final CardOperation cardOperation;

    @Transactional
    public void createRequest(UserDetails userDetails, Integer cardId, RequestType requestType) {
        User user = userRepository.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (requestType != RequestType.CREATE && cardId == null) {
            throw new InvalidDataException("Card ID is required for this operation");
        }
        if (requestType == RequestType.CREATE && cardId != null) {
            throw new InvalidDataException("Card ID should not be provided for create request");
        }

        Card card = null;
        if (cardId != null) {
            card = cardRepository.findById(cardId)
                    .orElseThrow(() -> new EntityNotFoundException("Card not found"));

            if (!card.getUser().getId().equals(user.getId())) {
                throw new InvalidDataException("Card does not belong to the user");
            }

            RequestValidation.validateCardOperation(card, requestType);
        }

        if (cardRequestRepository.existsByUserIdAndCardIdAndRequestTypeAndStatus(
                user.getId(), cardId, requestType, RequestStatus.PENDING)) {
            throw new InvalidDataException("Similar request already pending");
        }

        CardRequest request = CardRequest.builder()
                .user(user)
                .card(card)
                .requestType(requestType)
                .status(RequestStatus.PENDING)
                .build();

        cardRequestRepository.save(request);
    }

    public Page<CardRequestQuery> getPendingRequests(Pageable pageable) {

        Page<CardRequest> cardRequestPage= cardRequestRepository.findByStatus(RequestStatus.PENDING, pageable);
        return cardRequestPage.map(cardRequestMapper::toDTO);
    }

    public void rejectRequest(Integer requestId) {

        CardRequest request = cardRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        request.setStatus(RequestStatus.REJECTED);
        cardRequestRepository.save(request);
    }

    @Transactional
    public void approveRequest(Integer requestId) {

        CardRequest request = cardRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        if(request.getStatus().equals(RequestStatus.REJECTED)
        || request.getStatus().equals(RequestStatus.APPROVED)){
            throw new InvalidDataException("Request has already been processed");
        }

        switch (request.getRequestType()) {
            case BLOCK -> cardOperation.blockCard(request.getCard());
            case UNBLOCK -> cardOperation.unblockCard(request.getCard());
            case DELETE -> cardOperation.deleteCard(request.getCard());
            case CREATE -> cardOperation.createCard(request.getUser());
        }

        request.setStatus(RequestStatus.APPROVED);
        cardRequestRepository.save(request);
    }

}
