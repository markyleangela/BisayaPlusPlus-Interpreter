import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        try {
            File file = new File("src/Test.txt");
            Scanner reader = new Scanner(file);

            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                Lexer lex = new Lexer();
                List<Token> tokens = lex.lex(data);
                System.out.println("Tokens: " + tokens);


                Parser parser = new Parser(tokens);
                ASTNode ast = parser.parse();
                System.out.println("AST: " + ast);
            }
        }catch (FileNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}