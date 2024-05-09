package edu.upvictoria.fpoo;

public class AstPrinter implements Expression.Visitor<String>, Clause.Visitor<String> {
    String print(Expression expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpression(Expression.Binary expr) {
        return parenthesize(expr.operator.lexeme,
                expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpression(Expression.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpression(Expression.Literal expr) {
        if (expr.value == null)
            return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpression(Expression.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    /**************************************************************************/
    /******************************* CLAUSE *************************************/
    public String print(Clause clause) {
        return clause.accept(this);
    }

    @Override
    public String useClause(Clause.UseClause clause) {
        // Implementation for UseClause printing
        return "USE " + clause.path;
    }

    @Override
    public String createClause(Clause.CreateClause clause) {
        // Implementation for CreateClause printing
        return "CREATE " + clause.name;
    }

    @Override
    public String dropClause(Clause.DropClause clause) {
        // Implementation for DropClause printing
        return "DROP " + clause.lexeme;
    }

    @Override
    public String updateClause(Clause.UpdateClause clause) {
        // Implementation for UpdateClause printing
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE ").append(clause.table_name).append(" ");
        builder.append("SET ");
        for (int i = 0; i < clause.columns.size(); i++) {
            builder.append(clause.columns.get(i)).append(" = ").append(printExpression(clause.value));
            if (i != clause.columns.size() - 1) {
                builder.append(", ");
            }
        }
        builder.append(" ");
        if (clause.where_expression != null) {
            builder.append("WHERE ").append(printExpression(clause.where_expression)).append(" ");
        }
        return builder.toString();
    }

    @Override
    public String deleteClause(Clause.DeleteClause clause) {
        // Implementation for DeleteClause printing
        StringBuilder builder = new StringBuilder();
        builder.append("DELETE FROM ").append(clause.table_name).append(" ");
        if (clause.where_expression != null) {
            builder.append("WHERE ").append(printExpression(clause.where_expression)).append(" ");
        }
        return builder.toString();
    }

    @Override
    public String selectClause(Clause.SelectClause clause) {
        // Implementation for SelectClause printing
        // Use StringBuilder to construct the string representation
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        if (clause.columns.isEmpty()) {
            builder.append("* ");
        } else {
            builder.append(String.join(", ", clause.columns)).append(" ");
        }
        builder.append("FROM ").append(clause.table_name).append(" ");

        if (clause.where_expression != null) {
            builder.append("WHERE ").append(printExpression(clause.where_expression)).append(" ");
        }

        if (clause.columns_order != null && !clause.columns_order.isEmpty()) {
            builder.append("ORDER BY ").append(String.join(", ", clause.columns_order)).append(" ");
        }

        if (clause.limit != -1) {
            builder.append("LIMIT ").append(clause.limit).append(" ");
        }

        return builder.toString();
    }

    @Override
    public String insertClause(Clause.InsertClause clause) {
        // Implementation for InsertClause printing
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO ").append(clause.lexeme).append(" ");
        if (!clause.columns.isEmpty()) {
            builder.append("(").append(String.join(", ", clause.columns)).append(") ");
        }
        builder.append("VALUES (");
        for (int i = 0; i < clause.values.size(); i++) {
            builder.append(printExpression(clause.values.get(i)));
            if (i != clause.values.size() - 1) {
                builder.append(", ");
            }
        }
        builder.append(") ");
        return builder.toString();
    }

    private String printExpression(Token expr) {
        if (expr instanceof Token) {
            return ((Token) expr).lexeme;
        }

        return expr.lexeme;
    }
    

    private String printExpression(Expression expr) {
        return expr.accept(this);
    }

    private String parenthesize(String name, Expression... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expression expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

}
