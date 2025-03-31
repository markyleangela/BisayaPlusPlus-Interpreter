package SyntaxAnalyzer;

import java.util.List;
import LexicalAnalyzer.Token;
import SyntaxAnalyzer.Expr;

abstract class Stmt {
    interface Visitor<R> {
        R visitBlockStmt(Block stmt);
        R visitClassStmt(Class stmt);
        R visitExpressionStmt(Expr stmt);
//        R visitFunctionStmt(Function stmt);
//        R visitIfStmt(If stmt);
        R visitPrintStmt(Print stmt);
//        R visitReturnStmt(Return stmt);
//        R visitVarStmt(Var stmt);
//        R visitWhileStmt(While stmt);
    }
    // Nested Stmt classes here...
    abstract <R> R accept(Visitor<R> visitor);
}

class Print extends Stmt {
    Expr expression;

    public Print(Expr expression) {
        this.expression = expression;
    }
}

class ExpressionStatement extends Stmt {
    Expr expression;

    public ExpressionStatement(Expr expression) {
        this.expression = expression;
    }
}

class VariableDeclaration extends Stmt {
    List<Token> names;
    Token type;
    Expr initializer;

    public VariableDeclaration(List<Token> names, Token type, Expr initializer) {
        this.names = names;
        this.type = type;
        this.initializer = initializer;
    }
}

class Block extends Stmt {
    Block(List<Stmt> statements) {
        this.statements = statements;
    }
    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitBlockStmt(this);
    }
    final List<Stmt> statements;
}