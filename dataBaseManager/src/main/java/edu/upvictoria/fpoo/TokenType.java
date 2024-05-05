package edu.upvictoria.fpoo;

enum TokenType {
    
    // LITERALS ()
    NUMBER, STRING, IDENTIFIER,
    
    // Data Types
    INT, VARCHAR, BOOLEAN, DATE, 
    
    // Data Definition (DDL)
    CREATE, DROP, TABLE,
    
    // Data Manipulation (DML)
    SELECT, INSERT, UPDATE, DELETE,
    
    // Clauses
    WHERE, FROM, ORDER_BY, LIMIT, VALUES, INTO, AND, OR, NOT, NULL,
    
    // One character tokens
    LEFT_PAREN, RIGHT_PAREN, COMMA, MINUS, PLUS, SLASH, STAR, SEMICOLON, 
    
    // Operators
    BANG_EQUAL, BANG, EQUAL_EQUAL, EQUAL, 
    LESS_EQUAL, LESS, GREATER_EQUAL, GREATER,
    
    // End of File
    EOF
}
