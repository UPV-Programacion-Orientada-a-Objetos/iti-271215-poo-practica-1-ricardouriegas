package edu.upvictoria.fpoo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Somthing i need to say:
 * I take order by has order_by (with the underscore) the same with NOT_NULL
 * And i make all the sentence in uppercase
 * This is a First Practice and it doesnt need to validate things so dates are not validated to be treaten as dates
 * I use only the doble quotes ("") for the strings not the single quotes ('')
 */

/**
 * Example of some sentences:
 * CREATE TABLE alumnos (nombre "STRING" PRIMARY_KEY, edad 1 NOT_NULL, fecha_nacimiento "10/10/1001" NULL);
 * INSERT INTO alumnos (nombre, edad, fecha_nacimiento) VALUES ("Juan", 20, "2000-01-01");
 * SELECT nombre, edad FROM alumnos WHERE edad > 18 ORDER_BY edad DESC LIMIT 10;
 * UPDATE alumnos SET edad = 21 WHERE nombre = "Juan";
 * DELETE FROM alumnos WHERE nombre = "Juan";
 * DROP TABLE alumnos;
 */
public class App {
    // static boolean hadError = false;

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
            try {
                run(line);
            } catch (Error e) {
                System.err.println(e.getMessage());
            } catch (Exception e) {
                System.err.println("An error occurred.");
            }

            // hadError = false;
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
        // if (hadError)
        //     return;

        // System.out.println(new AstPrinter().print(expression));
    }
}
