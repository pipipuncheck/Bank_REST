package com.example.bankcards.util;

import java.util.Random;

public class MakeCardNumber {

    public static String generateCardNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }
}
