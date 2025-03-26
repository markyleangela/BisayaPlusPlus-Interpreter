import java.util.List;

// ASTNode.java
public abstract class ASTNode {
    @Override
    public abstract String toString();
}

// VariableDeclaration.java
class VariableDeclaration extends ASTNode {
    public final String type;
    public final String name;
    public final ASTNode value;

    public VariableDeclaration(String type, String name, ASTNode value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

//    @Override
//    public String toString() {
//        return "VariableDeclaration{" +
//                "type='" + type + '\'' +
//                ", name='" + name + '\'' +
//                ", value=" + value +
//                '}';
//    }

    @Override
    public String toString() {
        return name + " = " + value;
    }
}

// VariableAssignment.java
class VariableAssignment extends ASTNode {
    public final String name;
    public final ASTNode value;

    public VariableAssignment(String name, ASTNode value) {
        this.name = name;
        this.value = value;
    }

//    @Override
//    public String toString() {
//        return "VariableAssignment{" +
//                "name='" + name + '\'' +
//                ", value=" + value +
//                '}';
//    }

    @Override
    public String toString() {
        return
                 name + '=' + value;
    }
}

// BinaryExpression.java
class BinaryExpression extends ASTNode {
    public final ASTNode left;
    public final String operator;
    public final ASTNode right;

    public BinaryExpression(ASTNode left, String operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

//    @Override
//    public String toString() {
//        return "BinaryExpression{" +
//                "left=" + left +
//                ", operator='" + operator + '\'' +
//                ", right=" + right +
//                '}';
//    }

    @Override
    public String toString() {
        return "(" + left + " " + operator + " " + right + ")";
    }
}

// BooleanExpression.java
class BooleanExpression extends ASTNode {
    public final ASTNode left;
    public final String operator;
    public final ASTNode right;

    public BooleanExpression(ASTNode left, String operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public String toString() {
        return "BooleanExpression{" +
                "left=" + left +
                ", operator='" + operator + '\'' +
                ", right=" + right +
                '}';
    }
}

// Literal.java
class Literal extends ASTNode {
    public final Object value;

    public Literal(Object value) {
        this.value = value;
    }

//    @Override
//    public String toString() {
//        return "Literal{" +
//                "value=" + value +
//                '}';
//    }

    @Override
    public String toString() {
        return value.toString();
    }
}

// Identifier.java
class Identifier extends ASTNode {
    public final String name;

    public Identifier(String name) {
        this.name = name;
    }

//    @Override
//    public String toString() {
//        return "Identifier{" +
//                "name='" + name + '\'' +
//                '}';
//    }

    @Override
    public String toString() {
        return name;
    }
}


class Block extends ASTNode {
    public final List<ASTNode> statements;

    public Block(List<ASTNode> statements) {
        this.statements = statements;
    }

    @Override
    public String toString() {
        return "Block{" +
                "statements=" + statements +
                '}';
    }
}

// UnaryExpression.java
class UnaryExpression extends ASTNode {
    public final String operator;
    public final ASTNode right;

    public UnaryExpression(String operator, ASTNode right) {
        this.operator = operator;
        this.right = right;
    }

//    @Override
//    public String toString() {
//        return "UnaryExpression{" +
//                "operator='" + operator + '\'' +
//                ", right=" + right +
//                '}';
//    }

    @Override
    public String toString() {
        return operator + ' ' + right;
    }
}

// VariableDeclarationList.java

class VariableDeclarationList extends ASTNode {
    public final List<VariableDeclaration> declarations;

    public VariableDeclarationList(List<VariableDeclaration> declarations) {
        this.declarations = declarations;
    }

    @Override
    public String toString() {
        return "VariableDeclarationList{" +
                "declarations=" + declarations +
                '}';
    }
}