package com.example.bankcards;

import com.example.bankcards.util.MakeCardNumber;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MakeCardNumberTest {

    @Test
    void generateCardNumber_ValidFormat() {
        String cardNumber = MakeCardNumber.generateCardNumber();

        assertNotNull(cardNumber);
        assertEquals(16, cardNumber.length());
        assertTrue(cardNumber.matches("\\d{16}"));
    }
}