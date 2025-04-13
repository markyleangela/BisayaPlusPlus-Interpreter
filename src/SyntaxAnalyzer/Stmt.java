//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package SyntaxAnalyzer;

import LexicalAnalyzer.Token;
import java.util.List;

public abstract class Stmt {
    public Stmt() {
    }

    abstract <R> R accept(Visitor<R> var1);

    static class VarDeclaration extends Stmt {
        final List<Stmt.Var> variables;

        public VarDeclaration(List<Stmt.Var> variables) {
            this.variables = variables;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarDeclaration(this);
        }
    }



    static class Expression extends Stmt {
        final Expr expression;

        Expression(Expr expression) {
            this.expression = expression;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }
    }

    static class Block extends Stmt {
        final List<Stmt> statements;

        Block(List<Stmt> statements) {
            this.statements = statements;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }
    }

//    static class Print extends Stmt {
//        final Expr expression;
//
//        Print(Expr expression) {
//            this.expression = expression;
//        }
//
//
//
//
//        <R> R accept(Visitor<R> visitor) {
//            return visitor.visitPrintStmt(this);
//        }
//    }

    static class Print extends Stmt {
        final List<Expr> values;

        Print(List<Expr> values) {
            this.values = values;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }
    }




    static class Var extends Stmt {
        final Token name;
        final Expr initializer;
        final Token type;

        Var(Token name, Expr initializer, Token type) {
            this.name = name;
            this.initializer = initializer;
            this.type = type;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }
    }

    static class If extends Stmt {
        If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }
        final Expr condition;
        final Stmt thenBranch;
        final Stmt elseBranch;
    }

    interface Visitor<R> {
        R visitBlockStmt(Block var1);

        R visitExpressionStmt(Expression var1);

        R visitPrintStmt(Print var1);

        R visitVarStmt(Var var1);
        R visitIfStmt(If stmt);
        R visitVarDeclaration(VarDeclaration stmt);
    }
}
