import java.io.IOException;

public interface ICodeGenerator {
    void GenerateListingFile(Queue<LineStatement> IR, ST<String, Mnemonic> lookUpTable, ST<String, String> symbolTable, String outputFileName);
    void VerboseMode(Queue<LineStatement> IR, ST<String, Mnemonic> lookUpTable, ST<String, String> symbolTable, String outputFileName);
    void GenerateExecutableFile(String outputFileName) throws IOException; // to be done in next sprint
}
