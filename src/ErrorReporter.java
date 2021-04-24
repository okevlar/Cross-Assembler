public class ErrorReporter implements IErrorReporter{

    private Queue<CrossAssemblerError> queue;

    public ErrorReporter() {
        queue = new Queue<CrossAssemblerError>();
    }

    public void addError(Position p, String message)
    {
        CrossAssemblerError e = new CrossAssemblerError(p, message);
        queue.enqueue(e);
    }

    public boolean reporterHasErrors() {
        return queue.size() != 0;
    }

    public int getNumberOfErrors() {
        return queue.size();
    }

    public void printAllErrors() {
        for (CrossAssemblerError e : queue) {
            System.out.println(e.toString());
        }
    }
}
