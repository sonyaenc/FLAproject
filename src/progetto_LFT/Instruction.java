package progetto_LFT;

public class Instruction {
    OpCode opCode;
    int operand;

    public Instruction(OpCode opCd) {
        this.opCode = opCd;
    }
    public Instruction(OpCode opCd, int opr) {
        this.opCode = opCd;
        this.operand = opr;
    }
    public String toJasmin () {
       String temp = "";
       switch (opCode) {
           case ldc : temp = " ldc " + operand + "\n";
           break;
           case invokestatic:
               if(operand == 1) {
                   temp = "invokestatic" + " Output/print(I)V\n";
               } else {
                   temp = "invokestatic" + " Output/read()I\n";
               }
               break;
           case iadd: temp = " iadd " + "\n"; break;
           case imul: temp = " imul " + "\n"; break;
           case idiv: temp = " idiv " + "\n"; break;
           case isub: temp = " isub " + "\n"; break;
           case ineg: temp = " ineg " + "\n"; break;
           case istore: temp = " istore " + operand + "\n"; break;
           case ior: temp = " ior " + "\n"; break;
           case iand: temp = " iand " + "\n"; break;
           case iload: temp = " iload " + operand + "\n"; break;
           case if_icmpeq: temp = " if_icmpeq L" + operand + "\n"; break;
           case if_icmple: temp = " if_icmple L" + operand + "\n"; break;
           case if_icmplt: temp = " if_icmplt L" + operand + "\n"; break;
           case if_icmpne: temp = " if_icmpne L" + operand + "\n"; break;
           case if_icmpge: temp = " if_icmpge L" + operand + "\n"; break;
           case if_icmpgt: temp = " if_icmpgt L" + operand + "\n"; break;
           case ifne: temp = " ifne L" + operand + "\n"; break;
           case GOto: temp = " goto L" + operand + "\n"; break;
           case dup: temp = " dup " + "\n"; break;
           case pop: temp = " pop " + "\n"; break;
           case label: temp = "L" + operand + ":\n"; break;

       }
       return temp;
    }
}
