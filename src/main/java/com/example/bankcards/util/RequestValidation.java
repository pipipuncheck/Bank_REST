package com.example.bankcards.util;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.RequestType;
import com.example.bankcards.exception.InvalidDataException;

public class RequestValidation {

    public static void validateCardOperation(Card card, RequestType requestType) {
        switch (requestType) {
            case BLOCK -> {
                if (card.getStatus().equals(CardStatus.BLOCKED) || card.getStatus().equals(CardStatus.EXPIRED)) {
                    throw new InvalidDataException("Card is already blocked or expired");
                }
            }
            case UNBLOCK -> {
                if (card.getStatus().equals(CardStatus.ACTIVE) || card.getStatus().equals(CardStatus.EXPIRED)) {
                    throw new InvalidDataException("Card is already active or expired");
                }
            }
            case DELETE -> {
                if (card.getStatus().equals(CardStatus.EXPIRED)) {
                    throw new InvalidDataException("Cannot delete expired card");
                }
            }
        }
    }
}
