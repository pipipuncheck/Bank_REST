package com.example.bankcards.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


public final class DataEncryptor {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String KEY = "MySecretKey12345";

    private static final SecretKeySpec SECRET_KEY;

    static {
        byte[] keyBytes = new byte[16];
        byte[] originalKeyBytes = KEY.getBytes();
        System.arraycopy(originalKeyBytes, 0, keyBytes, 0,
                Math.min(originalKeyBytes.length, keyBytes.length));
        SECRET_KEY = new SecretKeySpec(keyBytes, ALGORITHM);
    }

    private DataEncryptor() {
    }


    public static String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при шифровании", e);
        }
    }

    public static String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при дешифровании", e);
        }
    }
}
