package edu.upvictoria.fpoo;

import java.util.*;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static edu.upvictoria.fpoo.TokenType.*;

public class LexerTest {
    @Test
    public void testScanTokens() {
        Lexer lexer = new Lexer("SELECT * FROM table;");
        List<Token> tokens = lexer.scanTokens();
        // System.out.println(tokens);
        // Expected:
            // SELECT SELECT null, FROM FROM null, TABLE TABLE null, SEMICOLON ; null, EOF  null
        List<Token> expected = new ArrayList<>();
        expected.add(
            new Token(SELECT, "SELECT", null, 1)
        );
        expected.add(
            new Token(FROM, "FROM", null, 1)
        );
        expected.add(
            new Token(TABLE, "TABLE", null, 1)
        );
        expected.add(
            new Token(SEMICOLON, ";", null, 1)
        );
        expected.add(
            new Token(EOS, "", null, 1)
        );


        assertEquals(expected, tokens);
    }

    /**
     * CREATE TABLE Alumnos (
  id INT NOT NULL PRIMARY KEY,
   nombre VARCHAR(20) NOT NULL,
   app VARCHAR(20) NOT NULL,
   apm VARCHAR(20) NOT NULL,
   edad INT NULL
);
     */
    @Test
    public void testScanTokens2() {
        Lexer lexer = new Lexer("CREATE TABLE Alumnos (id INT NOT NULL PRIMARY KEY, nombre VARCHAR(20) NOT NULL, app VARCHAR(20) NOT NULL, apm VARCHAR(20) NOT NULL, edad INT NULL);");
        List<Token> tokens = lexer.scanTokens();
        System.out.println(tokens);
        // Expected:
            // CREATE CREATE null, TABLE TABLE null, IDENTIFIER Alumnos null, LEFT_PAREN ( null, IDENTIFIER id null, INT INT null, NOT NOT null, NULL NULL null, PRIMARY PRIMARY null, KEY KEY null, COMMA , null, IDENTIFIER nombre null, VARCHAR VARCHAR null, LEFT_PAREN ( null, NUMBER 20 20, RIGHT_PAREN ) null, NOT NOT null, NULL NULL null, COMMA , null, IDENTIFIER app null, VARCHAR VARCHAR null, LEFT_PAREN ( null, NUMBER 20 20, RIGHT_PAREN ) null, NOT NOT null, NULL NULL null, COMMA , null, IDENTIFIER apm null, VARCHAR VARCHAR null, LEFT_PAREN ( null, NUMBER 20 20, RIGHT_PAREN ) null, NOT NOT null, NULL NULL null, COMMA , null, IDENTIFIER edad null, INT INT null, NULL NULL null, RIGHT_PAREN ) null, SEMICOLON ; null, EOF  null
        List<Token> expected = List.of(
            new Token(CREATE, "CREATE", null, 1),
            new Token(TABLE, "TABLE", null, 1),
            new Token(IDENTIFIER, "Alumnos", null, 1),
            new Token(LEFT_PAREN, "(", null, 1),
            new Token(IDENTIFIER, "id", null, 1),
            new Token(INT, "INT", null, 1),
            new Token(NOT, "NOT", null, 1),
            new Token(NULL, "NULL", null, 1),
            new Token(PRIMARY, "PRIMARY", null, 1),
            new Token(KEY, "KEY", null, 1),
            new Token(COMMA, ",", null, 1),
            new Token(IDENTIFIER, "nombre", null, 1),
            new Token(VARCHAR, "VARCHAR", null, 1),
            new Token(LEFT_PAREN, "(", null, 1),
            new Token(NUMBER, "20", 20, 1)
        );

        assertEquals(expected, tokens);
    }

}
