package progetto_LFT;
import java.io.*;
class Lexer {
    public static int line = 1;
    private char peek = ' ';
    public void readCh(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; //ERROR
        }
    }

    public Token lexicalScan (BufferedReader br) { //return type Token
        while(peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
            if(peek == '\n') {
                line++;
            }
            readCh(br);
        }
        switch (peek) {
            case ':':
                readCh(br);
                if(peek == '=') {
                    peek = ' ';
                    return Word.init;
                }
            case '!':
                peek = ' ';
                return Token.not;
            case ';':
                peek = ' ';
                return Token.semicolon;
            case '+':
                peek = ' ';
                return Token.plus;
            case '-':
                peek = ' ';
                return Token.minus;
            case '*':
                peek = ' ';
                return Token.mult;
            case '/':
                readCh(br);
                if(peek == '/'){
                    readComm(br, 0);
                    return lexicalScan(br);
                } else if (peek == '*'){
                    readComm(br, 1);
                    return lexicalScan(br);
                } else {
                    peek = ' ';
                    return Token.div;
                }
            case ',':
                peek = ' ';
                return Token.comma;
            case '(':
                peek = ' ';
                return Token.lpt;
            case ')':
                peek = ' ';
                return Token.rpt;
            case '[':
                peek = ' ';
                return Token.lpq;
            case ']':
                peek = ' ';
                return Token.rpq;
            case '{':
                peek = ' ';
                return Token.lpg;
            case '}':
                peek = ' ';
                return Token.rpg;
            case '=':
                readCh(br);
                if(peek == '=') {
                    peek = ' ';
                    return Word.eq;
                } else {
                    System.err.println("Erroneous character" + " after = : " + peek);
                    return null;
                }
            case '<':
                readCh(br);
                if(peek == '=') {
                    peek = ' ';
                    return Word.le;
                } else if (peek == '>') {
                    peek = ' ';
                    return Word.ne;
                } else {
                    peek = ' ';
                    return Word.lt;
                }
            case '>':
                readCh(br);
                if(peek == '=') {
                    peek = ' ';
                    return Word.ge;
                } else {
                    peek = ' ';
                    return Word.gt;
                }
            case '&':
                readCh(br);
                if(peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character" + " after &: " + peek);
                }
            case '|':
                readCh(br);
                if(peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else {
                    System.err.println("Erroneous character" + " after |: " + peek);
                }
            case (char) -1:
                return new Token(Tag.EOF);

            default:
                if(Character.isLetter(peek)|| peek == '_') {
                    StringBuilder str = new StringBuilder();
                    while(Character.isLetterOrDigit(peek) || peek == '_') {
                        str.append(peek);
                        readCh(br);
                    }

                    String identifier_RE = "[a-zA-Z_*a-zA-Z0-9][a-zA-Z0-9_]*";
                    switch (str.toString()) {
                        case "assign":
                            return Word.assign;
                        case "to":
                            return Word.to;
                        case "if":
                            return Word.iftok;
                        case "else":
                            return Word.elsetok;
                        case "do":
                            return Word.dotok;
                        case "for":
                            return Word.fortok;
                        case "begin":
                            return Word.begin;
                        case "end":
                            return Word.end;
                        case "print":
                            return Word.print;
                        case "read" :
                            return Word.read;
                        default:
                            if (str.toString().matches(identifier_RE)) {
                                return new Word(Tag.ID, str.toString());
                            }
                            System.err.println("Syntax error in: " + str);
                            return null;
                    }

                } else if (Character.isDigit(peek)) {
                    StringBuilder num = new StringBuilder();
                    while(Character.isDigit(peek)) {
                        num.append(peek);
                        readCh(br);
                    }
                    if (num.charAt(0) != '0')
                        return new NumberTok(Tag.NUM, num.toString());
                    else
                        return new NumberTok(Tag.NUM, "0");
                } else {
                    System.err.println("Erroneous character: " + peek);
                    return null;
                }
        }
    }

    //readComm --> gestisce il tipo di commenti che potrebbero essere presenti nella stringa
    public void readComm(BufferedReader br, int commType) {
        if (commType == 0) {
            // Commento singola riga con //
            while (peek != '\n' && peek != (char) -1) {
                readCh(br);
            }
        } else {
            // Commento multi-riga con /* ... */
            boolean closed = false;
            readCh(br); // legge il primo carattere dopo '*'
            while (peek != (char) -1) {
                if (peek == '*') {
                    readCh(br);
                    if (peek == '/') {
                        closed = true;
                        readCh(br); // consumiamo anche lo slash finale
                        break;
                    }
                } else {
                    readCh(br);
                }
            }
            if (!closed) {
                System.err.println("ERRORE: commento multi-linea non chiuso correttamente");
            }
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "D:\\IJ projects\\LFT\\src\\test_files\\test_lex.lft";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexicalScan(br);
                System.out.println("Scan " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
