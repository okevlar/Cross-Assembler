public class TestCodeGenerator {
    public static void main(String[] args) throws Exception {
        Queue <String> q=new Queue<>();
        q.enqueue("add");
        Mnemonic m=new Mnemonic("name",1);
        Instruction i= new Instruction(m,q);
        LineStatement L1=new LineStatement(i,"label","comment");
        Queue <LineStatement> q1=new Queue<>();
        q1.enqueue(L1);
        ST <String,Mnemonic> s=new ST<>();
        ST <String,String> st=new ST<>();
        s.put("hello", m);
        CodeGenerator c1=new CodeGenerator();
//Did not completely make it work only the header pass the test for now.
        
        System.out.println("Test CodeGenerator");
        System.out.println("Line Addr Machine Code \tLabel \t\t  Mne\t\tOperand\t\t\tComments \t\t \n");
        System.out.println("");
        c1.GenerateListingFile(q1,s, st,"code");
       
    }
    
}
