package SemanticAnalyzer;
import java.util.List;
import java.util.Scanner;

import LexicalAnalyzer.Token;
import LexicalAnalyzer.TokenType;
import SyntaxAnalyzer.Expr;
import SyntaxAnalyzer.Stmt;
import Utils.RuntimeError;
import LexicalAnalyzer.Lox;

public class Interpreter implements Expr.Visitor<Object>,
        Stmt.Visitor<Void> {
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
        if (expr == null) {
            // Handle null expression case, maybe return a default value or throw a more descriptive error
            return null;
        }

        return expr.accept(this);
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);
        if (expr.operator.getTokenType() == TokenType.OR) {
            if (isTruthy(left)) return left;
        } else {
            if (!isTruthy(left)) return left;
        }
        return evaluate(expr.right);
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);

        System.out.println(stringify(value));
        return null;
    }



    @Override
    public Void visitInputStmt(Stmt.Input inputStmt) {
        for (Token varName : inputStmt.getVariableNames()) {
            System.out.println("Enter value for " + varName.getLexeme() + ": ");
            Scanner scanner = new Scanner(System.in);
            String inputValue = scanner.nextLine();


            // Store the input value in the environment
            environment.define(varName.getLexeme(), inputValue);


            // Optionally, display the result
            System.out.println("Assigned value " + inputValue + " to variable " + varName.getLexeme());
        }

        return null;
    }





    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        // Evaluate the initializer expression
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        // Define the variable in the environment
        environment.define(stmt.name.getLexeme(), value);

        return null;
    }

    @Override
    public Void visitVarDeclaration(Stmt.VarDeclaration stmt) {
        for (Stmt.Var var : stmt.variables) {
            Object value = null;

            if (var.initializer != null) {
                value = evaluate(var.initializer);

                if (var.getType().equals("NUMERO") && !(value instanceof Double)) {
                    throw new RuntimeError(var.name, "Variable " + var.name.getLexeme() + " must be of type NUMERO.");
                } else if (var.getType().equals("TIPIK") && !(value instanceof Float)) {
                    throw new RuntimeError(var.name, "Variable " + var.name.getLexeme() + " must be of type TIPIK.");
                } else if (var.getType().equals("LETRA") && !(value instanceof Character)) {
                    throw new RuntimeError(var.name, "Variable " + var.name.getLexeme() + " must be of type LETRA.");
                } else if (var.getType().equals("TINUOD") && !(value instanceof Boolean)) {
                    throw new RuntimeError(var.name, "Variable " + var.name.getLexeme() + " must be of type TINUOD.");
                }
            }

            environment.define(var.name.getLexeme(), value, var.getType());
        }
        return null;
    }





    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        String type = environment.getType(expr.name.getLexeme());

        if (value == null) {
            environment.assign(expr.name, null);
            return null;
        }

        if (type.equals("NUMERO") && !(value instanceof Double)) {
            throw new RuntimeError(expr.name, "Expected a number for NUMERO variable.");
        }else if(type.equals("TIPIK") && !(value instanceof Float)) {
            System.out.println(value.getClass());
            throw new RuntimeError(expr.name, "Expected a number for TIPIK variable.");
        }else if(type.equals("LETRA") && !(value instanceof Character)) {
            throw new RuntimeError(expr.name, "Expected a character for LETRA variable.");
        }else if(type.equals("TINUOD") && !(value instanceof Boolean)) {
            throw new RuntimeError(expr.name, "Expected a boolean for TINUOD variable.");
        }

        environment.assign(expr.name, value);
        return value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);
        switch (expr.operator.getTokenType()) {
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double)right;
            case NOT:
                return !isTruthy(right);
        }
// Unreachable.
        return null;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {

        return environment.get(expr.name);  // This should now correctly retrieve the value.
    }


    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        switch (expr.operator.getTokenType()) {
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;
            case DIVIDE:
                checkNumberOperands(expr.operator, left, right);
                return (double)left / (double)right;
            case MULTIPLY:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
            case PLUS:
                if (left instanceof Double && right instanceof Double)
                {
                    return (double)left + (double)right;
                }
//                if (left instanceof String && right instanceof String)
//                {
//                    return (String)left + (String)right;
//                }
                throw new RuntimeError(expr.operator,
                        "Operands must be two numbers or two strings.");
            case GREATER_THAN:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;

            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;

            case LESS_THAN:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;

            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;


            case NOT_EQUALS:
                return !isEqual(left, right);


            case EQUALS:
                return isEqual(left, right);

            case CONCAT:return stringify(left) + stringify(right);
            case NEXT_LINE: return stringify(left) + "\n" + stringify(right);
        }
// Unreachable.
        return null;
    }

    @Override
    public Void visitSugodStmt(Stmt.Sugod stmt) {

        executeBlock(stmt.statements, environment);
        return null;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
        return a.equals(b);
    }

    private String stringify(Object object) {
        if (object == null) return "null";
        if (object instanceof Boolean) {
            return (Boolean) object ? "OO" : "DILI";
        }
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return object.toString();
    }

    private void checkNumberOperand(Token operator, Object
            operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator,
                "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator,
                                     Object left, Object right)
    {
        if (left instanceof Double && right instanceof Double)
            return;
        throw new RuntimeError(operator,
                "Operands must be numbers.");
    }

    public void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    public void executeBlock(List<Stmt> statements, Environment blockEnvironment) {
        Environment previous = this.environment;
        try {
            this.environment = blockEnvironment;
            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }


}
