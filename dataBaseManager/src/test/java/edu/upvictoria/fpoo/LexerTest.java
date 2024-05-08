package edu.upvictoria.fpoo;

import java.util.*;

import org.junit.Test;
import static org.junit.Assert.*;
import static edu.upvictoria.fpoo.TokenType.*;

public class LexerTest {
    /**
     * Test basic SELECT
     */
    @Test
    public void testScanTokens() {
        Lexer lexer = new Lexer("SELECT * from table;");
        List<Token> tokens = lexer.scanTokens();
        // System.out.println(tokens);
        // Expected:
        // SELECT SELECT null, FROM FROM null, TABLE TABLE null, SEMICOLON ; null, EOF
        // null
        List<Token> expected = new ArrayList<>();
        expected.add(
                new Token(SELECT, "SELECT", null, 1));
        expected.add(
                new Token(STAR, "*", null, 1));
        expected.add(
                new Token(FROM, "FROM", null, 1));
        expected.add(
                new Token(TABLE, "TABLE", null, 1));
        expected.add(
                new Token(SEMICOLON, ";", null, 1));
        expected.add(
                new Token(EOS, "", null, 1));

        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).type, tokens.get(i).type);
            assertEquals(expected.get(i).lexeme, tokens.get(i).lexeme);
            assertEquals(expected.get(i).literal, tokens.get(i).literal);
            assertEquals(expected.get(i).line, tokens.get(i).line);
        }
    }

    /**
     * Test CREATE TABLE (with definition columns)
     */
    @Test
    public void testScanTokens2() {
        Lexer lexer = new Lexer(
                "CREATE TABLE Alumnos (id INT NOT NULL PRIMARY_KEY, nombre VARCHAR(20) NOT NULL, app VARCHAR(20) NOT NULL, apm VARCHAR(20) NOT NULL, edad INT NULL);");
        List<Token> tokens = lexer.scanTokens();

        /**
         * CREATE TABLE Alumnos (
         * id INT NOT NULL PRIMARY KEY,
         * nombre VARCHAR(20) NOT NULL,
         * app VARCHAR(20) NOT NULL,
         * apm VARCHAR(20) NOT NULL,
         * edad INT NULL
         * );
         */

        List<Token> expected = new ArrayList<>();
        expected.add(
                new Token(CREATE, "CREATE", null, 1));
        expected.add(
                new Token(TABLE, "TABLE", null, 1));
        expected.add(
                new Token(IDENTIFIER, "ALUMNOS", null, 1));
        expected.add(
                new Token(LEFT_PAREN, "(", null, 1));
        expected.add(
                new Token(IDENTIFIER, "ID", null, 1));
        expected.add(
                new Token(INT, "INT", null, 1));
        expected.add(
                new Token(NOT, "NOT", null, 1));
        expected.add(
                new Token(NULL, "NULL", null, 1));
        expected.add(
                new Token(PRIMARY_KEY, "PRIMARY_KEY", null, 1));
        expected.add(
                new Token(COMMA, ",", null, 1));
        expected.add(
                new Token(IDENTIFIER, "NOMBRE", null, 1));
        expected.add(
                new Token(VARCHAR, "VARCHAR", null, 1));
        expected.add(
                new Token(LEFT_PAREN, "(", null, 1));
        expected.add(
                new Token(NUMBER, "20", 20.0, 1));
        expected.add(
                new Token(RIGHT_PAREN, ")", null, 1));
        expected.add(
                new Token(NOT, "NOT", null, 1));
        expected.add(
                new Token(NULL, "NULL", null, 1));
        expected.add(
                new Token(COMMA, ",", null, 1));
        expected.add(
                new Token(IDENTIFIER, "APP", null, 1));
        expected.add(
                new Token(VARCHAR, "VARCHAR", null, 1));
        expected.add(
                new Token(LEFT_PAREN, "(", null, 1));
        expected.add(
                new Token(NUMBER, "20", 20.0, 1));
        expected.add(
                new Token(RIGHT_PAREN, ")", null, 1));
        expected.add(
                new Token(NOT, "NOT", null, 1));
        expected.add(
                new Token(NULL, "NULL", null, 1));
        expected.add(
                new Token(COMMA, ",", null, 1));
        expected.add(
                new Token(IDENTIFIER, "APM", null, 1));
        expected.add(
                new Token(VARCHAR, "VARCHAR", null, 1));
        expected.add(
                new Token(LEFT_PAREN, "(", null, 1));
        expected.add(
                new Token(NUMBER, "20", 20.0, 1));
        expected.add(
                new Token(RIGHT_PAREN, ")", null, 1));
        expected.add(
                new Token(NOT, "NOT", null, 1));
        expected.add(
                new Token(NULL, "NULL", null, 1));
        expected.add(
                new Token(COMMA, ",", null, 1));
        expected.add(
                new Token(IDENTIFIER, "EDAD", null, 1));
        expected.add(
                new Token(INT, "INT", null, 1));
        expected.add(
                new Token(NULL, "NULL", null, 1));
        expected.add(
                new Token(RIGHT_PAREN, ")", null, 1));
        expected.add(
                new Token(SEMICOLON, ";", null, 1));
        expected.add(
                new Token(EOS, "", null, 1));

        // Assertion
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).type, tokens.get(i).type);
            assertEquals(expected.get(i).lexeme, tokens.get(i).lexeme);
            assertEquals(expected.get(i).literal, tokens.get(i).literal);
            assertEquals(expected.get(i).line, tokens.get(i).line);
        }
    }

    /**
     * Test SELECT (normal query)
     */
    @Test
    public void testScanTokens3() {
        Lexer lexer = new Lexer(
                "SELECT * FROM table WHERE id = 1;");
        List<Token> tokens = lexer.scanTokens();

        List<Token> expected = new ArrayList<>();
        expected.add(
                new Token(SELECT, "SELECT", null, 1));
        expected.add(
                new Token(STAR, "*", null, 1));
        expected.add(
                new Token(FROM, "FROM", null, 1));
        expected.add(
                new Token(TABLE, "TABLE", null, 1));
        expected.add(
                new Token(WHERE, "WHERE", null, 1));
        expected.add(
                new Token(IDENTIFIER, "ID", null, 1));
        expected.add(
                new Token(EQUAL, "=", null, 1));
        expected.add(
                new Token(NUMBER, "1", 1.0, 1));
        expected.add(
                new Token(SEMICOLON, ";", null, 1));
        expected.add(
                new Token(EOS, "", null, 1));

        // Assertion
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).type, tokens.get(i).type);
            assertEquals(expected.get(i).lexeme, tokens.get(i).lexeme);
            assertEquals(expected.get(i).literal, tokens.get(i).literal);
            assertEquals(expected.get(i).line, tokens.get(i).line);
        }
    }

    /**
     * Test SELECT (hard query)
     */
    @Test
    public void testScanTokens4() {
        /**
         * SELECT
         * employee_id,
         * first_name,
         * last_name,
         * department_name
         * FROM
         * employees
         * WHERE
         * salary > 50000
         * AND department_name = "Engineering"
         * ORDER_BY
         * last_name ASC,
         * first_name ASC;
         */
        Lexer lexer = new Lexer(
                "SELECT employee_id, first_name, last_name, department_name FROM employees WHERE salary > 50000 AND department_name = \"Engineering\" ORDER_BY last_name ASC, first_name ASC;");
        List<Token> tokens = lexer.scanTokens();

        List<Token> expected = new ArrayList<>();
        expected.add(
                new Token(SELECT, "SELECT", null, 1));
        expected.add(
                new Token(IDENTIFIER, "EMPLOYEE_ID", null, 1));
        expected.add(
                new Token(COMMA, ",", null, 1));
        expected.add(
                new Token(IDENTIFIER, "FIRST_NAME", null, 1));
        expected.add(
                new Token(COMMA, ",", null, 1));
        expected.add(
                new Token(IDENTIFIER, "LAST_NAME", null, 1));
        expected.add(
                new Token(COMMA, ",", null, 1));
        expected.add(
                new Token(IDENTIFIER, "DEPARTMENT_NAME", null, 1));
        expected.add(
                new Token(FROM, "FROM", null, 1));
        expected.add(
                new Token(IDENTIFIER, "EMPLOYEES", null, 1));
        expected.add(
                new Token(WHERE, "WHERE", null, 1));
        expected.add(
                new Token(IDENTIFIER, "SALARY", null, 1));
        expected.add(
                new Token(GREATER, ">", null, 1));
        expected.add(
                new Token(NUMBER, "50000", 50000.0, 1));
        expected.add(
                new Token(AND, "AND", null, 1));
        expected.add(
                new Token(IDENTIFIER, "DEPARTMENT_NAME", null, 1));
        expected.add(
                new Token(EQUAL, "=", null, 1));
        expected.add(
                new Token(STRING, "\"Engineering\"", "Engineering", 1));
        expected.add(
                new Token(ORDER_BY, "ORDER_BY", null, 1));
        expected.add(
                new Token(IDENTIFIER, "LAST_NAME", null, 1));
        expected.add(
                new Token(ASC, "ASC", null, 1));
        expected.add(
                new Token(COMMA, ",", null, 1));
        expected.add(
                new Token(IDENTIFIER, "FIRST_NAME", null, 1));
        expected.add(
                new Token(ASC, "ASC", null, 1));
        expected.add(
                new Token(SEMICOLON, ";", null, 1));
        expected.add(
                new Token(EOS, "", null, 1));

        // Assertion
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).type, tokens.get(i).type);
            assertEquals(expected.get(i).lexeme, tokens.get(i).lexeme);
            assertEquals(expected.get(i).literal, tokens.get(i).literal);
            assertEquals(expected.get(i).line, tokens.get(i).line);
        }

    }

    @Test
    public void testParse() {
        // use without doble quotes
        String source = "USE /home/username/database\";";
        Lexer lexer = new Lexer(source);
        try {
            lexer.scanTokens();
        } catch (Exception e) {
                // assertEquals("Unterminated string.", e.getMessage());
                // expected error
                
        }
    }

}
