package com.bookmanagement.bookmanagementapp.util.validation;

public final class IsbnUtils {

    private IsbnUtils() {
    }

    public static String normalize(String isbn) {
        return isbn == null ? null : isbn.replace("-", "").replace(" ", "").trim().toUpperCase();
    }

    public static boolean isValid(String rawIsbn) {
        String isbn = normalize(rawIsbn);
        if (isbn == null) {
            return false;
        }
        if (isbn.length() == 10) {
            return isValidIsbn10(isbn);
        }
        if (isbn.length() == 13) {
            return isValidIsbn13(isbn);
        }
        return false;
    }

    private static boolean isValidIsbn10(String isbn) {
        int sum = 0;
        for (int index = 0; index < 10; index++) {
            char current = isbn.charAt(index);
            int value;
            if (index == 9 && current == 'X') {
                value = 10;
            } else if (Character.isDigit(current)) {
                value = Character.getNumericValue(current);
            } else {
                return false;
            }
            sum += value * (10 - index);
        }
        return sum % 11 == 0;
    }

    private static boolean isValidIsbn13(String isbn) {
        if (!isbn.chars().allMatch(Character::isDigit)) {
            return false;
        }
        int sum = 0;
        for (int index = 0; index < 12; index++) {
            int digit = Character.getNumericValue(isbn.charAt(index));
            sum += digit * (index % 2 == 0 ? 1 : 3);
        }
        int checkDigit = (10 - (sum % 10)) % 10;
        return checkDigit == Character.getNumericValue(isbn.charAt(12));
    }
}
