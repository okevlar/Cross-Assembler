/**
 * SOEN 341    W21
 * GAMESTOP - GROUP 13
 * Filename: Main.java
 * Creation: 2021-02-24
 */

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.io.FileWriter;

public class Main_OLD {
	public static void main(String[] args) throws IOException {
        // [2]: Read the assembly source file (assume the file is validated and exists).
        // change "inputFileName" to your path
        String inputFileName = "src/TestInherentMnemonics.asm";

        // [3] Scan characters to extract and create tokens.
        // (a) Create a symbol table (for labels and mnemonics).
        ST lookUpTable = new ST<String, Mnemonic>();

        // (b) Enter mnemonics (keywords) in the symbol table.
        lookUpTable.put("halt", new Mnemonic("halt", 0x00));
        lookUpTable.put("pop", new Mnemonic("pop", 0x01));
        lookUpTable.put("dup", new Mnemonic("dup", 0x02));
        lookUpTable.put("exit", new Mnemonic("exit", 0x03));
        lookUpTable.put("ret", new Mnemonic("ret", 0x04));
        // 04 - 0B Reserved for future use.
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


        Queue<Token> tokenQueue = new Queue<Token>();
        //Scanner Test1 = new Scanner(inputFileName, lookUpTable);
        //Test1.PopulateTokenList(tokenQueue);
        //tokenQueue now contains all the token that were read in the input file in order (from first read to lasat read)


        int line = 1;
        BigInteger addressInt = new BigInteger("0", 2);
        String listingFile = "";

        /*
                We have a header class
         */
        listingFile = listingFile + ("Line Addr Code \t\t\tLabel \t\t  Mne   Operand \t\t Comments \t\t \n"); //header of the listing file

        for (Token token : tokenQueue) { //parse through the IR
                String lineString = "" + line;

                String address = addressInt.toString(16).toUpperCase();
                if (address.length() < 4)
                        address = "000".substring(address.length() - 1) + address; //padding out the address with heading 0's

                String lineSpaces = " ";
                for (int i = lineString.length(); i < 4; i++) {
                        lineSpaces = lineSpaces + " "; //spaces for formatting
                }

                String mneSpaces = " ";
                for (int i = token.getName().length(); i < 4; i++) {
                        mneSpaces = mneSpaces + " "; //spaces for formatting
                }

                listingFile = listingFile + (line + lineSpaces + address + " " + String.format("%02X", ((Mnemonic)lookUpTable.get(token.getName())).getOpcodeCode()) + "\t\t\t       \t\t  " + token.getName() + mneSpaces + "  \t \t \t \t \t\t\t\n");
                line++;
                addressInt = addressInt.add(BigInteger.ONE);
        }

        System.out.println(listingFile);

        try{
                File dotLst = new File("src/TestInherentMnemonicsGenerated.lst");

                if (dotLst.createNewFile()) {
                        FileWriter writer = new FileWriter("src/TestInherentMnemonicsGenerated.lst");
                        writer.write(listingFile);
                        writer.close();
                        System.out.println("File created: " + dotLst.getName());
                } else {
                        dotLst.delete();
                        dotLst.createNewFile();
                        FileWriter writer = new FileWriter("src/TestInherentMnemonicsGenerated.lst");
                        writer.write(listingFile);
                        writer.close();
                        System.out.println("File created: " + dotLst.getName());
                }
        } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
        }

    }
}

// Orion's push
