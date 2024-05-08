package edu.upvictoria.fpoo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
// TODO: Validar en el interpreter que el numero de argumentos y valores en la insert clause sean iguales. e.g. insert no valido "INSERT INTO alumnos (nombre, edad) VALUES ('Juan', 20, 'Pedro', 21);"
/**
 * Somthing i need to say:
 * I take order by has order_by (with the underscore)
 * And i make all the sentence in uppercase
 * I use only the doble quotes ("") for the strings not the single quotes ('')
 */
public class App {
    static boolean hadError = false;

    public static void main(String[] args) throws IOException {
        runPrompt();
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null)
                break;
            run(line);
            hadError = false;
        }
    }

    private static void run(String source) {
        // Tokenize
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();

        // Parse
        Parser parser = new Parser(tokens);
        Clause expression = parser.parse();

        // Interpret
        Interpreter interpreter = new Interpreter();
        interpreter.interpret(expression);

        // Stop if there was a syntax error.
        if (hadError)
            return;

        System.out.println(expression);
        // System.out.println(new AstPrinter().print(expression));
    }

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
        hadError = true;
        throw new Error("[line " + line + "] Error" + where + ": " + message);
        // System.err.println(
        // "[line " + line + "] Error" + where + ": " + message);
    }
}
