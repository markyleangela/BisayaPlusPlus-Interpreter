package SemanticAnalyzer;

import LexicalAnalyzer.Token;
import LexicalAnalyzer.TokenType;
import Utils.RuntimeError;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Object> values = new HashMap<>();
    private final Map<String, String> types = new HashMap<>();

    final Environment enclosing;

    Environment() {
        enclosing = null;
    }
    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    void define(String name, Object value) {
        values.put(name, value);
    }

    void define(String name, Object value, String type){
        values.put(name, value);
        types.put(name, type);
    }

    Object get(Token name) {
        if (values.containsKey(name.getLexeme())) {
            return values.get(name.getLexeme());
        }
        if (enclosing != null) return enclosing.get(name);
        throw new RuntimeError(name,
                "Undefined variable '" + name.getLexeme() + "'.");
    }

    void assign(Token name, Object value) {
        if (values.containsKey(name.getLexeme())) {
            values.put(name.getLexeme(), value);
            return;
        }
        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }
        throw new RuntimeError(name,
                "Undefined variable '" + name.getLexeme() + "'.");
    }

    String getType(String name) {

        if (types.containsKey(name)) {
            return types.get(name);
        }
        throw new RuntimeError(new Token(TokenType.IDENTIFIER, name, null, 0),
                "Undefined variable type '" + name + "'.");
    }


    boolean containsKey(String name) {
        return values.containsKey(name);
    }

    public void printTypes() {
        if(types.isEmpty()) {
            System.out.println("No types defined.");
            return;
        }
        for (Map.Entry<String, String> entry : types.entrySet()) {
            System.out.println("Variable: " + entry.getKey() + ", Type: " + entry.getValue());
        }
    }

    public void printValues() {
        if(values.isEmpty()) {
            System.out.println("No values defined.");
            return;
        }
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            System.out.println("Variable: " + entry.getKey() + ", Type: " + entry.getValue().toString());
        }
    }
}