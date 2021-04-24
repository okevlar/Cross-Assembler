public class TestCrossAssemblerError {
    public static void main(String[] args) {
        Position p1 =new Position(1,1);
        CrossAssemblerError e1=new CrossAssemblerError(p1,"Mistake");
    

        System.out.println("Test CrossAssemblerError");

        System.out.println("e1[(1,1),Mistake]");
        System.out.println("e1["+e1.getP()+","+e1.getMessage()+"]");
        
    }
}
