package com.craftinginterpreters;

public class Character {

    /**
     * Very simple implementation preferred over actual `Character.isDigit(c)`
     * because we're keeping our syntax simple.
     *
     * Character.isDigit includes stuff like https://en.wikipedia.org/wiki/Devanagari_numerals
     *
     * @param c
     * @return
     */
    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    public static boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
}
