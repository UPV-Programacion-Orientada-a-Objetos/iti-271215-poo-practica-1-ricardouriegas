package edu.upvictoria.fpoo;

import static edu.upvictoria.fpoo.TokenType.AND;
import static edu.upvictoria.fpoo.TokenType.ASC;
import static edu.upvictoria.fpoo.TokenType.BANG_EQUAL;
import static edu.upvictoria.fpoo.TokenType.COMMA;
import static edu.upvictoria.fpoo.TokenType.CREATE;
import static edu.upvictoria.fpoo.TokenType.DATE;
import static edu.upvictoria.fpoo.TokenType.DESC;
import static edu.upvictoria.fpoo.TokenType.DROP;
import static edu.upvictoria.fpoo.TokenType.EOS;
import static edu.upvictoria.fpoo.TokenType.EQUAL_EQUAL;
import static edu.upvictoria.fpoo.TokenType.FALSE;
import static edu.upvictoria.fpoo.TokenType.FROM;
import static edu.upvictoria.fpoo.TokenType.GREATER;
import static edu.upvictoria.fpoo.TokenType.GREATER_EQUAL;
import static edu.upvictoria.fpoo.TokenType.IDENTIFIER;
import static edu.upvictoria.fpoo.TokenType.LEFT_PAREN;
import static edu.upvictoria.fpoo.TokenType.LESS;
import static edu.upvictoria.fpoo.TokenType.LESS_EQUAL;
import static edu.upvictoria.fpoo.TokenType.LIMIT;
import static edu.upvictoria.fpoo.TokenType.MINUS;
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
import static edu.upvictoria.fpoo.TokenType.SLASH;
import static edu.upvictoria.fpoo.TokenType.STAR;
import static edu.upvictoria.fpoo.TokenType.STRING;
import static edu.upvictoria.fpoo.TokenType.TABLE;
import static edu.upvictoria.fpoo.TokenType.TRUE;
import static edu.upvictoria.fpoo.TokenType.UNIQUE;
import static edu.upvictoria.fpoo.TokenType.USE;
import static edu.upvictoria.fpoo.TokenType.WHERE;

import java.util.ArrayList;
import java.util.List;
// import static java.lang.Integer.parseInt;

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
    public Clause parse() {
        try {
            return program();
        } catch (ParseError error) {
            // The parser promises not to crash or hang on invalid syntax,
            // but it doesn’t promise to return a usable syntax tree
            // if an error is found of course
            synchronize();
            return null;
        }
    }

    // <PROGRAM>::= <SENTENCE> ;
    private Clause program() {
        Clause expression = sentence();
        consume(SEMICOLON, "Expected ; of statement.");
        return expression;
    }
    
    // <SENTENCE>::= <USE_CLAUSE> | <CREATE_CLAUSE> | <DROP_CLAUSE> |
    // <SELECT_CLAUSE> | <INSERT_CLAUSE> | <UPDATE_CLAUSE> | <DELETE_CLAUSE>
    private Clause sentence() {
        if (match(USE))
            return useClause();
        if (match(CREATE))
            return createClause();
        if (match(DROP))
            return dropClause();
        if (match(SELECT))
            return selectClause();

        throw error(peek(), "Expected statement.");
    }

    /**************************************************************************/
    /******************************** USE DATABASE ******************************/
    /**************************************************************************/
    // <USE_CLAUSE>::= USE <PATH>
    // <PATH>::= <STRING>
    private Clause useClause() {
        Token path = consume(STRING, "Expected path to database.");
        return new Clause.UseClause(path.lexeme);
    }

    /**************************************************************************/
    /******************************** CREATE TABLE ******************************/
    /**************************************************************************/

    // <CREATE_CLAUSE>::= CREATE TABLE <TABLE_NAME> OPEN_PAR <COLUMN_DEFINITION> (,
    // <COLUMN_DEFINITION>)* CLOSE_PAR
    // <TABLE_NAME>::= <STRING>
    // <COLUMN_NAME>::= ALPHA (ALPHA | DIGIT)*
    private Clause createClause() {
        consume(TABLE, "Expected keyword TABLE after CREATE.");
        Token name = consume(IDENTIFIER, "Expected table name.");
        consume(LEFT_PAREN, "Expected ( after table name.");

        List<List<String>> columnDefinition = new ArrayList<>();
        while (!check(RIGHT_PAREN)) {
            columnDefinition.add(columnDefinition());
            if (!match(COMMA))
                break;
        }

        consume(RIGHT_PAREN, "Expected ) after column definitions.");

        return new Clause.CreateClause(name.lexeme, columnDefinition);
    }

    // <COLUMN_DEFINITION>::= <COLUMN_NAME> <DATA_TYPE> <CONSTRAINT>*
    private List<String> columnDefinition() {
        Token name = consume(IDENTIFIER, "Expected column name.");
        String type = dataType();

        List<String> constraints = new ArrayList<>();
        constraints.add(name.lexeme);
        constraints.add(type);

        while (true) {
            String constraint = constraint();
            if (constraint == null)
                break;
            constraints.add(constraint);
        }

        return constraints;
    }

    // <DATA_TYPE>::= NUMBER | STRING | DATE
    private String dataType() {
        if (match(NUMBER))
            return "NUMBER";
        if (match(STRING))
            return "STRING";
        if (match(DATE))
            return "DATE";

        throw error(peek(), "Expected data type.");
    }

    // <CONSTRAINT>::= PRIMARY_KEY | NOT_NULL | UNIQUE | NULL
    private String constraint() {
        if (match(PRIMARY_KEY))
            return "PRIMARY KEY";
        if (match(NOT_NULL))
            return "NOT NULL";
        if (match(UNIQUE))
            return "UNIQUE";
        if (match(NULL))
            return "NULL";

        return null;
    }

    /**************************************************************************/
    /******************************** DROP TABLE ********************************/
    /**************************************************************************/
    // <DROP_CLAUSE>::= DROP TABLE <TABLE_NAME>
    // <TABLE_NAME>::= <STRING>
    private Clause dropClause() {
        consume(TABLE, "Expected keyword TABLE after DROP.");
        Token name = consume(IDENTIFIER, "Expected table name.");
        return new Clause.DropClause(name.lexeme);
    }

    /**************************************************************************/
    /******************************** SELECT ************************************/
    /**************************************************************************/
    // <SELECT_CLAUSE>::= SELECT (STAR | <COLUMN_NAME> (, <COLUMN_NAME>)*)
    // <FROM_CLAUSE> <WHERE_CLAUSE>? <ORDER_BY_CLAUSE>? <LIMIT_CLAUSE>?
    //// <FROM_CLAUSE>::= FROM <TABLE_NAME>
    private Clause selectClause() {
        if (match(STAR)) {
            consume(FROM, "Expected keyword FROM after SELECT *.");
            String table_name = consume(IDENTIFIER, "Expected table name.").lexeme;

            Expression where_expression = null;
            List<String> columns_order = null;
            int limit = -1;

            // where clause
            if (match(WHERE)) {
                where_expression = whereClause();
            }

            // order by clause
            if (match(ORDER_BY)) {
                consume(IDENTIFIER, "Expected column name.");
                columns_order = orderBy();
            }

            // limit clause
            if (match(LIMIT)) {
                limit = limitClause();
            }
        }

        // stills the select clause
        List<String> columns = new ArrayList<>();
        columns.add(consume(IDENTIFIER, "Expected column name.").lexeme);
        while (match(COMMA)) {
            columns.add(consume(IDENTIFIER, "Expected column name.").lexeme);
        }

        // from clause
        consume(FROM, "Expected keyword FROM after column names.");
        String table_name = consume(IDENTIFIER, "Expected table name.").lexeme;

        Expression where_expression = null;
        List<String> columns_order = null;
        int limit = -1;

        // where clause
        if (match(WHERE)) {
            where_expression = whereClause();
        }

        // order by clause
        if (match(ORDER_BY)) {
            consume(IDENTIFIER, "Expected column name.");
            columns_order = orderBy();
        }

        // limit clause
        if (match(LIMIT)) {
            limit = limitClause();
        }

        return new Clause.SelectClause(columns, table_name, where_expression, columns_order, limit);
    }

    // <WHERE_CLAUSE>::= WHERE <EXPRESSION>
    private Expression whereClause() {
        return expression();
    }

    
    // <EXPRESSION>::= <EQUALITY_LEFT> (OR <EQUALITY_LEFT>)*
    private Expression expression() {
        Expression left = equalityLeft();
        while (match(OR)) {
            Expression right = equalityLeft();
            left = new Expression.Binary(left, previous(), right);
        }
        return left;
    }

    // <EQUALITY_LEFT>::= <EQUALITY> (AND <EQUALITY>)*
    private Expression equalityLeft() {
        Expression left = equality();
        while (match(AND)) {
            Expression right = equality();
            left = new Expression.Binary(left, previous(), right);
        }
        return left;
    }

    // <EQUALITY>::= <COMPARISION> (( "!=" | "==" ) <COMPARISION>)*
    private Expression equality() {
        Expression left = comparision();
        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Expression right = comparision();
            left = new Expression.Binary(left, previous(), right);
        }
        return left;
    }

    // <COMPARISION>::= <TERM> (( ">" | ">=" | "<" | "<=" ) <TERM>)*
    private Expression comparision() {
        Expression left = term();
        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Expression right = term();
            left = new Expression.Binary(left, previous(), right);
        }
        return left;
    }

    // <TERM>::= <FACTOR> (( "-" | "+" ) <FACTOR>)*
    private Expression term() {
        Expression left = factor();
        while (match(MINUS, PLUS)) {
            Expression right = factor();
            left = new Expression.Binary(left, previous(), right);
        }
        return left;
    }

    // <FACTOR>::= <OPERAND> (( "/" | "*" ) <OPERAND>)*
    private Expression factor() {
        Expression left = operand();
        while (match(SLASH, STAR)) {
            Expression right = operand();
            left = new Expression.Binary(left, previous(), right);
        }
        return left;
    }

    // <OPERAND>::= <NUMBER> | <STRING> | TRUE | FALSE | NULL | NOT_NULL | "("
    // <EXPRESSION> ")"
    private Expression operand() {
        if (match(NUMBER, STRING, TRUE, FALSE, NULL, NOT_NULL)) {
            return new Expression.Literal(previous().literal);
        }
        if (match(LEFT_PAREN)) {
            Expression expression = expression();
            consume(RIGHT_PAREN, "Expected ) after expression.");
            return new Expression.Grouping(expression);
        }
        throw error(peek(), "Expected operand.");
    }

    // <ORDER_BY_CLAUSE>::= ORDER BY <COLUMN_NAME> (, <COLUMN_NAME> )* <ORDER_TYPE>?
    private List<String> orderBy() {
        List<String> columns = new ArrayList<>();
        consume(COMMA, "Missed coma");
        columns.add(consume(IDENTIFIER, "Expected column name.").lexeme);

        while (match(COMMA)) {
            columns.add(consume(IDENTIFIER, "Expected column name.").lexeme);
        }

        if (orderType()) {
            return columns;
        }

        return columns;
    }

    // <ORDER_TYPE>::= ASC | DESC
    private boolean orderType() {
        if (match(ASC, DESC)) {
            return true;
        }
        return false;
    }

    // <LIMIT_CLAUSE>::= LIMIT <DIGIT> -- Se puede agregar keyword: ROWS
    private int limitClause() {
        return (int) (double) consume(NUMBER, "Expected number.").literal;
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

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON)
                return;

            switch (peek().type) {
                case CREATE:
                case DROP:
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
     * @return boolean
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
        if (isAtEnd()){
            return false;
        }
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()){
            current++;
        }
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
