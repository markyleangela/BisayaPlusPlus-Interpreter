//package SyntaxAnalyzer;
//
//import LexicalAnalyzer.Token;
//import LexicalAnalyzer.TokenType;
//import SyntaxAnalyzer.Expr;
//
//public class AstPrinter implements Expr.Visitor<String>, Stmt.Visitor<String> {
//    public String print(Expr expr) {
//        return expr.accept(this);
//    }
//
//    public String print(Stmt stmt) {
//        return stmt.accept(this);
//    }
//
//    @Override
//    public String visitBinaryExpr(Expr.Binary expr) {
//        return parenthesize(expr.operator.getLexeme(), expr.left, expr.right);
//    }
//
//    @Override
//    public String visitGroupingExpr(Expr.Grouping expr) {
//        return parenthesize("group", expr.expression);
//    }
//
//    @Override
//    public String visitLiteralExpr(Expr.Literal expr) {
//        if (expr.value == null) return "nil";
//        return expr.value.toString();
//    }
//
//    @Override
//    public String visitUnaryExpr(Expr.Unary expr) {
//        return parenthesize(expr.operator.getLexeme(), expr.right);
//    }
//
//    @Override
//    public String visitVariableExpr(Expr.Variable expr) {
//        return expr.name.getLexeme();
//    }
//
//    @Override
//    public String visitAssignExpr(Expr.Assign expr) {
//        return parenthesize("=", expr.name, expr.value);
//    }
//
//    @Override
//    public String visitExpressionStmt(Stmt.Expression stmt) {
//        return parenthesize(";", stmt.expression);
//    }
//
//    @Override
//    public String visitPrintStmt(Stmt.Print stmt) {
//        return parenthesize("print", stmt.expression);
//    }
//
//    @Override
//    public String visitVarStmt(Stmt.Var stmt) {
//        return parenthesize("var", stmt.name, stmt.initializer);
//    }
//
//    @Override
//    public String visitBlockStmt(Stmt.Block stmt) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("(block");
//        for (Stmt statement : stmt.statements) {
//            builder.append(" ").append(statement.accept(this));
//        }
//        builder.append(")");
//        return builder.toString();
//    }
//
//    @Override
//    public String visitIfStmt(Stmt.If stmt) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("(if ").append(stmt.condition.accept(this));
//        builder.append(" ").append(stmt.thenBranch.accept(this));
//        if (stmt.elseBranch != null) {
//            builder.append(" else ").append(stmt.elseBranch.accept(this));
//        }
//        builder.append(")");
//        return builder.toString();
//    }
//
//    @Override
//    public String visitVarDeclaration(Stmt.VarDeclaration stmt) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("(varDeclaration");
//        for (Stmt.Var var : stmt.variables) {
//            builder.append(" ");
//            builder.append(parenthesize("var", var.name, var.initializer));
//        }
//        builder.append(")");
//        return builder.toString();
//    }
//
//
//    private String parenthesize(String name, Expr... exprs) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("(").append(name);
//        for (Expr expr : exprs) {
//            builder.append(" ");
//            builder.append(expr.accept(this));
//        }
//        builder.append(")");
//        return builder.toString();
//    }
//
//    private String parenthesize(String name, Token token, Expr expr) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("(").append(name);
//        builder.append(" ").append(token.getLexeme());
//        builder.append(" ").append(expr.accept(this));
//        builder.append(")");
//        return builder.toString();
//    }
//}