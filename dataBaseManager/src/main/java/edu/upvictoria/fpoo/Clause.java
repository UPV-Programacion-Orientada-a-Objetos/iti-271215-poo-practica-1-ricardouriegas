package edu.upvictoria.fpoo;

import java.util.List;

abstract class Clause {
    interface Visitor<R> {
        R useClause(UseClause clause);

        R createClause(CreateClause clause);

        R dropClause(DropClause clause);

        R selectClause(SelectClause clause);
        // R insertClause(InsertClause clause);
        // R updateClause(UpdateClause clause);
        // R deleteClause(DeleteClause clause);
    }

    static class UseClause extends Clause {
        UseClause(String path) {
            this.path = path;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.useClause(this);
        }

        final String path;
    }

    static class CreateClause extends Clause {
        CreateClause(String name, List<List<String>> columnsDefinition) {
            this.name = name;
            this.columnsDefinition = columnsDefinition;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.createClause(this);
        }

        final String name;
        final List<List<String>> columnsDefinition;
    }

    static class SelectClause extends Clause {
        SelectClause(List<String> columns, String table_name, Expression where_expression, List<String> columns_order,
                int limit) {
            this.columns = columns;
            this.table_name = table_name;
            this.where_expression = where_expression;
            this.columns_order = columns_order;
            this.limit = limit;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.selectClause(this);
        }

        final List<String> columns;
        final String table_name;
        final Expression where_expression;
        final List<String> columns_order;
        final int limit;
    }

    static class DropClause extends Clause {
        DropClause(String lexeme) {
            this.lexeme = lexeme;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.dropClause(this);
        }

        final String lexeme;
    }

    /**
     * static class InsertClause extends Clause {
     * InsertClause(String path) {
     * this.path = path;
     * }
     * 
     * @Override
     *           <R> R accept(Visitor<R> visitor) {
     *           return visitor.insertClause(this);
     *           }
     * 
     *           final String path;
     *           }
     * 
     *           static class UpdateClause extends Clause {
     *           UpdateClause(String path) {
     *           this.path = path;
     *           }
     * 
     * @Override
     *           <R> R accept(Visitor<R> visitor) {
     *           return visitor.updateClause(this);
     *           }
     * 
     *           final String path;
     *           }
     * 
     *           static class DeleteClause extends Clause {
     *           DeleteClause(String path) {
     *           this.path = path;
     *           }
     * 
     * @Override
     *           <R> R accept(Visitor<R> visitor) {
     *           return visitor.deleteClause(this);
     *           }
     * 
     *           final String path;
     *           }
     */
    abstract <R> R accept(Visitor<R> visitor);
}
