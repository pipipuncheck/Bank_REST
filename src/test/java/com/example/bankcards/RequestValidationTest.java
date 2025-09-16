package com.example.bankcards;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.RequestType;
import com.example.bankcards.exception.InvalidDataException;
import com.example.bankcards.util.RequestValidation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestValidationTest {

    @Test
    void validateCardOperation_BlockActiveCard_Success() {
        Card card = Card.builder().status(CardStatus.ACTIVE).build();

        assertDoesNotThrow(() ->
                RequestValidation.validateCardOperation(card, RequestType.BLOCK)
        );
    }

    @Test
    void validateCardOperation_BlockBlockedCard_ThrowsException() {
        Card card = Card.builder().status(CardStatus.BLOCKED).build();

        assertThrows(InvalidDataException.class, () ->
                RequestValidation.validateCardOperation(card, RequestType.BLOCK)
        );
    }

    @Test
    void validateCardOperation_UnblockBlockedCard_Success() {
        Card card = Card.builder().status(CardStatus.BLOCKED).build();

        assertDoesNotThrow(() ->
                RequestValidation.validateCardOperation(card, RequestType.UNBLOCK)
        );
    }

    @Test
    void validateCardOperation_UnblockActiveCard_ThrowsException() {
        Card card = Card.builder().status(CardStatus.ACTIVE).build();

        assertThrows(InvalidDataException.class, () ->
                RequestValidation.validateCardOperation(card, RequestType.UNBLOCK)
        );
    }

    @Test
    void validateCardOperation_DeleteExpiredCard_ThrowsException() {
        Card card = Card.builder().status(CardStatus.EXPIRED).build();

        assertThrows(InvalidDataException.class, () ->
                RequestValidation.validateCardOperation(card, RequestType.DELETE)
        );
    }
}





