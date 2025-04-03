//import LexicalAnalyzer.Lexer;
//import LexicalAnalyzer.Token;
//import SyntaxAnalyzer.AstPrinter;
//import SyntaxAnalyzer.Expr;
//import SyntaxAnalyzer.Parser;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.util.List;
//import java.util.Scanner;
//
//public class Main {
//    public static void main(String[] args) {
//
//        try {
//            File file = new File("src/Test.txt");
//            Scanner reader = new Scanner(file);
//
//            while (reader.hasNextLine()) {
//                String data = reader.nextLine();
//                Lexer lexer = new Lexer(data);
//                List<Token> tokens = lexer.scanTokens();
//                System.out.println("Tokens: ");
//                for (Token token : tokens) {
//                    System.out.println(token);
//                }
//
//                Parser parser = new Parser(tokens);
//                Expr expression = parser.parse();
//
//                if (expression != null) {
//                    System.out.println("AST: " + new AstPrinter().print(expression));
//                } else {
//                    System.out.println("Parsing failed.");
//                }
//
//
////                Parser parser = new Parser(tokens);
////                ASTNode ast = parser.parse();
////                System.out.println("AST: " + ast);
//            }
//        }catch (FileNotFoundException e) {
//            System.out.println("Error: " + e.getMessage());
//        }
//    }
//}