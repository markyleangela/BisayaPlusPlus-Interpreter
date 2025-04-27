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

        if (stmt.expression instanceof Expr.Variable) {
            Expr.Variable variableExpr = (Expr.Variable) stmt.expression;
            String varName = variableExpr.name.getLexeme();  // Get the variable name (e.g., 'ctr')

            // Check if the variable exists in the environment
            if (!environment.containsKey(varName)) {
                throw new RuntimeException("Undefined variable: " + varName);
            }
        }

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
    public Void visitWhileStmt(Stmt.While stmt) {
        while(isTruthy(evaluate(stmt.condition))){
            execute(stmt.body);
        }
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

            // Retrieve the existing variable type from the environment
            Object existing = environment.get(varName);

            try {
                // Check the type of the existing variable and parse input accordingly
                if (existing instanceof Double) {
                    // If the variable is a Double, parse the input to Double and assign
                    environment.assign(varName, Double.parseDouble(inputValue));
                    System.out.println("Assigned number " + inputValue + " to variable " + varName.getLexeme());
                } else if (existing instanceof Boolean) {
                    // If the variable is a Boolean, accept "OO" for true and "DILI" for false
                    if (inputValue.equalsIgnoreCase("\"OO\"")) {
                        environment.assign(varName, true);
                        System.out.println("Assigned boolean true (OO) to variable " + varName.getLexeme());
                    } else if (inputValue.equalsIgnoreCase("\"DILI\"")) {
                        environment.assign(varName, false);
                        System.out.println("Assigned boolean false (DILI) to variable " + varName.getLexeme());
                    } else {
                        throw new RuntimeError(varName, "TINUOD should be \"OO\" or \"DILI\"");
                    }
                } else if (existing instanceof Character) {
                    // If the variable is a String, treat the input as a String and assign
                    environment.assign(varName, inputValue);
                    System.out.println("Assigned string \"" + inputValue + "\" to variable " + varName.getLexeme());
                } else if (existing instanceof Integer) {
                    // If the variable is an Integer, parse the input to Integer and assign
                    environment.assign(varName, Integer.parseInt(inputValue));
                    System.out.println("Assigned integer " + inputValue + " to variable " + varName.getLexeme());
                } else {
                    // If the variable type is not recognized, throw an error
                    throw new RuntimeError(varName, "Undefined variable or unsupported type, cannot assign: " + varName.getLexeme());
                }
            } catch (NumberFormatException e) {
                // If parsing fails for numeric types, treat it as a string
                environment.assign(varName, inputValue);
                System.out.println("Assigned string \"" + inputValue + "\" to variable " + varName.getLexeme());
            }
        }
        return null;
    }

//
//    @Override
//    public Void visitInputStmt(Stmt.Input inputStmt) {
//        Scanner sc = new Scanner(System.in);
//        System.out.println("Input values separated by comma: ");
//        String inputLine = sc.nextLine();
//
//        String [] values = inputLine.split(",");
//        if (values.length != inputStmt.getVariableNames().size()) {
//            throw new RuntimeError(inputStmt.getVariableNames().get(0), "Kuwang or sobra imo gibutang nga mga value.");
//        }
//
//        for(int i=0;i <inputStmt.getVariableNames().size(); i++){
//            Token varToken = inputStmt.getVariableNames().get(i);
//            String varName = varToken.getLexeme();
//            String rawValue = values[i].trim();
//            // String dataType = environment.getType(stmt.names.get(i), rawValue);
//
//            Object existing = environment.get(varToken);
//
//            if(existing instanceof Double){
//                environment.assign(varToken,Double.parseDouble(rawValue));
//
//            } else if(existing instanceof Boolean) {
//                if(rawValue.equals("OO")){
//                    environment.assign(varToken,true);
//                }else if(rawValue.equals("DILI")){
//                    environment.assign(varToken,false);
//                }else{
//                    throw new RuntimeError(varToken, "TINUOD should be OO or DILI");
//                }
//            } else if(existing instanceof String){
//                environment.assign(varToken,rawValue);
//            } else if(existing instanceof Integer){
//                environment.assign(varToken,Integer.parseInt(rawValue));
//            }
//            else{
//                throw new RuntimeError(inputStmt.getVariableNames().get(i), "Undefined varible, cannot assign: dawat");
//            }
//        }
//        return null;
//    }






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
                } else if (var.getType().equals("TINUOD") && (!(value instanceof Boolean))) {
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
                if (left instanceof String && right instanceof String)
                {
                    return (String)left + (String)right;
                }

                if (left instanceof Character && right instanceof Character)
                {
                    return (Character)left + (Character) right;
                }
                throw new RuntimeError(expr.operator,
                        "Operands must be two numbers, two strings, or two characters.");
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
