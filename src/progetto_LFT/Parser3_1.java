package progetto_LFT;
import java.io.*;
public class Parser3_1 {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;
    public Parser3_1(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }
    void move() {
        look = lex.lexicalScan(pbr);
        System.out.println("token = " + look);
    }
    void error(String s) {
        throw new Error("near line " + lex.line + ": " + s);
    }
    void match(int t) {
        if(look.tag == t) {
            if(look.tag != Tag.EOF) {
                move();
            }
        } else {
            error("Syntax Error");
        }
    }

    public void start() {
        if(look.tag == '(' || look.tag == Tag.NUM){
            expr();
            match(Tag.EOF);
        } else {
            error ("Syntax Error in start");
        }
    }
    public void expr() {
        if(look.tag == '(' || look.tag == Tag.NUM) {
            term();
            exprp();
        } else {
            error("Syntax Error in expr");
        }
    }
    private void exprp() {
        switch(look.tag) {
            case '+':
                match('+');
                term();
                exprp();
                break;
            case '-':
                match('-');
                term();
                exprp();
                break;
            case ')':
            case Tag.EOF:
                break;
            default:
                error("Syntax Error in exprp");
        }
    }
    private void term() {
        if(look.tag == '(' || look.tag == Tag.NUM) {
            fact();
            termp();
        } else {
            error("Syntax Error in term");
        }
    }
    private void termp() {
        switch(look.tag) {
            case '*':
                match('*');
                fact();
                termp();
                break;
            case '/':
                match('/');
                fact();
                termp();
                break;
            case ')':
            case '+':
            case '-':
            case Tag.EOF:
                break;
            default:
                error("Syntax Error in termp");
        }
    }
    private void fact() {
        switch (look.tag) {
            case '(':
                match('(');
                expr();
                match(')');
                break;
            case Tag.NUM:
                match(Tag.NUM);
                break;
            default:
                error("Syntax Error in fact");
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "D:\\IJ projects\\LFT\\src\\test_files\\test1.lft";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser3_1 parser = new Parser3_1(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
