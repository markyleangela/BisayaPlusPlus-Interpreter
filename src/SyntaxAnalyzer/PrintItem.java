package SyntaxAnalyzer;

import LexicalAnalyzer.Token;

abstract class PrintItem {
    static class PrintLiteral extends PrintItem {
        final String value;
        PrintLiteral(String value) {
            this.value = value;
        }
    }

    static class PrintConcat extends PrintItem {
        final PrintItem left;
        final PrintItem right;
        PrintConcat(PrintItem left, PrintItem right) {
            this.left = left;
            this.right = right;
        }
    }

    static class PrintExpr extends PrintItem {
        final Expr expression;
        PrintExpr(Expr expression) {
            this.expression = expression;
        }
    }

    static class PrintVariable extends PrintItem {
        final Token name;
        PrintVariable(Token name) {
            this.name = name;
        }
    }

    static class PrintEscape extends PrintItem {
        final String escape;
        PrintEscape(String escape) {
            this.escape = escape;
        }
    }

    static class PrintNewline extends PrintItem {
        PrintNewline() {}
    }
}






