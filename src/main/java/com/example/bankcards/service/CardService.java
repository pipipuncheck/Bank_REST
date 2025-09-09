package com.example.bankcards.service;

import com.example.bankcards.dto.CardCommand;
import com.example.bankcards.dto.CardQuery;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Status;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.MakeCardNumber;
import com.example.bankcards.util.MakeMaskCardNumber;
import com.example.bankcards.util.mapper.CardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;

    public void createCard(CardCommand command){

        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        LocalDate validityPeriod = LocalDate.now().plusYears(10);

        Card card = Card.builder()
                .user(user)
                .number(MakeCardNumber.generateCardNumber())
                .validityPeriod(validityPeriod)
                .status(Status.ACTIVE)
                .balance(BigDecimal.ZERO)
                .build();

        cardRepository.save(card);
    }

    public List<CardQuery> getAll(String username){
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<CardQuery> cards = cardMapper.toDTO(user.getCards());

        return cards.stream()
                .map(a ->
                {
                    String number = a.getNumber();
                    a.setNumber(MakeMaskCardNumber.maskCardNumber(number));
                    a.setFullName(user.getUsername());
                    return a;
                }
                )
                .collect(Collectors.toList());

    }




}
