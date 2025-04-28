
# Bisaya Interpreter

### HOW TO RUN/TEST

1. Clone the repository
2. ```bash
    git clone git@github.com:markyleangela/BisayaPlusPlus-Interpreter.git
3. To add Test Cases
- Create a new txt file in the src/Test directory
4. Change path in LexicalAnalyzer/Lox file 
5. ```bash
   BufferedReader reader = new BufferedReader(new FileReader("src/Test/filename.txt"));
## Description
Bisaya++ is a strongly–typed high–level interpreted Cebuano-based programming language developed to teach Cebuanos
the basics of programming. Its simple syntax and native keywords make programming easy to learn.

## Language Grammar
Program Structure:
- all codes are placed inside SUGOD and KATAPUSAN
- all variable declaration starts with MUGNA
- all variable names are case sensitive and starts with letter or an underscore (_) and followed by a letter,
  underscore or digits.
- every line contains a single statement
- comments starts with double minus sign(--) and it can be placed anywhere in the program
- all reserved words are in capital letters and cannot be used as variable names
- dollar sign($) signifies next line or carriage return
- ampersand(&) serves as a concatenator
- the square braces([]) are as escape code


## Data Types:
1. NUMERO – an ordinary number with no decimal part. It occupies 4 bytes in the memory.
2. LETRA – a single symbol.
3. TINUOD – represents the literals true or false.
4. TIPIK – a number with decimal part.

## Operators

#### Arithmetic Operators
( ) - Parenthesis

*, /, % - Multiplication, Division, Modulo

+, - - Addition, Subtraction

'>' , '<' - Greater than, Lesser than

'>=', '<=' - Greater than or equal to, Lesser than or equal to

==, <> - Equal, Not Equal

## Logical Operators



UG - AND (Both expressions must be true)

O - OR (At least one expression must be true)

DILI - NOT (Reverses the boolean value)

Boolean Values

(Enclosed with double quotes)

"OO" - TRUE

"DILI" - FALSE

## Unary Operators
'+' - Positive

'-' - Negative
