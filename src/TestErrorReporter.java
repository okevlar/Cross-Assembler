public class TestErrorReporter {
    public static void main(String[] args) {
        Position p1=new Position(1, 1);
        ErrorReporter er= new ErrorReporter();
        er.addError(p1, "erreur");
        System.out.println("Test ErrorReporter");
        System.out.println("Error at position (1,1): erreur");
        er.printAllErrors();

    }
    
}
