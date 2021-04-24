import java.io.IOException;

public interface IReader {
    char getCurrentChar();
    int getCurrentCharInt();
    boolean isEOF();
    boolean isEOL();
    boolean isCR();
    boolean isComment();
    boolean isSpace();
    void nextChar() throws IOException;
}
