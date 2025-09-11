package com.example.bankcards.service;

import com.example.bankcards.dto.TransferCommand;
import com.example.bankcards.dto.UserQuery;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.exception.InvalidDataException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.mapper.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final UserMapper userMapper;

    public List<UserQuery> getAll(){

        return userMapper.toDTO(userRepository.findAll());

    }

    @Transactional
    public void transfer(UserDetails userDetails, Integer fromCardId, Integer toCardId, TransferCommand command) {

        User user = userRepository.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Card fromCard = cardRepository.findById(fromCardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));
        Card toCard = cardRepository.findById(toCardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));

        if (!fromCard.getUser().getId().equals(user.getId()) ||
                !toCard.getUser().getId().equals(user.getId())) {
            throw new InvalidDataException("Card does not belong to the user");
        }

        if (fromCard.getStatus().equals(CardStatus.BLOCKED)
        || toCard.getStatus().equals(CardStatus.BLOCKED)) {
            throw new InvalidDataException("One of the cards is blocked");
        }

        if (fromCard.getStatus().equals(CardStatus.EXPIRED)
                || toCard.getStatus().equals(CardStatus.EXPIRED)) {
            throw new InvalidDataException("One of the cards is expired");
        }

        if (fromCardId.equals(toCardId)) {
            throw new InvalidDataException("Cannot transfer to the same card");
        }

        if (fromCard.getBalance().compareTo(command.getAmount()) < 0) {
            throw new InvalidDataException("Insufficient funds");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(command.getAmount()));
        toCard.setBalance(toCard.getBalance().add(command.getAmount()));

    }

    public void deleteUser(Integer userId){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        userRepository.delete(user);

    }


}
