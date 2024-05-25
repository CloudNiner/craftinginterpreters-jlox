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

    private void assertTokensEqual(List<Token> expected, List<Token> actual) {
        assertArrayEquals(expected.stream().map((t) -> t.toString()).toArray(), actual.stream().map((t) -> t.toString()).toArray());
    }

}