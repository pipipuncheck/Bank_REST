package com.example.bankcards;

import com.example.bankcards.util.MakeMaskCardNumber;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MakeMaskCardNumberTest {

    @Test
    void maskCardNumber_ValidCard_Success() {
        String result = MakeMaskCardNumber.maskCardNumber("1234567812345678");

        assertEquals("**** **** **** 5678", result);
    }

    @Test
    void maskCardNumber_ShortCard_ReturnsStars() {
        String result = MakeMaskCardNumber.maskCardNumber("123");

        assertEquals("****", result);
    }

    @Test
    void maskCardNumber_NullCard_ReturnsStars() {
        String result = MakeMaskCardNumber.maskCardNumber(null);

        assertEquals("****", result);
    }
}
