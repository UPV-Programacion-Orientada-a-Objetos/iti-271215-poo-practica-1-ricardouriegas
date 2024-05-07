package edu.upvictoria.fpoo;

enum TokenType {
    
    // Data Types
    INT, VARCHAR, BOOLEAN, DATE, 
    
    // Data Definition (DDL)
    CREATE, DROP, USE, 
    
    // Data Manipulation (DML)
    SELECT, INSERT, UPDATE, DELETE,
    
    // Keywords
    // TODO: ADD NOT_NULL, and SET
    WHERE, FROM, ORDER_BY, LIMIT, VALUES, INTO, AND, OR, NOT, NULL,
    TRUE, FALSE, PRIMARY, KEY, DATABASE, TABLE, ASC, DESC,

    // LITERALS (a literal is a representative of a fixed value)
    // AN IDENTIFIER IS ONLY A STRING THAT REPRESENTS A NAME OF A TABLE, COLUMN, ETC.
    NUMBER, STRING, IDENTIFIER,
    
    // One character tokens
    LEFT_PAREN, RIGHT_PAREN, COMMA, MINUS, PLUS, SLASH, STAR, SEMICOLON, 
    
    // Operators
    BANG_EQUAL, BANG, EQUAL_EQUAL, EQUAL, 
    LESS_EQUAL, LESS, GREATER_EQUAL, GREATER,
    
    // End of Sentence
    EOS
}
