package LexicalAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private  int start = 0;
    private int current =0;
    private int line = 1;
    private static final Map<String, TokenType> keywords;

    public Lexer(String source){
        this.source = source;
    }

    static {
        keywords = new HashMap<>();
        keywords.put("MUGNA",TokenType.MUGNA);
        keywords.put("SUGOD",TokenType.START);
        keywords.put("KATAPUSAN",TokenType.END);
        keywords.put("NUMERO",TokenType.NUMERO);
        keywords.put("TIPIK",TokenType.TIPIK);
        keywords.put("LETRA",TokenType.LETRA);
        keywords.put("TINUOD",TokenType.TINUOD);
        keywords.put("IPAKITA",TokenType.PRINT);
        keywords.put("DAWAT",TokenType.INPUT);
        keywords.put("KUNG",TokenType.IF);
        keywords.put("KUNG WALA",TokenType.ELSE);
        keywords.put("KUNG DILI",TokenType.ELSE_IF);
        keywords.put("PUNDOK",TokenType.BLOCK);
        keywords.put("DILI",TokenType.BOOL_FALSE);
        keywords.put("OO",TokenType.BOOL_TRUE);
        keywords.put("ALANG SA",TokenType.FOR);
        keywords.put("UG",TokenType.AND);
        keywords.put("O",TokenType.OR);
    }

    public List<Token> scanTokens(){
        while (!isAtEnd()){
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "",null, line));
        return tokens;
    }

    public boolean isAtEnd(){
        return current >= source.length();
    }

    private void scanToken(){
        char val = advance();
        switch (val){
            case '(': addToken(TokenType.LPAREN);break;
            case ')': addToken(TokenType.RPAREN);break;
            case '{': addToken(TokenType.LBRACE);break;
            case '}': addToken(TokenType.RBRACE);break;
            case ',': addToken(TokenType.COMMA);break;
//            case '-': addToken(TokenType.MINUS);break; // this has to be handled differently because comments starts with --

            case '[':
                if (match('#') && match(']')) {
                    addToken(TokenType.ESCAPE_HASH); // Add ESCAPE_HASH to your TokenType
                } else {
                    addToken(TokenType.LBRACKET);
                }
                break;

            case '&': addToken(TokenType.CONCAT);break;
            case '$': addToken(TokenType.NEXT_LINE);break;
            case ':': addToken(TokenType.COLON);break;
            case '*': addToken(TokenType.MULTIPLY);break;
            case '%': addToken(TokenType.MODULO);break;
            case '+': addToken(TokenType.PLUS);break;
            case '/': addToken(TokenType.DIVIDE);break;
            case '=': addToken(match('=')? TokenType.EQUALS : TokenType.ASSIGNMENT);break;
            case '<':
                if (match('=')) {
                    addToken(TokenType.LESS_EQUAL);
                } else if (match('>')) {
                    addToken(TokenType.NOT_EQUALS);
                } else {
                    addToken(TokenType.LESS_THAN);
                }
                break;
            case '>': addToken(match('=')? TokenType.GREATER_EQUAL : TokenType.GREATER_THAN);break;
            case '-':
                if(match('-')){
                    while(peek() != '\n' && !isAtEnd()) advance();
                }else{
                    addToken(TokenType.MINUS);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;
            case '"': string();  break;
            case '\'': character();  break;
            default:
                if(isDigit(val)){
                    number();
                }else if(isAlpha(val)){
                    identifier();
                }else{
                    Lox.error(line, "Unexpected character." + " " + val);
                }
                break;


        }
    }

    private char peek(){
        if(isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext(){
        if(current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private char advance(){
        return  source.charAt(current++);
    }

    private void addToken(TokenType type){
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal){
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));

    }

    private boolean match(char expected){
        if(isAtEnd()) return false;
        if(source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private void string(){
        while(peek() != '"' && !isAtEnd()){
            if(peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()){
            Lox.error(line, "Unclosed string.");
            return;
        }

        advance();

        String value = source.substring(start + 1, current - 1);
        if (value.equals("OO")) {
            addToken(TokenType.BOOL_TRUE, value);
        } else if(value.equals("DILI")){
            addToken(TokenType.BOOL_FALSE, value);
        }else {
            addToken(TokenType.STRING, value);
            return;
        }

        addToken(TokenType.STRING, value);
    }

    private void character() {
        if (isAtEnd()) {
            Lox.error(line, "Unclosed character literal.");
            return;
        }

        char c = advance(); // Get the character inside the single quote

        // Check for escape characters like '\n'
        if (c == '\\' && !isAtEnd()) {
            c = advance(); // Advance to get the actual escaped character
        }

        if (peek() != '\'') {
            Lox.error(line, "Unclosed or invalid character literal.");
            return;
        }

        advance(); // Consume the closing single quote

        // You can add a validation here for more than one character if needed

        addToken(TokenType.CHARACTER, String.valueOf(c));
    }


    private boolean isDigit(char c){
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c){
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isAlphaNumeric(char c){
        return isAlpha(c) || isDigit(c);
    }

    private void number(){
        while(isDigit(peek())) advance();

        if(peek() == '.' && isDigit(peekNext())){
            advance();
            while(isDigit(peek())) advance();
        }

        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void identifier(){
        while(isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if(type == null) type = TokenType.IDENTIFIER;
        addToken(type);
    }
}

