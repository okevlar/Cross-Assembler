/**
 * SOEN 341    W21
 * GAMESTOP - GROUP 13
 * Filename: GameStopReader.java
 * Creation: 2021-02-24
 */
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

public class GameStopReader {

    public static void main (String[] args) throws FileNotFoundException, IOException {
        String fileName = "src/TestInherentMnemonics.asm" ;
        File file = new File(fileName);

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            int singleCharint;
            char singlechar;

            while((singleCharint = fileInputStream.read()) != -1) {
                singlechar = (char) singleCharint;
                System.out.print(singlechar);
            }
        }
    }
}