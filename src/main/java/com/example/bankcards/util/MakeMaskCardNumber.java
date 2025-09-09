package com.example.bankcards.util;

public class MakeMaskCardNumber {

    public static String maskCardNumber(String fullNumber) {
        if (fullNumber == null || fullNumber.length() < 4) {
            return "****";
        }
        String lastFourDigits = fullNumber.substring(fullNumber.length() - 4);
        return "**** **** **** " + lastFourDigits;
    }

}
