// Optimized Interpreter.java (no added features, just performance and clarity improvements)

package SemanticAnalyzer;

import java.util.List;
import java.util.Scanner;

import LexicalAnalyzer.Token;
import LexicalAnalyzer.TokenType;
import SyntaxAnalyzer.Expr;
import SyntaxAnalyzer.Stmt;
import Utils.RuntimeError;
import LexicalAnalyzer.Lox;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    private Environment environment = new Environment();

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    private Object evaluate(Expr expr) {
        return expr == null ? null : expr.accept(this);
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        if (stmt.expression instanceof Expr.Variable varExpr && !environment.containsKey(varExpr.name.getLexeme())) {
            throw new RuntimeError(varExpr.name, "Undefined variable: " + varExpr.name.getLexeme());
        }
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);
        return switch (expr.operator.getTokenType()) {
            case OR -> isTruthy(left) ? left : evaluate(expr.right);
            default -> !isTruthy(left) ? left : evaluate(expr.right);
        };
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        while (isTruthy(evaluate(stmt.condition))) execute(stmt.body);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        execute(isTruthy(evaluate(stmt.condition)) ? stmt.thenBranch : stmt.elseBranch);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        System.out.println(stringify(evaluate(stmt.expression)));
        return null;
    }

    @Override
    public Void visitInputStmt(Stmt.Input inputStmt) {
        Scanner scanner = new Scanner(System.in);
        for (Token varName : inputStmt.getVariableNames()) {
            System.out.print("Enter value for " + varName.getLexeme() + ": ");
            String inputValue = scanner.nextLine().replace("\"", "");
            String type = environment.getType(varName.getLexeme());

            if (type == null) throw new RuntimeError(varName, "Variable type is undefined.");

            try {
                switch (type) {
                    case "NUMERO", "TIPIK" -> environment.assign(varName, Double.parseDouble(inputValue));
                    case "TINUOD" -> environment.assign(varName, inputValue.equalsIgnoreCase("OO"));
                    case "LETRA" -> {
                        if (inputValue.length() != 1)
                            throw new RuntimeError(varName, "Expected a single character.");
                        environment.assign(varName, inputValue.charAt(0));
                    }
                    default -> throw new RuntimeError(varName, "Unsupported type: " + type);
                }
            } catch (NumberFormatException e) {
                throw new RuntimeError(varName, "Invalid input for type " + type + ": " + inputValue);
            }
        }
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = stmt.initializer != null ? evaluate(stmt.initializer) : switch (stmt.getType()) {
            case "NUMERO" -> 0.0;
            case "TIPIK" -> 0.0f;
            case "TINUOD" -> false;
            case "LETRA" -> '\0';
            default -> throw new RuntimeError(stmt.name, "Unsupported type: " + stmt.getType());
        };
        environment.define(stmt.name.getLexeme(), value, stmt.getType());
        return null;
    }

    @Override
    public Void visitVarDeclaration(Stmt.VarDeclaration stmt) {
        for (Stmt.Var var : stmt.variables) {
            Object value = var.initializer != null ? evaluate(var.initializer) : null;
            if (value != null && !isTypeCompatible(var.getType(), value)) {
                throw new RuntimeError(var.name, "Variable " + var.name.getLexeme() + " must be of type " + var.getType() + ".");
            }
            environment.define(var.name.getLexeme(), value, var.getType());
        }
        return null;
    }

    private boolean isTypeCompatible(String type, Object value) {
        return switch (type) {
            case "NUMERO" -> value instanceof Double;
            case "TIPIK" -> value instanceof Float;
            case "LETRA" -> value instanceof Character;
            case "TINUOD" -> value instanceof Boolean;
            default -> false;
        };
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        String type = environment.getType(expr.name.getLexeme());
        if (value != null && !isTypeCompatible(type, value)) {
            throw new RuntimeError(expr.name, "Expected " + type + " for assignment.");
        }
        environment.assign(expr.name, value);
        return value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);
        return switch (expr.operator.getTokenType()) {
            case MINUS -> {
                checkNumberOperand(expr.operator, right);
                yield -(double) right;
            }
            case NOT -> !isTruthy(right);
            default -> null;
        };
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return environment.get(expr.name);
    }

    private boolean isTruthy(Object object) {
        return object instanceof Boolean ? (boolean) object : object != null;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        return switch (expr.operator.getTokenType()) {
            case MINUS, DIVIDE, MULTIPLY, PLUS -> applyArithmetic(expr.operator, left, right);
            case GREATER_THAN -> (double) left > (double) right;
            case GREATER_EQUAL -> (double) left >= (double) right;
            case LESS_THAN -> (double) left < (double) right;
            case LESS_EQUAL -> (double) left <= (double) right;
            case EQUALS -> isEqual(left, right);
            case NOT_EQUALS -> !isEqual(left, right);
            case CONCAT -> stringify(left) + stringify(right);
            case NEXT_LINE -> stringify(left) + "\n" + stringify(right);
            default -> null;
        };
    }

    private Object applyArithmetic(Token operator, Object left, Object right) {
        checkNumberOperands(operator, left, right);
        return switch (operator.getTokenType()) {
            case MINUS -> (double) left - (double) right;
            case DIVIDE -> (double) left / (double) right;
            case MULTIPLY -> (double) left * (double) right;
            case PLUS -> (left instanceof Double && right instanceof Double) ?
                (double) left + (double) right :
                stringify(left) + stringify(right);
            default -> null;
        };
    }

    @Override
    public Object visitIncrementExpr(Expr.Increment expr) {
        return applyIncrementDecrement(expr.name, 1);
    }

    @Override
    public Object visitDecrementExpr(Expr.Decrement expr) {
        return applyIncrementDecrement(expr.name, -1);
    }

    private Object applyIncrementDecrement(Token name, int delta) {
        Object value = environment.get(name);
        if (value instanceof Double num) {
            double result = num + delta;
            environment.assign(name, result);
            return result;
        }
        throw new RuntimeError(name, "Only numbers can be incremented or decremented.");
    }

    @Override
    public Void visitSugodStmt(Stmt.Sugod stmt) {
        executeBlock(stmt.statements, environment);
        return null;
    }

    private boolean isEqual(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }

    private String stringify(Object object) {
        if (object == null) return "null";
        if (object instanceof Boolean b) return b ? "OO" : "DILI";
        if (object instanceof Double d) {
            String text = d.toString();
            return text.endsWith(".0") ? text.substring(0, text.length() - 2) : text;
        }
        return object.toString();
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (!(operand instanceof Double)) throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (!(left instanceof Double) || !(right instanceof Double)) {
            throw new RuntimeError(operator, "Operands must be numbers.");
        }
    }

    public void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) execute(statement);
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    private void execute(Stmt stmt) {
        if (stmt != null) stmt.accept(this);
    }

    public void executeBlock(List<Stmt> statements, Environment blockEnvironment) {
        Environment previous = this.environment;
        try {
            this.environment = blockEnvironment;
            for (Stmt statement : statements) execute(statement);
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, environment);
        return null;
    }
}
