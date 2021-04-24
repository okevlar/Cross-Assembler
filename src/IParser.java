import java.io.IOException;

public interface IParser {
    String getFile();
    Queue<LineStatement> createLineStatement();
    boolean VerifyFormatAndOperands(LineStatement line);
}
