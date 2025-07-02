package progetto_LFT;
import java.io.*;
import java.nio.Buffer;

public class Translator5_1 {
    private final Lexer lex;
    private final BufferedReader pbr;
    private Token look;

    final SymbolTable st = new SymbolTable();
    final CodeGenerator code = new CodeGenerator();

    int count = 0;
    public Translator5_1(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexicalScan(pbr);
        System.out.println("token = " + look);
    }
    void error(String s) {
        throw new Error ("near line " + lex.line + ": " + s);
    }
    void match(int t) {
        if(look.tag == t) {
            if(look.tag != Tag.EOF) {
                //System.out.println("Matched token: " + look); <- debug
                move();
            }
        } else {
            error("syntax error");
        }
    }
    public void prog() {
        switch(look.tag) {
            case Tag.ASSIGN, Tag.PRINT, Tag.READ, Tag.IF, Tag.FOR, '{' -> {
                int lnext_prog = code.newLabel();
                statlist(lnext_prog);
                code.emitLabel(lnext_prog);
                match(Tag.EOF);
                try{
                    code.toJasmin();
                } catch (IOException e) {
                    System.out.println("IO error\n");
                }
            }
            default -> error ("error in prog");
        }
    }

    public void statlist(int lnext_stat) {
        switch (look.tag) {
            case Tag.ASSIGN, Tag.PRINT, Tag.READ, Tag.IF, Tag.FOR, '{' -> {
                int lnext = code.newLabel();
                stat(lnext);
                code.emitLabel(lnext);
                statlistp(lnext_stat);
            }
            default -> error("error in statlist");
        }
    }

    public void statlistp(int lnext_stat) {
        switch(look.tag) {
            case ';':
                match(';');
                statlistp(lnext_stat);
                break;
            case Tag.ASSIGN, Tag.PRINT, Tag.READ, Tag.IF, Tag.FOR, '{':
                int lnext = code.newLabel();
                stat(lnext);
                code.emitLabel(lnext);
                statlistp(lnext_stat);
            case Tag.EOF, '}', Tag.END, Tag.ELSE:
                break;
            default: error ("syntax error in statlistp");
        }
    }

    //gestione di operazioni principali di tipo assign, print, read, for, if e {...}
    public void stat(int lnext_stat) {
        //System.out.println("In stat, current token = " + look); <- debug
        switch(look.tag) {
            case Tag.ASSIGN -> {
                match(Tag.ASSIGN);
                assignlist();
                code.emit(OpCode.GOto, lnext_stat);
            }
            case Tag.PRINT -> {
                match(Tag.PRINT);
                match('(');
                exprlist(1);
                match(')');
                code.emit(OpCode.GOto, lnext_stat);
            }
            case Tag.READ -> {
                match(Tag.READ);
                match('(');
                idlist(1);
                match(')');
                code.emit(OpCode.GOto, lnext_stat);
            }
            case Tag.FOR -> {
                int condL = code.newLabel();  // label condizione del ciclo for
                int bodyL = code.newLabel();  // label inizio corpo ciclo
                int bodyNext = code.newLabel(); // label fine del corpo, prima di tornare alla cond
                int endL = code.newLabel();   // label fine ciclo

                match(Tag.FOR);
                match('(');
                code.emitLabel(condL); //*
                forexpr(condL,bodyL, endL);
                match(')');
                match(Tag.DO);
                //se la condizione e vera ->
                code.emitLabel(bodyL);
                stat(bodyNext); //instruzioni dentro il ciclo for
                code.emitLabel(bodyNext);

                //torno alla condizione
                code.emit(OpCode.GOto, condL);

                //fine del ciclo for
                code.emitLabel(endL);
                code.emit(OpCode.GOto, lnext_stat); //prossimo statement
            }
            case Tag.IF -> {
                //labels per if statement
                int thenL = code.newLabel();
                int elseL = code.newLabel();
                int endL = code.newLabel();

                match(Tag.IF);
                match('(');
                bexpr(thenL, elseL);
                match(')');

                // la parte di then dell'if, quindi se la condizione e vera
                code.emitLabel(thenL);
                int lnext_then = code.newLabel();
                statlist(lnext_then); //uso statlist al posto di stat nel caso c'e' piu' di uno statement nel caso di if, anche senza {}
                code.emitLabel(lnext_then);
                code.emit(OpCode.GOto, endL);

                // la parte di else dell'if, quindi se la condizione e falsa
                code.emitLabel(elseL);
                statelse(endL);
                code.emitLabel(endL);
                code.emit(OpCode.GOto, lnext_stat);
            }
            case '{' -> {
                match('{');
                statlist(lnext_stat);
                match('}');
                code.emit(OpCode.GOto, lnext_stat);
            }
            default -> {
                error("error in stat");
            }
        }
    }

    //metodo di supporto per il caso for
    public void forexpr(int condL, int trueL, int falseL) {
        if(look.tag == Tag.ID) { //verifico tipo di token
            String var = ((Word)look).lexeme; //estraggo la stringa associata al token
            match(Tag.ID);
            if(look.tag == Tag.INIT) {
                match(Tag.INIT);
                expr(0);
                int addr = st.lookupAddress(var);
                if(addr < 0) { //se la variabile non e presente nella stack la aggiungiamo
                    addr = count;
                    st.insert(var, count++);
                }
                code.emit(OpCode.istore, addr); //salva valore nello slot addr
                match(';');
                code.emitLabel(condL);
                bexpr(trueL, falseL);
            } else {
                error ("expected ':=' in for");
            }
        } else if (look.tag == Tag.RELOP) { //nel caso ci fosse direttamente un operatore relazionale!
            bexpr(trueL, falseL);
        } else {
            error("error in forexpr");
        }
    }

//metodo di supporto per il caso if-else
    public void statelse(int endL) {
        if(look.tag == Tag.ELSE) { //controllo tipo di token
            match(Tag.ELSE);
            int lnext = code.newLabel(); //creo una label per l'else
            statlist(lnext); //analizza il blocco di istruzioni presenti nel ramo else
            code.emitLabel(lnext);
            match(Tag.END);
        } else if (look.tag == Tag.END) {
            match(Tag.END);
        } else {
            error ("error in statelse");
        }
    }

    //gestione delle assegnazioni con TO
    public void assignlist() {
        if(look.tag == '[') {
            match('[');
            expr(0);
            match(Tag.TO);
            idlist(0);
            match(']');
            assignlistp();
        } else {
            error("error in assignlist");
        }
    }
    public void assignlistp() {
        switch (look.tag) {
            case '[' -> {
                match('[');
                expr(0);
                match(Tag.TO);
                idlist(0);
                match(']');
                assignlistp();
                break;
            }
            case ';', Tag.EOF, Tag.ELSE, Tag.END -> {
                break;
            }
            default -> error ("error in assignlistp");
        }
    }

    //gestione di una lista di ID separati da virgole
    public void idlist(int read_store) {
        if(look.tag == Tag.ID) {
            //verifichiamo se la variabile e gia presente sulla stack con lookup
            //se no, la allochiamo
            String name = ((Word)look).lexeme; //estraggo la stringa associata al token
            int addr = st.lookupAddress(name); //verifico se la variabile e' gia dichiarata
            if(addr < 0) { //se non, associamo uno slot
                addr = count;
                st.insert(name, count++);
                if (read_store == 0) { //caso di assign
                    code.emit(OpCode.ldc, 0); //inizializzo var nuova
                    code.emit(OpCode.istore, addr);
                }
            }
            match(Tag.ID);
            //gestisco i casi di read o store in base al contesto
            if(read_store == 1) {
                code.emit(OpCode.invokestatic, 0);
                code.emit(OpCode.istore, addr);
            } else {
                code.emit(OpCode.istore, addr);
            }
            idlistp(read_store);
        } else {
            error ("error in idlist");
        }
    }
    public void idlistp(int read_store) {
        switch (look.tag) {
            case ',' -> {
                match(',');
                String name = ((Word)look).lexeme;
                int addr = st.lookupAddress(name);
                if(addr < 0) {
                    addr = count;
                    st.insert(name, count++);
                    if (read_store == 0) {
                        code.emit(OpCode.ldc, 0);
                        code.emit(OpCode.istore, addr);
                    }
                }
                match(Tag.ID);

                if(read_store == 1) {
                    code.emit(OpCode.invokestatic, 0);
                } else {
                    code.emit(OpCode.istore, addr);
                }
                idlistp(read_store);
                break;
            }
            case Tag.FOR, Tag.IF, ';', ')', '}', ']', Tag.EOF -> {
                break;
            }
            default -> error("error in idlistp");
        }
    }

    //gestione degli operatori relazionali
    public void bexpr(int trueL, int falseL) {
        if(look.tag == Tag.RELOP) {
            String relop = ((Word)look).lexeme;
            match(Tag.RELOP);
            expr(0);
            expr(0);
            switch (relop) {
                case "<" -> code.emit(OpCode.if_icmplt, trueL);
                case ">" -> code.emit(OpCode.if_icmpgt, trueL);
                case "<=" -> code.emit(OpCode.if_icmple, trueL);
                case ">=" -> code.emit(OpCode.if_icmpge, trueL);
                case "==" -> code.emit(OpCode.if_icmpeq, trueL);
                case "<>" -> code.emit(OpCode.if_icmpne, trueL);
                default -> {
                    error ("error in relop ");
                }
            }
        } else {
            error ("error in bexpr");
        }
        code.emit(OpCode.GOto,falseL);
    }

    //gestisce i tipi delle operazioni
    public void expr(int opType) {
        switch (look.tag) {
            case '+' -> {
                match('+');
                match('(');
                exprlist(0);
                match(')');
            }
            case '-' -> {
                match('-');
                expr(0);
                expr(0);
                code.emit(OpCode.isub);
            }
            case '*' -> {
                match('*');
                match('(');
                exprlist(2);
                match(')');
            }
            case '/' -> {
                match('/');
                expr(0);
                expr(0);
                code.emit(OpCode.idiv);
            }
            case Tag.NUM -> {
                code.emit(OpCode.ldc, Integer.parseInt(((NumberTok)look).lexeme));
                match(Tag.NUM);
            }
            case Tag.ID -> {
                String name = ((Word)look).lexeme;
                int addr = st.lookupAddress(name);
                if(addr < 0) {
                    addr = count;
                    st.insert(name, count++);
                    code.emit(OpCode.ldc, 0);
                    code.emit(OpCode.istore, addr);
                }
                code.emit(OpCode.iload, addr);
                match(Tag.ID);
            }
            default -> error("error in expr");
        }
    }

    //gestiscono una lista di espressioni separate da virgole
    public void exprlist(int opType) {
        switch (look.tag) {
            case '+', '-', '*', '/', Tag.NUM, Tag.ID -> {
                expr(opType); //valutazione della prima espressione
                exprlistp(opType); //gestione di eventuali espressione successive

                if(opType == 1) {
                    code.emit(OpCode.invokestatic, 1); //stampa il valore calcolato, se opType == 1
                }
            }
            default -> error("error in exprlist");
        }
    }
    public void exprlistp(int opType) {
        switch(look.tag) {
            case ',':
                match(',');
                expr(opType); //valuta espressione successiva
                if (opType == 0) {
                    code.emit(OpCode.iadd); //somma risultato dell'espr corrente con il risultato della precedente
                } else if (opType == 2) {
                    code.emit(OpCode.imul); //idem ma con la moltiplicazione
                }
                exprlistp(opType);
                break;
            case ')', Tag.EOF: //fine lista
                break;
            default:
                error("error in exprlistp");
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "D:\\IJ projects\\LFT\\src\\test_files\\input.lft";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator5_1 tr = new Translator5_1(lex, br);
            tr.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
