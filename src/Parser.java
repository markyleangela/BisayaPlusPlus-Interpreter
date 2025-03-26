import java.util.List;
import java.util.ArrayList;

public class Parser {
    private final List<Token> tokens;
    private int position;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
    }

    public ASTNode parse() {
        List<ASTNode> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(parseStatement());
        }
        return new Block(statements);
    }

    private ASTNode parseStatement() {
        if (match(TokenType.NUMERO, TokenType.LETRA, TokenType.TINUOD, TokenType.MUGNA)) {
            return parseVariableDeclaration();
        } else if (match(TokenType.IDENTIFIER)) {
            return parseVariableAssignment();
        } else {
            throw new RuntimeException("Unexpected token: " + peek().type);
        }
    }

    private ASTNode parseVariableDeclaration() {
        Token typeToken = previous();
        System.out.println(typeToken);
        if (peek().type == TokenType.TINUOD) {
            consume(TokenType.TINUOD, "Expect TINUOD for variable declaration.");
        } else if (peek().type == TokenType.NUMERO) {
            consume(TokenType.NUMERO, "Expect NUMERO for variable declaration.");
        } else if (peek().type == TokenType.LETRA) {
            consume(TokenType.LETRA, "Expect LETRA for variable declaration.");
        } else if (peek().type == TokenType.MUGNA) {
            consume(TokenType.MUGNA, "Expect MUGNA for variable declaration.");
        }

        List<VariableDeclaration> declarations = new ArrayList<>();
        do {
            Token nameToken = consume(TokenType.IDENTIFIER, "Expect variable name.");
            ASTNode value = null;
            if (match(TokenType.ASSIGNMENT)) {
                value = parseExpression();
            }
            declarations.add(new VariableDeclaration(typeToken.type.name(), nameToken.value.toString(), value));
        } while (match(TokenType.COMMA));

        return new VariableDeclarationList(declarations);
    }

    private ASTNode parseVariableAssignment() {
        Token nameToken = previous();
        consume(TokenType.ASSIGNMENT, "Expect '=' after variable name.");
        ASTNode value = parseExpression();

        while (match(TokenType.ASSIGNMENT)) {
            Token nextNameToken = previous();
            value = new VariableAssignment(nextNameToken.value.toString(), value);
        }

        return new VariableAssignment(nameToken.value.toString(), value);
    }

    private ASTNode parseExpression() {
        return parseAssignment();
    }

    private ASTNode parseAssignment() {
        ASTNode expr = parseEquality();

        if (match(TokenType.ASSIGNMENT)) {
            Token equals = previous();
            ASTNode value = parseAssignment();
            if (expr instanceof Identifier) {
                String name = ((Identifier) expr).name;
                return new VariableAssignment(name, value);
            }
            throw new RuntimeException("Invalid assignment target.");
        }

        return expr;
    }


//    private ASTNode parseExpression() {
//        return parseEquality();
//    }

    private ASTNode parseEquality() {
        ASTNode expr = parseComparison();

        while (match(TokenType.EQUALS, TokenType.NOT_EQUALS)) {
            Token operator = previous();
            ASTNode right = parseComparison();
            expr = new BooleanExpression(expr, operator.value.toString(), right);
        }

        return expr;
    }

    private ASTNode parseComparison() {
        ASTNode expr = parseTerm();

        while (match(TokenType.GREATER_THAN, TokenType.LESS_THAN, TokenType.GREATER_EQUAL, TokenType.LESS_EQUAL)) {
            Token operator = previous();
            ASTNode right = parseTerm();
            expr = new BooleanExpression(expr, operator.value.toString(), right);
        }

        return expr;
    }

    private ASTNode parseTerm() {
        ASTNode expr = parseFactor();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            ASTNode right = parseFactor();
            expr = new BinaryExpression(expr, operator.value.toString(), right);
        }

        return expr;
    }

    private ASTNode parseFactor() {
        ASTNode expr = parseUnary();

        while (match(TokenType.MULTIPLY, TokenType.DIVIDE)) {
            Token operator = previous();
            ASTNode right = parseUnary();
            expr = new BinaryExpression(expr, operator.value.toString(), right);
        }

        return expr;
    }

    private ASTNode parseUnary() {
        if (match(TokenType.NOT, TokenType.MINUS)) {
            Token operator = previous();
            ASTNode right = parseUnary();
            return new UnaryExpression(operator.value.toString(), right);
        }

        return parsePrimary();
    }

    private ASTNode parsePrimary() {
        if (match(TokenType.NUMBER)) {
            return new Literal(previous().value);
        }

        if (match(TokenType.IDENTIFIER)) {
            return new Identifier(previous().value.toString());
        }

        if (match(TokenType.LPAREN)) {
            ASTNode expr = parseExpression();
            consume(TokenType.RPAREN, "Expect ')' after expression.");
            return expr;
        }

        throw new RuntimeException("Unexpected token: " + peek().type);
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) position++;
        return previous();
    }

    private boolean isAtEnd() {
        return position >= tokens.size();
    }

    private Token peek() {
        return tokens.get(position);
    }

    private Token previous() {
        return tokens.get(position - 1);
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw new RuntimeException(message);
    }
}