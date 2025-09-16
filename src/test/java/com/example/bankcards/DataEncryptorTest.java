package com.example.bankcards;

import com.example.bankcards.util.DataEncryptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class DataEncryptorTest {

    @Test
    void encryptDecrypt_Success() {
        String original = "test1234";

        String encrypted = DataEncryptor.encrypt(original);
        String decrypted = DataEncryptor.decrypt(encrypted);

        assertEquals(original, decrypted);
        assertNotEquals(original, encrypted);
    }

    @Test
    void encryptDecrypt_EmptyString_Success() {
        String original = "";

        String encrypted = DataEncryptor.encrypt(original);
        String decrypted = DataEncryptor.decrypt(encrypted);

        assertEquals(original, decrypted);
    }
}
