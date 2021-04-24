public class TestST {
    public static void main(String[] args) {
        ST<String,String> st =new ST<>();
        st.put("key", "value");
        st.put("Hello","World");
        st.delete("Hello");

        System.out.println("Test ST");
        System.out.println("st[value]");
        System.out.println("st["+st.get("key")+"]");

    }
}
