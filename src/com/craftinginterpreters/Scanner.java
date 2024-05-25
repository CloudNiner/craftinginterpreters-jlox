package com.craftinginterpreters;

import java.util.ArrayList;
import java.util.List;

import static com.craftinginterpreters.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<Token>();

    // The position in the source string of the first character of the current lexeme being tokenized
    private int start = 0;
    // The position in the source string of the scanner cursor
    private int current = 0;
    // The line in the source string of the `current` cursor
    private int line = 1;

    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        reset();

        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        var currentChar = advance();
        switch (currentChar) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!': addToken(matchNext('=') ? BANG_EQUAL : BANG); break;
            case '=': addToken(matchNext('=') ? EQUAL_EQUAL : EQUAL); break;
            case '>': addToken(matchNext('=') ? GREATER_EQUAL : GREATER); break;
            case '<': addToken(matchNext('=') ? LESS_EQUAL : LESS); break;
            case '/':
                // A comment: `//` -- ignore characters until end of line
                if (matchNext('/')) {
                    while(peekNext() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(SLASH);
                }
                break;
            // Ignore whitespace
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;
        }

    }

    private char advance() {
        // current++ returns the current value of the var, _then_ increments afterwards
        return source.charAt(current++);
    }

    /**
     * Look ahead one character, return true if it matches expected else false
     * @param expected
     * @return
     */
    private boolean matchNext(char expected) {

        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private char peekNext() {
        if (isAtEnd()) return '\0';

        return source.charAt(current);
    }

    private void addToken(TokenType tokenType) {
        addToken(tokenType, null);
    }

    private void addToken(TokenType tokenType, Object literal) {
        var text = source.substring(start, current);
        tokens.add(new Token(tokenType, text, literal, line));
    }

    public void reset() {
        tokens.clear();
        start = 0;
        current = 0;
        line = 1;
    }
}
