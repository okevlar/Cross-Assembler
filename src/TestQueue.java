public class TestQueue {
    public static void main(String[] args) {
        Queue <String> q = new Queue<>();
        q.enqueue("software");
        q.enqueue("Engineering");
        q.dequeue();
        System.out.println("Test Queue");

        System.out.println("q[Engineering]");
        System.out.println("q["+q.toString()+"]");

    }
    
}
