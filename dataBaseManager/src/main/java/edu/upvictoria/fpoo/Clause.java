package edu.upvictoria.fpoo;


abstract class Clause {
    interface Visitor<R> {
    R useClause(UseClause clause);
    // R createClause(CreateClause clause);
    // R dropClause(DropClause clause);
    // R selectClause(SelectClause clause);
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
/**
    static class CreateClause extends Clause {
        CreateClause(Token token) {
            this.token = token;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.createClause(this);
        }

        final Token token;
    }

    static class DropClause extends Clause {
        DropClause(String path) {
            this.path = path;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.dropClause(this);
        }

        final String path;
    }

    static class SelectClause extends Clause {
        SelectClause(String path) {
            this.path = path;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.selectClause(this);
        }

        final String path;
    }

    static class InsertClause extends Clause {
        InsertClause(String path) {
            this.path = path;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.insertClause(this);
        }

        final String path;
    }

    static class UpdateClause extends Clause {
        UpdateClause(String path) {
            this.path = path;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.updateClause(this);
        }

        final String path;
    }

    static class DeleteClause extends Clause {
        DeleteClause(String path) {
            this.path = path;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.deleteClause(this);
        }

        final String path;
    }
*/
    abstract <R> R accept(Visitor<R> visitor);
}
