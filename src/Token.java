public class Token {
    TokenType type;
    Object value;

    public Token(TokenType type, Object value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type='" + type.toString() + '\'' +
                ", value='" + value.toString() + '\'' +
                '}';
    }
}