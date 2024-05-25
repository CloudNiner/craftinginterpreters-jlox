package com.craftinginterpreters;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScannerTest {

    @Test
    void testComments() {
        var source =
                """
                        // foo
                        // bar
                        {} // baz
                        """;
        var scanner = new Scanner(source);
        var actual = scanner.scanTokens();
        var expected = List.of(
                new Token(TokenType.LEFT_BRACE, "{", null, 3),
                new Token(TokenType.RIGHT_BRACE, "}", null, 3),
                new Token(TokenType.EOF, "", null, 3)
        );
        assertTokensEqual(expected, actual);
    }

    @Test
    void testOperators() {
        var scanner = new Scanner("<= >= ===><");
        var actual = scanner.scanTokens();
        var expected = List.of(
                new Token(TokenType.LESS_EQUAL, "<=", null, 1),
                new Token(TokenType.GREATER_EQUAL, ">=", null, 1),
                new Token(TokenType.EQUAL_EQUAL, "==", null, 1),
                new Token(TokenType.EQUAL, "=", null, 1),
                new Token(TokenType.GREATER, ">", null, 1),
                new Token(TokenType.LESS, "<", null, 1),
                new Token(TokenType.EOF, "", null, 1)
        );
        assertTokensEqual(expected, actual);
    }

    @Test
    void testCh4_6BookExample() {
        var source =
                """
                        // this is a comment
                        (( )){} // grouping stuff
                        !*+-/=<> <= == // operators
                        """;
        var scanner = new Scanner(source);
        var actual = scanner.scanTokens();
        var expected = List.of(
                new Token(TokenType.LEFT_PAREN, "(", null, 1),
                new Token(TokenType.LEFT_PAREN, "(", null, 1),
                new Token(TokenType.RIGHT_PAREN, ")", null, 1),
                new Token(TokenType.RIGHT_PAREN, ")", null, 1),
                new Token(TokenType.LEFT_BRACE, "{", null, 1),
                new Token(TokenType.RIGHT_BRACE, "}", null, 1),
                new Token(TokenType.BANG, "!", null, 1),
                new Token(TokenType.STAR, "*", null, 1),
                new Token(TokenType.PLUS, "+", null, 1),
                new Token(TokenType.MINUS, "-", null, 1),
                new Token(TokenType.SLASH, "/", null, 1),
                new Token(TokenType.EQUAL, "=", null, 1),
                new Token(TokenType.LESS, "<", null, 1),
                new Token(TokenType.GREATER, ">", null, 1),
                new Token(TokenType.LESS_EQUAL, "<=", null, 1),
                new Token(TokenType.EQUAL_EQUAL, "==", null, 1),
                new Token(TokenType.EOF, "", null, 1)
        );
        assertTokensEqual(expected, actual);
    }

    @Test
    void testStringLexemeOneLine() {
        var scanner = new Scanner("+ \"foo\" ");
        var actual = scanner.scanTokens();
        var expected = List.of(
                new Token(TokenType.PLUS, "+", null, 1),
                new Token(TokenType.STRING, "\"foo\"", "foo", 1),
                new Token(TokenType.EOF, "", null, 1)
        );
        assertTokensEqual(expected, actual);
    }

    @Test
    void testStringLexemeTwoLines() {
        var scanner = new Scanner("+ \"f\noo\" ");
        var actual = scanner.scanTokens();
        var expected = List.of(
                new Token(TokenType.PLUS, "+", null, 1),
                new Token(TokenType.STRING, "\"f\noo\"", "f\noo", 2),
                new Token(TokenType.EOF, "", null, 2)
        );
        assertTokensEqual(expected, actual);
    }

    @Test
    void testStringLexemeEOF() {
        var scanner = new Scanner("+ \"foo ");
        var actual = scanner.scanTokens();
        var expected = List.of(
                new Token(TokenType.PLUS, "+", null, 1),
                new Token(TokenType.EOF, "", null, 1)
        );
        assertTokensEqual(expected, actual);
        assertTrue(Lox.hadError);
    }

    private void assertTokensEqual(List<Token> expected, List<Token> actual) {
        assertArrayEquals(expected.stream().map(Token::toString).toArray(), actual.stream().map(Token::toString).toArray());
    }

}