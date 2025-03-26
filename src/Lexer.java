import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private static final Pattern TOKEN_PATTERNS = Pattern.compile(
            "(IPAKITA:|DAWAT:|KUNG|DILI|[a-zA-Z_][a-zA-Z0-9_]*|\\d+|==|!=|<=|>=|&&|\n|\\(|\\)|\\+|\\-|\\*|\\/|=|;)");


    public static List<Token> lex(String input) {
        List<Token> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERNS.matcher(input);

        while (matcher.find()) {
            String token = matcher.group();
            TokenType type;

            switch (token) {
                case "SUGOD":
                    type = TokenType.START;
                    break;
                case "KATAPUSAN":
                    type = TokenType.END;
                    break;
                case "IPAKITA:":
                    type = TokenType.IPAKITA;
                    break;
                case "DAWAT:":
                    type = TokenType.DAWAT;
                    break;
                case "KUNG":
                    type = TokenType.IF;
                    break;
                case "KUNG WALA":
                    type = TokenType.ELSE;
                    break;
                case "KUNG DILI":
                    type = TokenType.ELSE_IF;
                    break;
                case "PUNDOK":
                    type = TokenType.BLOCK_START;
                    break;
                case "DILI":
                case "OO":
                    type = TokenType.BOOLEAN_LITERAL;
                    break;


                case "==": case "<>": case "<=": case ">=":
                    type = TokenType.RELATIONAL_OPERATOR;
                    break;
                case "O": case "UG":
                    type = TokenType.LOGICAL_OPERATOR;
                    break;
                case "=":
                    type = TokenType.ASSIGNMENT;
                    break;
                case "(":
                    type = TokenType.LPAREN;
                    break;
                case ")":
                    type = TokenType.RPAREN;
                    break;
                case "+":
                    type = TokenType.PLUS;
                    break;
                case "-":
                    type = TokenType.MINUS;
                    break;
                case "*":
                    type = TokenType.MULTIPLY;
                    break;
                case "/":
                    type = TokenType.DIVIDE;
                    break;
                case ";":
                    type = TokenType.SEMICOLON;
                    break;
                default:
                    if(isKeyword(token)) {
                        type = TokenType.valueOf(token);
                    } else if (token.matches("\\d+")) {
                        type = TokenType.NUMBER;
                    } else if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                        type = TokenType.IDENTIFIER;
                    } else {
                        type = TokenType.UNKNOWN;
                    }
            }
            tokens.add(new Token(type, token));
        }
        return tokens;
    }

    private static boolean isKeyword(String token) {
        try {
            TokenType.valueOf(token);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
