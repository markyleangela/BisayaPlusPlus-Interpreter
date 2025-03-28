package SyntaxAnalyzer;

import LexicalAnalyzer.Lox;
import LexicalAnalyzer.Token;
import LexicalAnalyzer.TokenType;

import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;
    private static class ParseError extends RuntimeException {}
    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Expr expression() {
        return equality();
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
        while (match(TokenType.MINUS, TokenType.PLUS)) {
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
        throw this.error(this.peek(), "Expect expression.");
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw this.error(this.peek(), message);
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
