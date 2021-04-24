public interface IErrorReporter {
    void addError(Position p, String message);
    void printAllErrors();
    boolean reporterHasErrors();
    int getNumberOfErrors();         // is necessary?
}