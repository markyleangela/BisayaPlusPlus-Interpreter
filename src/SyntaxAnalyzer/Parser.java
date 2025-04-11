package SyntaxAnalyzer;

import LexicalAnalyzer.Lox;
import LexicalAnalyzer.Token;
import LexicalAnalyzer.TokenType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import SyntaxAnalyzer.Stmt;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;
    private static class ParseError extends RuntimeException {}
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Expr expression() {
        return assignment();
    }

    private Stmt declaration() {
        try {
            if (match(TokenType.MUGNA)) {
                return varDeclaration(); // Handle variable declaration first
            }
            return statement(); // If not variable declaration, treat as a statement
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }




    private Stmt statement() {
        if (match(TokenType.IF)) return ifStatement();
        if (match(TokenType.PRINT)){
            consume(TokenType.COLON, "Expect ':' after print statement.");
            return printStatement();
        }

        if (match(TokenType.LBRACE)) return new Stmt.Block(block());
        return expressionStatement();
    }

    private Stmt printStatement() {
        Expr value = expression();

        return new Stmt.Print(value);
    }


    private Stmt ifStatement() {
        consume(TokenType.LPAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(TokenType.RPAREN, "Expect ')' after if condition.");
        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(TokenType.ELSE)) {

            elseBranch = statement();
        }
        return new Stmt.If(condition, thenBranch, elseBranch);
    }

//    private Stmt varDeclaration() {
//        Token type = consume(TokenType.NUMERO, TokenType.LETRA, TokenType.TIPIK, TokenType.TINUOD);
//        Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");
//
//        Expr initializer = null;
//        if (match(TokenType.ASSIGNMENT)) {
//            initializer = expression();
//        }
//        return new Stmt.Var(name, initializer, type);
//    }

    private Stmt varDeclaration() {
        consume(TokenType.NUMERO, "Expect type for variable declaration.");
        Token type = previous();  // Capture the type (NUMERO)
        List<Stmt.Var> vars = new ArrayList<>();

        // Parse multiple variables separated by commas
        do {
            Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");
            consume(TokenType.ASSIGNMENT, "Expect '=' after variable name.");
            Expr initializer = expression();  // Expression for the initializer (like 10 or 20)

            vars.add(new Stmt.Var(name, initializer, type));  // Add each variable declaration

        } while (match(TokenType.COMMA));  // Continue parsing if there are more variables

        return new Stmt.VarDeclaration(vars);  // Return a block of multiple Stmt.Var
    }




    private Stmt expressionStatement() {
        Expr expr = expression();

        return new Stmt.Expression(expr);
    }


    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            statements.add(declaration());
        }
        consume(TokenType.RBRACE, "Expect '}' after block.");
        return statements;
    }

    private Expr assignment() {
        Expr expr = equality();
        if (match(TokenType.ASSIGNMENT)) {
            Token equals = previous();
            Expr value = assignment();
            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable)expr).name;
                return new Expr.Assign(name, value);
            }
            error(equals, "Invalid assignment target.");
        }
        return expr;
    }

    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            Stmt decl = declaration();
            if (decl != null) {
                statements.add(decl);
            }
        }
        return statements;
    }




    private Expr equality() {
        Expr expr = comparison();
        while (match(TokenType.NOT_EQUALS, TokenType.EQUALS)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
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
        return peek().getTokenType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().getTokenType() == TokenType.EOF;
    }
    private Token peek() {
        return tokens.get(current);
    }
    private Token previous() {
        return tokens.get(current - 1);
    }

    private Expr comparison() {
        Expr expr = term();
        while (match(TokenType.GREATER_THAN, TokenType.GREATER_EQUAL, TokenType.LESS_THAN, TokenType.LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr term() {
        Expr expr = factor();
        while (match(TokenType.MINUS, TokenType.PLUS, TokenType.CONCAT, TokenType.NEXT_LINE)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr factor() {
        Expr expr = unary();
        while (match(TokenType.DIVIDE, TokenType.MULTIPLY)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr unary() {
        if (match(TokenType.NOT, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return primary();
    }

    private Expr primary() {
        if (match(TokenType.BOOL_FALSE)) return new Expr.Literal(false);
        if (match(TokenType.BOOL_TRUE)) return new Expr.Literal(true);
        if (match(TokenType.NULL)) return new Expr.Literal(null);
        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return new Expr.Literal(previous().getLiteral());
        }
        if (match(TokenType.LPAREN)) {
            Expr expr = expression();
            consume(TokenType.RPAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }
        if (match(TokenType.IDENTIFIER)) {
            return new Expr.Variable(previous());
        }
        throw this.error(this.peek(), "Expect expression.");
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw this.error(this.peek(), message);
    }

    private Token consume(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) return advance();
        }
        throw this.error(peek(), "Expect one of " + Arrays.toString(types));
    }




    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }


    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().getTokenType() == TokenType.NEXT_LINE) return;

            switch (peek().getTokenType()) {
                case MUGNA:
                case START: //sugod
                case END: //katapusan
                case PRINT: // ipakita
                case INPUT: // dawat
                case BLOCK: // pundok
                case IF: // kung
                case ELSE_IF: // kung wala
                case ELSE:  //kung dili
                case FOR: // alang sa
                    return;
            }

            advance();
        }
    }



}
