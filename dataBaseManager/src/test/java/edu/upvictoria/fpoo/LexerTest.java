package edu.upvictoria.fpoo;

import java.util.List;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class LexerTest {
    @Test
    public void testScanTokens() {
        Lexer lexer = new Lexer("SELECT * FROM table;");
        List<Token> tokens = lexer.scanTokens();
        System.out.println(tokens);
        // assertEquals(5, tokens.size());
    }
}
