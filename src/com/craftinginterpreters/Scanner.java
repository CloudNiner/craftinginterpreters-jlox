package com.craftinginterpreters;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
                    while (peekNext() != '\n' && !isAtEnd()) advance();
                } else if (matchNext('*')) {
                    blockComment();
                } else {
                    addToken(SLASH);
                }
                break;
            // Literals
            case '"': string(); break;
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '0': number(); break;
            // Ignore whitespace
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;
            default:
                if (Character.isAlpha(currentChar)) {
                    identifier();
                } else {
                    Lox.report(line, "", MessageFormat.format("Unexpected character {0}", peekNext()));
                }
        }

    }

    private char advance() {
        // current++ returns the current value of the var, _then_ increments afterward
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
        return peekAhead(0);
    }

    private char peekAhead(int count) {
        var ahead = Math.abs(count);
        if (current + ahead >= source.length()) return '\0';

        return source.charAt(current + ahead);
    }

    /**
     * Scan for end of string and save the string value minus quotes to Token object property
     *
     * We support multiline strings because it's a little easier that not doing so.
     * TODO: Single line strings only!
     */
    private void string() {
        while (peekNext() != '"' && !isAtEnd()) {
            if (peekNext() == '\n') {
                line++;
            }
            advance();
        }
        if (isAtEnd()) {
            Lox.report(line, "", MessageFormat.format("Unterminated string literal starting at line {0}, source char {1}", line, start));
            return;
        }

        advance();  // The closing "

        // Trim the surrounding quotes
        var theString = source.substring(start + 1, current - 1);
        addToken(STRING, theString);
    }

    /**
     * Scan for number literal -- a series of digits optionally followed by
     * a '.' and one or more trailing decimal digits.
     *
     * A lox number literal cannot start or end with a '.' -- this makes things weird
     * if we allow things like calling functions on number literals. For example: `4.sqrt()`
     */
    private void number() {
        while (Character.isDigit(peekNext())) {
            advance();
        }

        if (peekNext() == '.' && Character.isDigit(peekAhead(1))) {
            advance();

            while (Character.isDigit(peekNext())) {
                advance();
            }
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void identifier() {
        while (Character.isAlphaNumeric(peekNext())) {
            advance();
        }

        var lexeme = source.substring(start, current);
        var tokenType = keywords.get(lexeme);
        if (tokenType == null) {
            tokenType = IDENTIFIER;
        }
        addToken(tokenType, lexeme);
    }

    private void blockComment() {
        var nestingCount = 1;

        while (nestingCount > 0 && !isAtEnd()) {
            if (peekNext() == '/' && peekAhead(1) == '*') {
                nestingCount++;
            } else if (peekNext() == '*' && peekAhead(1) == '/') {
                nestingCount--;
            }
            if (peekNext() == '\n') {
                line++;
            }
            advance();
        }

        if (isAtEnd()) {
            Lox.report(line, "", MessageFormat.format("Unterminated block comment starting at source char {0}", start));
            return;
        }

        // The closing `*/`
        advance();
        advance();
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

    private static final HashMap<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
    }
}
