public class TestParser {
    public static void main(String[] args) {
        ST <String,String> st=new ST<>();
        ST <String,String> st2=new ST<>();
        Position p= new Position(1, 1);
        ErrorReporter er=new ErrorReporter();
        er.addError(p, "help");
        st.put("halt","good");
        IParser p1=new Parser(st,"file",er, st2);

        System.out.println("Test Parser");
        System.out.println("p1[good,file]");
        System.out.println("p1["+st.get("halt")+","+p1.getFile()+"]");
    }
}
