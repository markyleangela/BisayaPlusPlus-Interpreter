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
                return varDeclaration();
            }
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }


    private Stmt statement() {

        if (match(TokenType.IF)) return ifStatement();
        if (match(TokenType.FOR)) return forStatement();
        if (match(TokenType.INPUT)){
            consume(TokenType.COLON, "Expect ':' after input statement.");
            return inputStatement();
        }
        if (match(TokenType.PRINT)){
            consume(TokenType.COLON, "Expect ':' after print statement.");
            return printStatement();
        }

        if (match(TokenType.LBRACE)) return new Stmt.Block(block());
        return expressionStatement();
    }

    private Stmt inputStatement() {
        List<Token> variableNames = new ArrayList<>();


        do {
            Token variableName = consume(TokenType.IDENTIFIER, "Expect variable name.");
            variableNames.add(variableName);
        } while (match(TokenType.COMMA));

        return new Stmt.Input(variableNames);
    }



    private Stmt printStatement() {
        Expr value = expression();

        return new Stmt.Print(value);
    }

    private Expr or() {
        Expr expr = and();
        while (match(TokenType.OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }

    private Expr and() {
        Expr expr = equality();
        while (match(TokenType.AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }



    private Stmt ifStatement() {
        consume(TokenType.LPAREN, "Expect '(' after 'KUNG'.");
        Expr condition = expression();
        consume(TokenType.RPAREN, "Expect ')' after KUNG condition.");
        consume(TokenType.BLOCK, "Expect 'PUNDOK' after ')'");
        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(TokenType.ELSE_IF)) {
            elseBranch = ifStatement();
        }
        else if (match(TokenType.ELSE)) {
            consume(TokenType.BLOCK, "Expect 'PUNDOK' after 'KUNG WALA'");
            elseBranch = statement();
        }
        return new Stmt.If(condition, thenBranch, elseBranch);
    }



    private Stmt forStatement() {
        consume(TokenType.LPAREN, "Expect '(' after 'ALANG SA'.");


        Stmt initializer;
        if (match(TokenType.COMMA)) {
            initializer = null;
        }
        else if (match(TokenType.MUGNA)) {
            initializer = singleVarDeclaration();
        } else {
            initializer = expressionStatement();
        }

        consume(TokenType.COMMA, "Expect ',' after initializer.");


        Expr condition = null;
        if (!check(TokenType.COMMA)) {
            condition = expression();
        }
        consume(TokenType.COMMA, "Expect ',' after loop condition.");


        Expr increment = null;
        if (!check(TokenType.RPAREN)) {
            increment = expression();
        }

        consume(TokenType.RPAREN, "Expect ')' after for clauses.");
        consume(TokenType.BLOCK, "Expect 'PUNDOK' after ).");


        Stmt body = statement();


        if (increment != null) {
            body = new Stmt.Block(
                    Arrays.asList(
                            body,
                            new Stmt.Expression(increment)
                    ));
        }


        if (condition == null) condition = new Expr.Literal(true);


        body = new Stmt.While(condition, body);


        List<Stmt> statements = new ArrayList<>();
        if (initializer != null) statements.add(initializer);
        statements.add(body);
        body = new Stmt.Block(statements);


        return body;
    }











    private Stmt varDeclaration() {

        consume(TokenType.NUMERO, TokenType.LETRA, TokenType.TINUOD, TokenType.TIPIK);
        Token type = previous();
        List<Stmt.Var> vars = new ArrayList<>();


        do {

            Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");


            if (!check(TokenType.ASSIGNMENT)) {
                vars.add(new Stmt.Var(name, null, type));
            } else {

                consume(TokenType.ASSIGNMENT, "Expect '=' after variable name.");
                Expr initializer = expression();
                vars.add(new Stmt.Var(name, initializer, type));
            }


        } while (match(TokenType.COMMA));

        return new Stmt.VarDeclaration(vars);
    }


    private Stmt singleVarDeclaration() {

        consume(TokenType.NUMERO, TokenType.LETRA, TokenType.TINUOD, TokenType.TIPIK);
        Token type = previous();

        List<Stmt.Var> vars = new ArrayList<>();


        Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");

        // Check if the variable has an initializer
        Expr initializer = null;
        if (match(TokenType.ASSIGNMENT)) {

            initializer = expression();
            vars.add(new Stmt.Var(name, initializer, type));
        }


        return new Stmt.VarDeclaration(vars);
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

        Expr expr = or();


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

    public List<Stmt> parse(){
        List<Stmt> statements = new ArrayList<>();
        if(!peek().getLexeme().equals("SUGOD")){
            throw error(peek(),"Expect 'SUGOD' at the start of the program.");
        }

        statements.add(sugodStatement());

        if(!isAtEnd()){
            throw error(peek(),"Expect 'KATAPUSAN' at the end of the program.");
        }
        return statements;

    }

    private Stmt sugodStatement(){
        consume(TokenType.START, "Expect 'SUGOD' at the start of the program.");
        List<Stmt> statements = new ArrayList<>();
        while(!peek().getLexeme().equals("KATAPUSAN") && !isAtEnd()){
            statements.add(declaration());
        }
        consume(TokenType.END, "Expect 'KATAPUSAN' after program.");
        return new Stmt.Sugod(statements);
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
        if (match(TokenType.NUMBER)) {
            return new Expr.Literal(previous().getLiteral());
        }
        if (match(TokenType.STRING)) {
            return new Expr.Literal(previous().getLiteral());
        }
        if (match(TokenType.CHARACTER)) {
            return new Expr.Literal(previous().getLiteral());
        }
        if (match(TokenType.FLOAT)) {
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
        if (match(TokenType.ESCAPE_CODE)) {
            String value = previous().getLiteral().toString();




            if (isIdentifier(value)) {
                return new Expr.Variable(new Token(TokenType.IDENTIFIER, value, value, previous().getLine()));
            }
            if ((value.startsWith("\"") && value.endsWith("\"")) ||
                    (value.startsWith("'") && value.endsWith("'"))) {
                value = value.substring(1, value.length() - 1);
                return new Expr.Literal(value);
            }


            return new Expr.Literal(previous().getLiteral());
        }

        if (match(TokenType.NEXT_LINE)) return new Expr.Literal('\n');



        throw this.error(this.peek(), "Expect expression.");
    }

    private boolean isIdentifier(String value) {

        return value.matches("[a-zA-Z_][a-zA-Z0-9_]*");
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
