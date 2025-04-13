//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package SyntaxAnalyzer;

import LexicalAnalyzer.Token;

public abstract class Expr {
    public Expr() {
    }

    abstract <R> R accept(Visitor<R> var1);

    static class Variable extends Expr {
        final Token name;

        Variable(Token name) {
            this.name = name;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }
    }

    static class Binary extends Expr {
        final Expr left;
        final Token operator;
        final Expr right;

        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }
    }

    static class Grouping extends Expr {
        final Expr expression;

        Grouping(Expr expression) {
            this.expression = expression;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }
    }

    static class Literal extends Expr {
        final Object value;

        Literal(Object value) {
            this.value = value;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }

    static class Unary extends Expr {
        final Token operator;
        final Expr right;

        Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }

    static class Assign extends Expr {
        final Token name;
        final Expr value;

        Assign(Token name, Expr value) {
            this.name = name;
            this.value = value;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }
    }

    static class Escape extends Expr {
        final Token content;

        Escape(Token content) {
            this.content = content;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitEscapeExpr(this);
        }
    }

    static class NewLine extends Expr {
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitNewLineExpr(this);
        }
    }





    interface Visitor<R> {
        R visitBinaryExpr(Binary var1);

        R visitGroupingExpr(Grouping var1);

        R visitLiteralExpr(Literal var1);

        R visitUnaryExpr(Unary var1);

        R visitVariableExpr(Variable var1);

        R visitAssignExpr(Assign var1);

        R visitEscapeExpr(Escape var1);
        R visitNewLineExpr(NewLine var1);
    }
}
