package progetto_LFT;
import java.io.*;
public class Valutatore4_1 {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Valutatore4_1(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexicalScan(pbr);
        System.out.println("Token = " + look);
    }
    void error (String s) {
        throw new Error ("near line " + lex.line + ": " + s);
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
        int expr_val;
        if(look.tag == '(' || look.tag == Tag.NUM) {
            expr_val = expr();
            match(Tag.EOF);
            System.out.println(expr_val);
        } else {
            error("Syntax Error in start");
        }
    }
    private int expr() {
        int term_val, exprp_val = 0;
        if(look.tag == '(' || look.tag == Tag.NUM) {
            term_val = term();
            exprp_val = exprp(term_val);
        } else {
            error("Syntax error in expr");
        }
        return exprp_val;
    }
    private int exprp(int exprp_i) {
        int term_val, exprp_val = 0;
        switch (look.tag) {
            case '+':
                match('+');
                term_val = term();
                exprp_val = exprp(exprp_i + term_val);
                break;
            case '-':
                match('-');
                term_val = term();
                exprp_val = exprp(exprp_i - term_val);
                break;
            case ')':
            case Tag.EOF:
                exprp_val = exprp_i;
                break;
            default:
                error("Syntax Error in exprp");
        }
        return exprp_val;
    }
    private int term() {
        int fact_val, termp_val = 0;
        if(look.tag == '(' || look.tag == Tag.NUM) {
            fact_val = fact();
            termp_val = termp(fact_val);
        } else {
            error("Syntax Error in term");
        }
        return termp_val;
    }
    private int termp(int termp_i) {
        int fact_val, termp_val = 0;
        switch (look.tag) {
            case '*':
                match('*');
                fact_val = fact();
                termp_val = termp(termp_i * fact_val);
                break;
            case '/':
                match('/');
                fact_val = fact();
                termp_val = termp(termp_i / fact_val);
                break;
            case ')':
            case '+':
            case '-':
            case Tag.EOF:
                termp_val = termp_i;
                break;
            default:
                error("Syntax Error in termp");
        }
        return termp_val;
    }
    private int fact() {
        int fact_val = 0;
        switch (look.tag) {
            case '(':
                match('(');
                fact_val = expr();
                match(')');
                break;
            case Tag.NUM :
                if(look instanceof NumberTok num) {
                    fact_val = Integer.parseInt(num.lexeme);
                    match(Tag.NUM);
                } else {
                    throw new RuntimeException("Errore: il token non e' un numero");
                }
                break;
            default:
                error("Syntax Error in fact");
        }
        return fact_val;
    }
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "D:\\IJ projects\\LFT\\src\\progetto_LFT\\test2.lft";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Valutatore4_1 val = new Valutatore4_1(lex, br);
            val.start();
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
