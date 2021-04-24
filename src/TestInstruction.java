public class TestInstruction {
    public static void main(String[] args) {
        Queue <String> q=new Queue<>();
        q.enqueue("add");
        Mnemonic m1=new Mnemonic("name",1);
        Instruction i1=new Instruction(m1,q);

    
        System.out.println("Test Instruction");

        System.out.println("i1[name,1,add]");
        //System.out.println("i1["+i1.getMnemonic()+","+i1.getOperand()+"]");
        System.out.println("i1["+m1.getName()+","+m1.getOpcodeCode()+","+i1.getOperands().toString()+"]");

    }
    
}
