package edu.upvictoria.fpoo;

import java.io.File;

/**
 * This class will interpret the abstract syntax tree generated by the parser
 */
public class Interpreter
        implements Clause.Visitor<Void>
// , Expression.Visitor<Object>
{

    void interpret(Clause clause) {
        excecute(clause);
    }

    private void excecute(Clause clause) {
        clause.accept(this);
    }

    // Use clause
    @Override
    public Void useClause(Clause.UseClause clause) {
        // check if the path exists

        File file = new File(clause.path);
        if (file.exists()) {
            System.out.println("The path exists");
        } else {
            System.out.println("The path does not exist");
        }
        return null;
    }
}