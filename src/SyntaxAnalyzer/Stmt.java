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

    static class Print extends Stmt {
        final Expr expression;

        Print(Expr expression) {
            this.expression = expression;
        }

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

    interface Visitor<R> {
        R visitBlockStmt(Block var1);

        R visitExpressionStmt(Expression var1);

        R visitPrintStmt(Print var1);

        R visitVarStmt(Var var1);
    }
}
