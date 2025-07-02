package progetto_LFT;
import java.io.*;

public class Parser3_2 {
    private final Lexer lex;
    private final BufferedReader pbr;
    private Token look;
    public Parser3_2(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }
    void move() {
        look = lex.lexicalScan(pbr);
        System.out.println("token = " + look);
    }
    void error(String s){
        throw new Error("near line " + lex.line + ": " + s);
    }
    void match(int t) {
        if(look.tag == t) {
            if(look.tag != Tag.EOF) {
                move();
            }
        } else {
            error("Syntax error");
        }
    }
   public void prog() {
        if(look.tag == Tag.ASSIGN || look.tag == Tag.PRINT || look.tag == Tag.READ || look.tag == Tag.FOR
                || look.tag == Tag.IF || look.tag == '{' ) {
            statlist();
            match(Tag.EOF);
        } else {
            error("Syntax Error in prog");
        }
    }

   public void statlist() {
        if(look.tag == Tag.ASSIGN || look.tag == Tag.PRINT || look.tag == Tag.READ || look.tag == Tag.FOR
                || look.tag == Tag.IF || look.tag == '{' ) {
            stat();
            statlistp();
        } else {
            error("Syntax Error in statlist");
        }
    }

   public void statlistp() {
        switch(look.tag) {
            case ';':
                match(';');
                stat();
                statlistp();
                break;
            case Tag.EOF, '}':
                break;
            default:
                error("Syntax Error in statlistp");
        }
    }

   public void stat() {
       switch (look.tag) {
           case Tag.ASSIGN -> {
               match(Tag.ASSIGN);
               assignlist();
           }
           case Tag.PRINT -> {
               match(Tag.PRINT);
               match('(');
               exprlist();
               match(')');
           }
           case Tag.READ -> {
               match(Tag.READ);
               match('(');
               idlist();
               match(')');
           }
           case Tag.FOR -> {
               match(Tag.FOR);
               match('(');
               forexpr();
               match(')');
               match(Tag.DO);
               stat();
           }
           case Tag.IF -> {
               match(Tag.IF);
               match('(');
               bexpr();
               match(')');
               stat();
               statelse();
           }
           case '{' -> {
               match('{');
               statlist();
               match('}');
           }
           default -> error("Syntax error in stat");
       }
    }

    public void forexpr() {
        if(look.tag == Tag.ID) {
            match(Tag.ID);
            if(look.tag == Tag.INIT) {
                match(Tag.INIT);
                expr();
                match(';');
                bexpr();
            } else {
                error("Expected ':=' in loop initialization");
            }
        } else {
            bexpr();
        }
    }

    public void statelse() {
        if (look.tag == Tag.ELSE) {
            match(Tag.ELSE);
            stat();
            match(Tag.END);
        } else if (look.tag == Tag.END) {
            match(Tag.END);
        } else {
            error("Syntax Error in statelse");
        }
    }
   public void assignlist() {
       if (look.tag == '[') {
           match('[');
           expr();
           match(Tag.TO);
           idlist();
           match(']');
           assignlistp();
       } else {
           error("Syntax Error in assignlist");
       }
   }
   public void assignlistp() {
       switch (look.tag) {
           case '[':
               match('[');
               expr();
               match(Tag.TO);
               idlist();
               match(']');
               assignlistp();
               break;
           case ';':
           case Tag.EOF:
               break;
           default:
               error("Syntax Error in assignlistp");
       }
   }

   public void idlist() {
        if(look.tag == Tag.ID) {
            match(Tag.ID);
            idlistp();
        } else {
            error("Syntax Error in idlist");
        }
    }
   public void idlistp() {
        switch(look.tag) {
            case ',':
                match(',');
                match(Tag.ID);
                idlistp();
                break;
            case Tag.FOR:
            case Tag.IF:
            case ';':
            case ')':
            case '}':
            case ']':
            case Tag.EOF:
                break;
            default:
                error("Error in idlistp");
        }
    }
   public void bexpr(){
       if(look.tag == Tag.RELOP) {
           match(Tag.RELOP);
           expr();
           expr();
       } else {
           error("Syntax Error in bexpr");
       }
    }
   public void expr() {
       switch (look.tag) {
           case '+' -> {
               match('+');
               match('(');
               exprlist();
               match(')');
           }
           case '-' -> {
               match('-');
               expr();
               expr();
           }
           case '*' -> {
               match('*');
               match('(');
               exprlist();
               match(')');
           }
           case '/' -> {
               match('/');
               expr();
               expr();
           }
           case Tag.NUM -> match(Tag.NUM);
           case Tag.ID -> match(Tag.ID);
           default -> error("Syntax Error in expr");
       }
    }

   public void exprlist() {
        if(look.tag == '+' || look.tag == '-' || look.tag == '*' || look.tag == '/'
                || look.tag == Tag.NUM || look.tag == Tag.ID) {
            expr();
            exprlistp();
        } else {
            error("Syntax Error in exprlist");
        }
   }
   public void exprlistp() {
        switch(look.tag) {
            case ',':
                match(',');
                expr();
                exprlistp();
                break;
            case ')', Tag.EOF:
                break;
            default:
                error("Syntax Error in exprlistp");
        }
   }

   public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "D:\\IJ projects\\LFT\\src\\test_files\\test_parser.lft";
        try{
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser3_2 parser3_2 = new Parser3_2(lex, br);
            parser3_2.prog();
            System.out.println("Input OK");
            br.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
   }
}
