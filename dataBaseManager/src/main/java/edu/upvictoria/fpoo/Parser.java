package edu.upvictoria.fpoo;

import java.util.List;

import static edu.upvictoria.fpoo.TokenType.*;

/**
 * A parser really has two jobs:
 * 
 * 1. Given a valid sequence of tokens, produce a corresponding syntax tree.
 * 
 * 2. Given an invalid sequence of tokens, detect any errors and tell the user
 * about their mistakes.
 */
public class Parser {

    static class ParseError extends RuntimeException {
        // This is a simple sentinel class used to unwind the parser
    }

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // The parser
    Expression parse() {
        try {
            return expression();
        } catch (ParseError error) {
            // The parser promises not to crash or hang on invalid syntax, 
            // but it doesn’t promise to return a usable syntax tree 
            // if an error is found of course
            return null;
        }
    }

    // program := statement* EOF ;
    private Expression expression() {
        return equality();
    }

    // equality := comparison ( ( "!=" | "==" ) comparison )* ;
    private Expression equality() {
        Expression expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expression right = comparison();
            expr = new Expression.Binary(expr, operator, right);
        }

        return expr;
    }

    // comparison := term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    private Expression comparison() {
        Expression expr = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expression right = term();
            expr = new Expression.Binary(expr, operator, right);
        }

        return expr;
    }

    // term := factor ( ( "-" | "+" ) factor )* ;
    private Expression term() {
        Expression expr = factor();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expression right = factor();
            expr = new Expression.Binary(expr, operator, right);
        }

        return expr;
    }

    // factor := unary ( ( "/" | "*" ) unary )* ;
    private Expression factor() {
        Expression expr = unary();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expression right = unary();
            expr = new Expression.Binary(expr, operator, right);
        }

        return expr;
    }

    // unary := ( "!" | "-" ) unary
    // | primary ;
    private Expression unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expression right = unary();
            return new Expression.Unary(operator, right);
        }

        return primary();
    }

    // primary := NUMBER | STRING | "true" | "false" | "nil"
    // | "(" expression ")" ;
    private Expression primary() {
        if (match(NUMBER, STRING))
            return new Expression.Literal(previous().literal);

        if (match(TRUE))
            return new Expression.Literal(true);

        if (match(FALSE))
            return new Expression.Literal(false);

        if (match(NULL))
            return new Expression.Literal(null);

        if (match(LEFT_PAREN)) {
            Expression expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expression.Grouping(expr);
        }

        // If none of the cases in there match,
        // it means we are sitting on a token that can’t start an expression.
        throw error(peek(), "Expect expression.");
    }

    /**
     * This function just consumes the next token if it is of the expected type
     * 
     * @param type
     * @param message
     * @return
     */
    private Token consume(TokenType type, String message) {
        if (check(type))
            return advance();

        throw error(peek(), message);
    }

    /**
     * This function is used to report an error and return a ParseError exception
     * 
     * @param token
     * @param message
     * @return
     */
    private ParseError error(Token token, String message) {
        App.error(token, message);
        return new ParseError();
    }

    /**
     * This function is used to synchronize the parser after an error
     * with this i mean to discard tokens until
     * we’re right at the beginning of the next statement.
     * 
     * @return
     */
    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            // Bc the the program just admit one statement per line
            // we can assume that the next statement is at the beginning
            // of the next line so we dont need to check a semicolon before
            // if (previous().type == SEMICOLON)
            // return;

            switch (peek().type) {
                // A sentence can start with any of these tokens
                case CREATE:
                case DROP:
                case USE:
                case SELECT:
                case INSERT:
                case UPDATE:
                case DELETE:
                    return;
            }

            advance();
        }
    }

    /**
     * This function is used to synchronize the parser after an error
     * with this i mean that it will iterate tokens until it finds a statement
     * 
     * @param types
     * @return
     */
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd())
            return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd())
            current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOS;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }
}
