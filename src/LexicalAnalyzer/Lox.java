package LexicalAnalyzer;

import SyntaxAnalyzer.*;
import Utils.RuntimeError;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Lox {
    static  boolean hadError = false;
    static boolean hadRuntimeError = false;
    private static final Interpreter interpreter = new
            Interpreter();
    public static void main(String[] args) throws IOException {
        if(args.length > 1){
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        }else if(args.length == 1){
            runFile(args[0]);
        }else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException{
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if(hadError) System.exit(65);
        if (hadRuntimeError) System.exit(70);
    }
//
//    private static void runPrompt() throws IOException{
//        InputStreamReader input = new InputStreamReader(System.in);
//        BufferedReader reader = new BufferedReader(input);
//
//        for(;;){
//            System.out.print("> ");
//            String line = reader.readLine();
//            if(line == null) break;
//            run(line);
//            hadError=false;
//        }
//    }

    private static void runPrompt() throws IOException {
        // Read the entire file content as a single string
        BufferedReader reader = new BufferedReader(new FileReader("src/Test/Logical.txt"));
        StringBuilder sourceBuilder = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            sourceBuilder.append(line).append("\n"); // Append each line with a newline
        }
        reader.close();

        // Pass the entire source to the Lexer
        run(sourceBuilder.toString());
    }


    private static void run(String source) {
        Lexer scanner = new Lexer(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);

        List<Stmt> statements = parser.parse();
        // Stop if there was a syntax error.
        if (hadError) return;

        // Print the parsed statements
//        AstPrinter printer = new AstPrinter();
//        for (Stmt statement : statements) {
//            System.out.println(printer.print(statement));
//        }

        interpreter.interpret(statements);
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    public static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'",
                    message);
        }
    }

    public static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() +
                "\n[line " + error.getToken().line + "]");
        hadRuntimeError = true;
    }

    private static void report(int line, String where, String message){
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }


}
