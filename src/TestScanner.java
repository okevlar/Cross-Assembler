public class TestScanner {
    public static void main(String[] args) throws Exception {
        //Declaring two scanners to scan through two different classes
        Scanner sc1=new Scanner("src/TestImmediate.asm");
        Scanner sc2=new Scanner("src/TestInherentMnemonics.asm");
        sc1.nextToken();
        sc1.nextToken();
        sc1.nextToken();
        sc2.nextToken();
        System.out.println("Test Scanner");
        System.out.println("sc1[[enter.u5(3,2)=null],sc2[[halt(1,3)=null]]");
        System.out.println("sc1["+sc1.getToken()+","+"sc2["+sc2.getToken()+"]");
       


        
    }
    
}
