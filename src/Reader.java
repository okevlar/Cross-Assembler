import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Reader implements IReader{
    private String fileName;
    private FileInputStream fileInputStream;
    private char CurrentChar;
    private int CurrentCharInt;
    private boolean isEOF;//used for debugging
    private boolean isEOL;//used for debugging
    private boolean isCR;
    private boolean isSpace;
    private boolean isComment;

    Reader(String iFileName) throws IOException {
        File file = new File(iFileName);
        fileInputStream = new FileInputStream(file);
    }

    public char getCurrentChar() {
        return CurrentChar;
    }

    public int getCurrentCharInt() {
        return CurrentCharInt;
    }

    public boolean isEOF() {
        if (CurrentCharInt == -1 || CurrentChar == '\uFFFF') {
            isEOF = true;
            return true;
        } else {
            isEOF = false;
            return false;
        }
    }

    public boolean isEOL() {
        if (CurrentChar == '\n') {
            isEOL = true;
            return true;
        } else {
            isEOL = false;
            return false;
        }
    }

    public boolean isCR() {
        if (CurrentChar == '\r') {
            isCR = true;
            return true;
        } else {
            isCR = false;
            return false;
        }
    }

    public boolean isComment() {
        if (CurrentChar == ';') {
            isComment = true;
            return true;
        } else {
            isComment = false;
            return false;
        }
    }

    public boolean isSpace() {
        if (CurrentChar == ' ') {
            isSpace = true;
            return true;
        } else {
            isSpace = false;
            return false;
        }
    }

    public void nextChar() throws IOException {
        if (fileInputStream != null) {
            CurrentCharInt = fileInputStream.read();
            CurrentChar = (char) CurrentCharInt;
        }
    }

}
