import java.io.IOException;

public interface IScanner {
    void nextToken() throws IOException;
    Token getToken();
    int getColumnNumb();
    boolean getIsNL();
    int getLineNumb();
    boolean getIsEOF();
}
