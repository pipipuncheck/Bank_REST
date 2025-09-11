package com.example.bankcards.service;

import com.example.bankcards.dto.CardQuery;
import com.example.bankcards.entity.*;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.exception.InvalidDataException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardOperation;
import com.example.bankcards.util.mapper.CardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;
    private final CardOperation cardOperation;

    public Page<CardQuery> getAllCards(Pageable pageable) {
        Page<Card> cardPage = cardRepository.findAll(pageable);
        return cardPage.map(cardMapper::toDTO);
    }

    public Page<CardQuery> getUserCards(UserDetails userDetails, Pageable pageable) {

        User user = userRepository.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Page<Card> cardPage = cardRepository.findByUserId(user.getId(), pageable);
        return cardPage.map(cardMapper::toDTO);
    }

    public Page<CardQuery> searchUserCards(UserDetails userDetails, String searchTerm, Pageable pageable) {

        User user = userRepository.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Page<Card> cardPage = cardRepository.findByUserIdAndNumberContaining(user.getId(), searchTerm, pageable);
        return cardPage.map(cardMapper::toDTO);
    }

    public BigDecimal getBalance(UserDetails userDetails, Integer cardId){

        User user = userRepository.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));

        if (!card.getUser().getId().equals(user.getId())) {
            throw new InvalidDataException("Card does not belong to the user");
        }

        return card.getBalance();
    }
    public CardQuery getById(UserDetails userDetails, Integer cardId){

        User user = userRepository.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));

        if (!card.getUser().getId().equals(user.getId())) {
            throw new InvalidDataException("Card does not belong to the user");
        }

        return cardMapper.toDTO(card);
    }

    public void createCard(UserDetails userDetails){

        User user = userRepository.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        cardOperation.createCard(user);
    }

    public void deleteCard(UserDetails userDetails, Integer cardId){

        User user = userRepository.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));

        if (!card.getUser().getId().equals(user.getId())) {
            throw new InvalidDataException("Card does not belong to the user");
        }

        cardOperation.deleteCard(card);

    }

    public void activateCard(UserDetails userDetails, Integer cardId){

        User user = userRepository.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));

        if (!card.getUser().getId().equals(user.getId())) {
            throw new InvalidDataException("Card does not belong to the user");
        }

        cardOperation.unblockCard(card);

    }

    public void blockCard(UserDetails userDetails, Integer cardId){

        User user = userRepository.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));

        if (!card.getUser().getId().equals(user.getId())) {
            throw new InvalidDataException("Card does not belong to the user");
        }

        cardOperation.blockCard(card);

    }

}
