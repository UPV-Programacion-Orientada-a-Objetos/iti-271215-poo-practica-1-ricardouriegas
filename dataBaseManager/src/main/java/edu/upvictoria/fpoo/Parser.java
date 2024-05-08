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
    Clause parse() {
        try {
            return program();
        } catch (ParseError error) {
            // The parser promises not to crash or hang on invalid syntax,
            // but it doesn’t promise to return a usable syntax tree
            // if an error is found of course
            return null;
        }
    }

    // <PROGRAM>::= <SENTENCE> ;
    private Clause program() {
        Clause expression = sentence();
        consume(SEMICOLON, "Expected ; of statement.");
        return expression;
    }

    // <SENTENCE>::= <USE_CLAUSE> | <CREATE_CLAUSE> | <DROP_CLAUSE> | <SELECT_CLAUSE> | <INSERT_CLAUSE> | <UPDATE_CLAUSE> | <DELETE_CLAUSE>
    private Clause sentence() {
        if (match(USE))
            return useClause();
        // if (match(CREATE))
        //     return ddlClause();
        // if (match(DROP))
        //     return ddlClause();
        // if (match(SELECT))
        //     return dmlClause();
        // if (match(INSERT))
        //     return dmlClause();
        // if (match(UPDATE))
        //     return dmlClause();
        // if (match(DELETE))
        //     return dmlClause();

        throw error(peek(), "Expected statement.");
    }

    // <USE_CLAUSE>::= USE <PATH>
    // <PATH>::= <STRING>
    private Clause useClause() {
        consume(USE, "Expected keyword USE  at the beginning.");
        Token path = consume(STRING, "Expected path to database.");
        return new Clause.UseClause(path.lexeme);
    }


    /**
     * ************************************************************
     * Down here are the functions that are utilities
     * ************************************************************
     */
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
