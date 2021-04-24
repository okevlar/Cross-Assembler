import javax.sound.sampled.Line;
import java.io.IOException;

public class CrossAssembler implements ICrossAssembler {

    // constructor for CA with associations
    // try to keep scanner independent of the parser
    ST<String,Mnemonic> lookUpTable;
    IErrorReporter ER;
    ICodeGenerator CG;
    IScanner scanner;

    ST<String, String> symbolTable;

    IParser parser;
    IIR ir;

    String FileName;
    boolean verboseOption, listingOption;

    public CrossAssembler(String FileName, boolean verboseOption, boolean listingOption) {
        lookUpTable = new ST<String, Mnemonic>();  // hide implementation
        {
            lookUpTable.put("halt", new Mnemonic("halt", 0x00));
            lookUpTable.put("pop", new Mnemonic("pop", 0x01));
            lookUpTable.put("dup", new Mnemonic("dup", 0x02));
            lookUpTable.put("exit", new Mnemonic("exit", 0x03));
            lookUpTable.put("ret", new Mnemonic("ret", 0x04));
            lookUpTable.put("not", new Mnemonic("not", 0x0C));
            lookUpTable.put("and", new Mnemonic("and", 0x0D));
            lookUpTable.put("or", new Mnemonic("or", 0x0E));
            lookUpTable.put("xor", new Mnemonic("xor", 0x0F));
            lookUpTable.put("neg", new Mnemonic("neg", 0x10));
            lookUpTable.put("inc", new Mnemonic("inc", 0x11));
            lookUpTable.put("dec", new Mnemonic("dec", 0x12));
            lookUpTable.put("add", new Mnemonic("add", 0x03));
            lookUpTable.put("sub", new Mnemonic("sub", 0x14));
            lookUpTable.put("mul", new Mnemonic("mul", 0x15));
            lookUpTable.put("div", new Mnemonic("div", 0x16));
            lookUpTable.put("rem", new Mnemonic("rem", 0x17));
            lookUpTable.put("shl", new Mnemonic("shl", 0x18));
            lookUpTable.put("shr", new Mnemonic("shr", 0x19));
            lookUpTable.put("teq", new Mnemonic("teq", 0x1A));
            lookUpTable.put("tne", new Mnemonic("tne", 0x1B));
            lookUpTable.put("tlt", new Mnemonic("tlt", 0x1C));
            lookUpTable.put("tgt", new Mnemonic("tgt", 0x1D));
            lookUpTable.put("tle", new Mnemonic("tle", 0x1E));
            lookUpTable.put("tge", new Mnemonic("tge", 0x1F));
            lookUpTable.put("enter.u5", new Mnemonic("enter", 0x70));
            lookUpTable.put("ldc.i3", new Mnemonic("ldc", 0x90));
            lookUpTable.put("addv.u3", new Mnemonic("addv", 0x98));
            lookUpTable.put("ldv.u3", new Mnemonic("ldv", 0xA0));
            lookUpTable.put("stv.u3", new Mnemonic("stv", 0xA8));
            lookUpTable.put(".cstring", new Mnemonic(".cstring", 0x00));
            lookUpTable.put("br.i8", new Mnemonic(".cstring", 0xE0));
            lookUpTable.put("brf.i8", new Mnemonic(".cstring", 0xE3));
            lookUpTable.put("ldc.i8", new Mnemonic(".cstring", 0xD9));
            lookUpTable.put("ldv.u8", new Mnemonic(".cstring", 0xB1));
            lookUpTable.put("stv.u8", new Mnemonic(".cstring", 0xB2));
            lookUpTable.put("lda.i16", new Mnemonic(".cstring", 0xD5));
        }

        ER = new ErrorReporter();
        CG = new CodeGenerator();
        symbolTable = new ST<String, String>();
        this.FileName = FileName;

        try {
            scanner = new Scanner(FileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        parser = new Parser(lookUpTable,FileName, (ErrorReporter) ER, symbolTable, (Scanner)scanner);
        ir = new IR();
        this.verboseOption = verboseOption;
        this.listingOption = listingOption;
    }

    public void run() {
        ir.setIR(parser.createLineStatement());

        System.out.println("\n  The parser found " + symbolTable.size() + " unique mnemonics and labels.\n\n");
        /*
        for(LineStatement line: IR) {
            System.out.println(line.toString());
        }
        FOR DEBUGGING: Looks at the content of the IR file
         */


        if (ER.reporterHasErrors()) {
            ER.printAllErrors();
        } else {
            if (listingOption == true) {
                String outputFileName = FileName.substring(0, FileName.indexOf('.'));
                outputFileName = outputFileName + "Generated.lst";
                CG.GenerateListingFile(ir.getIR(), lookUpTable, symbolTable, outputFileName);
            } else if (verboseOption == true) {
                String outputFileName = FileName.substring(0, FileName.indexOf('.'));
                outputFileName = outputFileName + "Generated.lst";
                CG.VerboseMode(ir.getIR(), lookUpTable, symbolTable, outputFileName);
            }
            String outputFileName = FileName.substring(0, FileName.indexOf('.'));
            outputFileName = outputFileName + ".exe";
            try {
                CG.GenerateExecutableFile(outputFileName);
            } catch (IOException err) {
                err.printStackTrace();
            }
        }

        /*
        Instruction a1 = new Instruction(new Mnemonic("halt"));
        Instruction a2 = new Instruction(new Mnemonic("pop"));
        Instruction a3 = new Instruction(new Mnemonic("do"));
        Instruction a4 = new Instruction(new Mnemonic("u"));
        Instruction a5 = new Instruction(new Mnemonic("enter"));
        Instruction a6 = new Instruction(new Mnemonic("and"));
        Instruction a7 = new Instruction(new Mnemonic("work"));
        Instruction a8 = new Instruction(new Mnemonic("halt"));
        //System.out.println(a1+" "+a2+" "+a3+" "+a4+" "+a5+" "+a6+" "+a7+" "+a8);
        USED FOR DEBUGGING ONLY.
         */
    }
}
