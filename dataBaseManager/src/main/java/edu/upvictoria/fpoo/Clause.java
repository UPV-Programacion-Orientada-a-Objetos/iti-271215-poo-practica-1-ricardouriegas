package edu.upvictoria.fpoo;

import java.util.HashMap;
import java.util.List;

abstract class Clause {
    interface Visitor<R> {
        R useClause(UseClause clause);

        R createClause(CreateClause clause);

        R dropClause(DropClause clause);

        R selectClause(SelectClause clause);

        R insertClause(InsertClause clause);
        
        R updateClause(UpdateClause clause);
        
        R deleteClause(DeleteClause clause);
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
    
    static class InsertClause extends Clause {
        InsertClause(String lexeme, HashMap<String, Object> valuesMap) {
            this.lexeme = lexeme;
            this.valuesMap = valuesMap;
            
        }
        
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.insertClause(this);
        }
        
        final String lexeme;
        final HashMap<String, Object> valuesMap;
        
    }
    
    //// new Clause.UpdateClause(table_name.lexeme, column_name.lexeme, value, where_expression);
    static class UpdateClause extends Clause {
        UpdateClause(String table_name, HashMap<String, Object> valuesMap, Expression where_expression) {
            this.table_name = table_name;
            this.valuesMap = valuesMap;
            this.where_expression = where_expression;
        }
        
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.updateClause(this);
        }
        
        final String table_name;
        final HashMap<String, Object> valuesMap;
        final Expression where_expression;
        
    }
    
    //// new Clause.DeleteClause(table_name.lexeme, where_expression);
    static class DeleteClause extends Clause {
        DeleteClause(String table_name, Expression where_expression) {
            this.table_name = table_name;
            this.where_expression = where_expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.deleteClause(this);
        }

        final String table_name;
        final Expression where_expression;
    }

    abstract <R> R accept(Visitor<R> visitor);
}
