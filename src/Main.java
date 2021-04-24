import java.io.File;

public class Main {
    public static void main(String args[]) {

        boolean helpOption = false;
        boolean verboseOption = false;
        boolean listingOption = false;
        String FileName = "src/TestImmediate.asm"; //placeholder so we can run it using IntelliJ, without the command line

        if (args.length > 0) {
            for (String option : args) {
                if (option.equalsIgnoreCase("-h") || option.equalsIgnoreCase("--help")) {
                    helpOption = true;
                } else if (option.equalsIgnoreCase("-v") || option.equalsIgnoreCase("--verbose")) {
                    verboseOption = true;
                } else if (option.equalsIgnoreCase("-l") || option.equalsIgnoreCase("--listing")) {
                    listingOption = true;
                } else {
                    FileName = option;
                }
            }
        }

        File file = new File(FileName);
        if (!file.exists()) {
            System.out.println("\n  Error: File not found!");
            helpOption = true;
        }

        if (helpOption == true) {
            System.out.println("\nUSAGE: java Main [-h] [-v] [-l] <source file>");
            System.out.println("It is mandatory to specify a source file.");
            System.out.println("Options indicated inside brackets [] are optional. To enable them, include them in the command, but without the brackets.");
            System.out.println("[-h] or [--help]: The help option. Disables the functionality of the program to show the help dialogue.");
            System.out.println("[-v] or [--verbose]: The verbose option. Produces a source listing and the after pass 1 and a label table.");
            System.out.println("[-l] or [--listing]: The listing option. Produces a complete source listing of all virtual code.");
            System.out.println("<source file>: May be relative or the complete file path. Must be a .asm source file.\n");
        } else {
            ICrossAssembler CA = new CrossAssembler(FileName, verboseOption, listingOption);
            CA.run();
        }



    }
}
