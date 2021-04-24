public class TestMnemonic {
    public static void main(String[] args) {
        Mnemonic m1 =new Mnemonic("joy",1);
        System.out.println("Test Mnemonic");

        System.out.println("m1[joy,1]");
        System.out.println("m1["+m1.getName()+","+m1.getOpcodeCode()+"]");
        
    }
}
