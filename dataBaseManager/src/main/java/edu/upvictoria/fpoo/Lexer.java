package edu.upvictoria.fpoo;

import static edu.upvictoria.fpoo.TokenType.AND;
import static edu.upvictoria.fpoo.TokenType.ASC;
import static edu.upvictoria.fpoo.TokenType.BANG;
import static edu.upvictoria.fpoo.TokenType.BANG_EQUAL;
import static edu.upvictoria.fpoo.TokenType.BOOLEAN;
import static edu.upvictoria.fpoo.TokenType.COMMA;
import static edu.upvictoria.fpoo.TokenType.CREATE;
import static edu.upvictoria.fpoo.TokenType.DATABASE;
import static edu.upvictoria.fpoo.TokenType.DATE;
import static edu.upvictoria.fpoo.TokenType.DELETE;
import static edu.upvictoria.fpoo.TokenType.DESC;
import static edu.upvictoria.fpoo.TokenType.DROP;
import static edu.upvictoria.fpoo.TokenType.EQUAL;
import static edu.upvictoria.fpoo.TokenType.EQUAL_EQUAL;
import static edu.upvictoria.fpoo.TokenType.FALSE;
import static edu.upvictoria.fpoo.TokenType.FROM;
import static edu.upvictoria.fpoo.TokenType.GREATER;
import static edu.upvictoria.fpoo.TokenType.GREATER_EQUAL;
import static edu.upvictoria.fpoo.TokenType.IDENTIFIER;
import static edu.upvictoria.fpoo.TokenType.INSERT;
import static edu.upvictoria.fpoo.TokenType.INT;
import static edu.upvictoria.fpoo.TokenType.INTO;
import static edu.upvictoria.fpoo.TokenType.LEFT_PAREN;
import static edu.upvictoria.fpoo.TokenType.LESS;
import static edu.upvictoria.fpoo.TokenType.LESS_EQUAL;
import static edu.upvictoria.fpoo.TokenType.LIMIT;
import static edu.upvictoria.fpoo.TokenType.MINUS;
import static edu.upvictoria.fpoo.TokenType.NOT;
import static edu.upvictoria.fpoo.TokenType.NOT_NULL;
import static edu.upvictoria.fpoo.TokenType.NULL;
import static edu.upvictoria.fpoo.TokenType.NUMBER;
import static edu.upvictoria.fpoo.TokenType.OR;
import static edu.upvictoria.fpoo.TokenType.ORDER_BY;
import static edu.upvictoria.fpoo.TokenType.PLUS;
import static edu.upvictoria.fpoo.TokenType.PRIMARY_KEY;
import static edu.upvictoria.fpoo.TokenType.RIGHT_PAREN;
import static edu.upvictoria.fpoo.TokenType.SELECT;
import static edu.upvictoria.fpoo.TokenType.SEMICOLON;
import static edu.upvictoria.fpoo.TokenType.SET;
import static edu.upvictoria.fpoo.TokenType.SLASH;
import static edu.upvictoria.fpoo.TokenType.STAR;
import static edu.upvictoria.fpoo.TokenType.STRING;
import static edu.upvictoria.fpoo.TokenType.TABLE;
import static edu.upvictoria.fpoo.TokenType.TRUE;
import static edu.upvictoria.fpoo.TokenType.UNIQUE;
import static edu.upvictoria.fpoo.TokenType.UPDATE;
import static edu.upvictoria.fpoo.TokenType.USE;
import static edu.upvictoria.fpoo.TokenType.VALUES;
import static edu.upvictoria.fpoo.TokenType.VARCHAR;
import static edu.upvictoria.fpoo.TokenType.WHERE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This object will be in charge of receive the SQL query and tokenize it
 */
public class Lexer {
    // The query to be tokenized
    private final String query;
    
    // List of tokens on the query
    private final List<Token> tokens = new ArrayList<>();

    // Variables to keep track of the current lexeme being scanned
    private int start = 0;
    private int current = 0;
    private int line = 1;

    // Map of SQL keywords
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("INT", INT);
        keywords.put("VARCHAR", VARCHAR);
        keywords.put("BOOLEAN", BOOLEAN);
        keywords.put("DATE", DATE);

        keywords.put("TRUE", TRUE);
        keywords.put("FALSE", FALSE);
        keywords.put("PRIMARY_KEY", PRIMARY_KEY);
        keywords.put("UNIQUE", UNIQUE);
        keywords.put("DATABASE", DATABASE);
        keywords.put("TABLE", TABLE);

        keywords.put("CREATE", CREATE);
        keywords.put("DROP", DROP);
        keywords.put("USE", USE);

        keywords.put("SELECT", SELECT);
        keywords.put("INSERT", INSERT);
        keywords.put("UPDATE", UPDATE);
        keywords.put("DELETE", DELETE);

        keywords.put("WHERE", WHERE);
        keywords.put("FROM", FROM);
        keywords.put("ORDER_BY", ORDER_BY);
        keywords.put("LIMIT", LIMIT);
        keywords.put("VALUES", VALUES);
        keywords.put("INTO", INTO);
        keywords.put("AND", AND);
        keywords.put("OR", OR);
        keywords.put("NOT", NOT);
        keywords.put("NULL", NULL);
        keywords.put("ASC", ASC);
        keywords.put("DESC", DESC);
        keywords.put("NOT_NULL", NOT_NULL);
        keywords.put("SET", SET);
    }

    /**
     * Constructor
     * @param query
     */
    Lexer(String query) {
        this.query = query;
    }

    /**
     * This method will scan to get the next token
     * 
     * @return List<Token> (list of tokens)
     */
    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOS, "", null, line));
        return tokens;
    }

    /**
     * This method will scan the token
     */
    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '-':
                if (match('-')) {
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd())
                        advance();
                } else {
                    addToken(MINUS);
                }
                break;
            case '+':
                addToken(PLUS);
                break;
            case '/':
                addToken(SLASH);
                break;
            case '*': // an asterisk can mean ALL if theres not a number after
                addToken(STAR);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;
            case '\n':
                // move next line
                line++;
                break;
            case '"':
                string();
                break;
            case '\'':
                string();
                break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    ErrorHandler.error(line, "Unexpected character: " + c);
                }
                break;
        }
    }

    /**
     * This method will scan the identifier
     */
    private void identifier() {
        // while the character is alphanumeric
        while (isAlphaNumeric(peek()))
            advance();

        // see if it matches anything in the map
        String text = query.substring(start, current);
        TokenType type = keywords.get(text.toUpperCase());
        
        if (type == null)
            type = IDENTIFIER;

        // the identifiers keywords (e.g. Keyword: AGE) must be saved in 
        // uppercase so the comparisons work downstream 
        addToken(type, text.toUpperCase()); 
    }

    
    /**
     * Just a method to check if the character is a letter
     * 
     * @param c
     * @return
     */
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }
    
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
    /**
     * Just a method to check if the character is a digit
     * 
     * @param c
     * @return
     */
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * This method will scan the identifier
     */
    private void number() {
        while (isDigit(peek()))
            advance();

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            while (isDigit(peek()))
                advance();
        }

        // convert the string to a double
        addToken(NUMBER,
                Double.parseDouble(query.substring(start, current)));
    }

    
    /**
     * string() method will scan the string
     */
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n')
                line++;
            advance();
        }
        
        if (isAtEnd()) {
            ErrorHandler.error(line, "Unterminated string.");
            return;
        }
        
        // The closing ".
        advance();
        
        // Trim the surrounding quotes.
        String value = query.substring(start + 1, current - 1);
        addToken(STRING, value);
    }
    
    /**
     * Method used to peek the next character (if there is one)
     * 
     * @return
     */
    private char peekNext() {
        if (current + 1 >= query.length())
            return '\0';
        return query.charAt(current + 1);
    }
    
    /**
     * Look ahead to see if the current character is a digit
     * 
     * @return
     */
    private char peek() {
        if (isAtEnd())
            return '\0';
        return query.charAt(current);
    }

    /**
     * match the digit
     * 
     * @param expected
     * @return
     */
    private boolean match(char expected) {
        if (isAtEnd())
            return false;
        if (query.charAt(current) != expected)
            return false;

        current++;
        return true;
    }

    /**
     * Consumes the next character
     * 
     * @return
     */
    private char advance() {
        return query.charAt(current++);
    }

    /**
     * Grabs the currect lexeme and adds it to the tokens list
     * 
     * @param type
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    /**
     * Grabs the current lexeme and adds it to the tokens list
     * 
     * @param type
     * @param literal
     */
    private void addToken(TokenType type, Object literal) {
        String text = query.substring(start, current);

        // the strings are not uppercase
        if (type != STRING)
            text = text.toUpperCase();

        tokens.add(new Token(type, text, literal, line));
    }

    /**
     * This method will return true if we are at the end of the query
     */
    private boolean isAtEnd() {
        return current >= query.length();
    }

}
