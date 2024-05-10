package edu.upvictoria.fpoo;

/**
 * This class will handle the errors of the lexer and parser
 */
public class ErrorHandler {

    /**
     * This method will report an error
     * 
     * @param line
     * @param message
     */
    static void error(int line, String message) {
        report(line, "", message);
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOS) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    /**
     * This method will report an error
     * 
     * @param line
     * @param where
     * @param message
     */
    private static void report(int line, String where, String message) {
        // hadError = true;
        throw new Error("[line " + line + "] Error" + where + ": " + message);
        // System.err.println(
        // "[line " + line + "] Error" + where + ": " + message);
    }
}
