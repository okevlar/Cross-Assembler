import java.io.IOException;

public class TestReader {
    public static void main(String[] args) throws IOException {

        Reader r1 = new Reader("src/TestInherentMnemonicsReaderTest.asm");
        Reader r2 = new Reader("src/TestImmediate.asm");
        r1.nextChar();
        r1.nextChar();
        r2.nextChar();
        r2.nextChar();
        r2.nextChar();
        System.out.println("Test Reader");
        System.out.println("r1[h],r2[T]"); 
        System.out.println("r1["+r1.getCurrentChar()+"]"+",r2["+r2.getCurrentChar()+"]");
        }
    }

