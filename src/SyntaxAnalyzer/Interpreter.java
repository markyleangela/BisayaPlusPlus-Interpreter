package SyntaxAnalyzer;
import java.util.List;
import LexicalAnalyzer.Token;
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
        return expr.accept(this);
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
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

//    @Override
//    public Void visitPrintStmt(Stmt.Print stmt) {
//        Object value = evaluate(stmt.expression);
//
//        System.out.println(stringify(value));
//        return null;
//    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        // Evaluate the expression and split by the concatenator (&)
        Object value = evaluate(stmt.expression);
        String[] parts = stringify(value).split("&");

        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            // Handle escape codes within square braces
            if (part.contains("[")) {
                part = part.replace("[#]", "#"); // Example: Replace [#] with #
            }

            // Handle new line ($)
            if (part.contains("$")) {
                String[] subParts = part.split("\\$");
                for (int i = 0; i < subParts.length; i++) {
                    result.append(subParts[i]);
                    if (i < subParts.length - 1) {
                        result.append("\n"); // Add a new line for each $
                    }
                }
            } else {
                result.append(part);
            }
        }

        // Print the final result
        System.out.println(result.toString());
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
            Object value = evaluate(var.initializer);  // Evaluate the initializer
            environment.define(var.name.getLexeme(), value);  // Define in the current environment
        }
        return null;
    }




    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
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
        System.out.println("Retrieving variable: " + expr.name.getLexeme());
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
                if (left instanceof String && right instanceof String)
                {
                    return (String)left + (String)right;
                }
                throw new RuntimeError(expr.operator,
                        "Operands must be two numbers or two strings.");
            case GREATER_THAN:
                checkNumberOperands(expr.operator, left, right);
                if((double)left > (double)right){
                    return "OO";
                }
                return "DILI";
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                if((double)left >= (double)right){
                    return "OO";
                }
                return "DILI";

            case LESS_THAN:
                checkNumberOperands(expr.operator, left, right);
                if((double)left < (double)right){
                    return "OO";
                }
                return "DILI";

            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                if((double)left <= (double)right){
                    return "OO";
                }
                return "DILI";

            case NOT_EQUALS:
                if(!isEqual(left, right)){
                    return "OO";
                }
                return "DILI";

            case EQUALS:
                if(isEqual(left, right)){
                    return "OO";
                }
                return "DILI";
            case CONCAT:return stringify(left) + stringify(right);
            case NEXT_LINE: return stringify(left) + "\n" + stringify(right);
        }
// Unreachable.
        return null;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
        return a.equals(b);
    }

    private String stringify(Object object) {
        if (object == null) return "nil";
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
