public class TestLineStatement {
    public static void main(String[] args){
        Queue <String> q = new Queue<>();
        q.enqueue("1");
        Mnemonic mne=new Mnemonic("add",1);
        Instruction instruction= new Instruction(mne,q);
        LineStatement L1=new LineStatement(instruction,"label","comment");

        System.out.println("Test Line Statement");
        System.out.println("L1[mnemonic name add,label,comment]");

        System.out.print("L1["+L1.getInstruction()+","+L1.getLabel()+","+L1.getComment()+"]");

    }
}
