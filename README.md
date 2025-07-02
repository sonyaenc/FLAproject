# FLAproject
This is a simple lexical analyzer developed for the course: Formal Languages Languages and Automata Theory.
The project demonstrates the ability to define and implement a tokenizer capable of identifying valid tokens in a given input string or file,
based on a simple grammar and regular expressions.

*keep in mind: the comments describing various parts of the code are written in italian. For now at least, I won't change them*
*the project is not perfect, it requires additional changes, and some other tests may not work correctly, because of the restricting grammar it works upon.
You are welcome to suggest additional alterations and features if you are interested in continuing this small project*

## Features
- identifies keywords, identifiers, numbers, operators and punctuation
- written in Java
- processes input from test files with the formal .lft (stands for "linguaggi formali e traduttori", meaning "formal languages and traslators")
- some test files may require user input
- the lexer and parser output the list of recognised tokens with their type, based on an ASCII like table
- the translator5_1 (final part of the project) also generates the bytecode for the contents of the test file

