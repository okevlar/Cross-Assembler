import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;

public class CodeGenerator implements ICodeGenerator {

    ArrayList<String> machine;

    public CodeGenerator() {
        machine = new ArrayList<>();
    }

    public void GenerateListingFile(Queue<LineStatement> IR, ST<String, Mnemonic> lookUpTable, ST<String, String> symbolTable, String outputFileName) {
        int line = 1;
        BigInteger addressInt = new BigInteger("0", 2);
        String listingFile = "";
        listingFile = listingFile + ("Line Addr Machine Code\tLabel\t\tMne\t\tOperands\t\t\tComments\t\t\n"); //header of the listing file

        for (LineStatement lineStatement : IR) { //parse through the IR
            String lineString = "" + line;

            String address = addressInt.toString(16).toUpperCase();
            if (address.length() < 4)
                address = "000".substring(address.length() - 1) + address; //padding out the address with heading 0's

            String lineSpaces = " ";
            for (int i = lineString.length(); i < 4; i++) {
                lineSpaces = lineSpaces + " "; //spaces for formatting
            }

            listingFile = listingFile + (line + lineSpaces + address + " ");

            if (lineStatement.getInstruction() != null) {
                String machineCodeString = "";

                if (lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("br.i8") ||
                        lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("brf.i8") ||
                        lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("ldc.i8") ||
                        lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("ldv.u8") ||
                        lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("stv.u8") ||
                        lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("lda.i16")) {
                    //relative addressing
                    machineCodeString = Integer.toHexString(lookUpTable.get(lineStatement.getInstruction().getMnemonic().getName()).getOpcodeCode()).toUpperCase();
                    if (lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("ldv.u8") ||
                            lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("stv.u8") ||
                            lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("ldc.i8")) {
                        String operandString = Integer.toHexString(Integer.parseInt(lineStatement.getInstruction().getOperands().peek()));
                        machineCodeString = machineCodeString + " " + "0".substring(operandString.length() - 1) + operandString;
                    } else {
                        String relativeAddr = "";
                        if (lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("lda.i16")) {
                            relativeAddr = "00 ";
                        }
                        if (symbolTable.get(lineStatement.getInstruction().getOperands().peek()).equalsIgnoreCase("null") == false) {
                            int difference = Integer.parseInt(symbolTable.get(lineStatement.getInstruction().getOperands().peek())) - Integer.parseInt(address);
                            if (difference < 0) {
                                if (lineStatement.getInstruction().getMnemonic().getName().contains(".i8")) {
                                    difference = difference + 256;
                                }
                            }
                            relativeAddr = relativeAddr + Integer.toHexString(difference);
                        } else {
                            relativeAddr = relativeAddr + "??";
                        }

                        machineCodeString = machineCodeString + " " + relativeAddr;

                    }
                } else if (lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase(".cstring") == true) {
                    String operand = lineStatement.getInstruction().getOperands().peek();
                    operand = operand.replaceAll("\"", "");
                    for (int i = 0; i < operand.length(); i++) {
                        machineCodeString = machineCodeString + Integer.toHexString(operand.charAt(i)) + " ";
                    }
                    machineCodeString = machineCodeString + "00";
                } else if (lineStatement.getInstruction().getMnemonic().getName().contains(".") == true) { //inherent addressing
                    int immediateOffset = 0;
                    if (lineStatement.getInstruction().getMnemonic().getName().contains(".i3")) {
                        immediateOffset = Integer.parseInt(lineStatement.getInstruction().getOperands().peek()) + 4;
                    } else {
                        immediateOffset = Integer.parseInt(lineStatement.getInstruction().getOperands().peek());
                    }
                    int machineCoder = lookUpTable.get(lineStatement.getInstruction().getMnemonic().getName()).getOpcodeCode() + immediateOffset;
                    machineCodeString = "" + Integer.toHexString(machineCoder);
                } else {
                    machineCodeString = Integer.toHexString(lookUpTable.get(lineStatement.getInstruction().getMnemonic().getName()).getOpcodeCode());
                }

                //listingFile = listingFile + String.format("%02X", (lookUpTable.get(lineStatement.getInstruction().getMnemonic().getName())).getOpcodeCode());
                listingFile = listingFile + machineCodeString.toUpperCase();
            } else {
                listingFile = listingFile + "\t\t";
            }

            if (lineStatement.getLabel() != null) {
                symbolTable.delete(lineStatement.getLabel());
                symbolTable.put(lineStatement.getLabel(), address);
                if (lineStatement.getLabel().length() < 4) {
                    listingFile = listingFile + "\t\t" + lineStatement.getLabel() + "\t\t\t";
                } else {
                    listingFile = listingFile + "\t\t" + lineStatement.getLabel() + "\t\t";
                }
            } else {
                listingFile = listingFile + "\t\t\t" + "\t" + "\t\t";
            }

            if (lineStatement.getInstruction() != null) {
                listingFile = listingFile + lineStatement.getInstruction().getMnemonic().getName() + "\t";
                if (lineStatement.getInstruction().getOperands() != null) {
                    listingFile = listingFile + lineStatement.getInstruction().getOperands().toString();
                    if (lineStatement.getInstruction().getOperands().toString().length() < 4) {
                        listingFile = listingFile + "\t\t\t\t";
                    } else {
                        listingFile = listingFile + "\t\t\t";
                    }
                }
            } else {
                listingFile = listingFile + "\t\t\t\t";
            }

            if (lineStatement.getComment() != null) {
                listingFile = listingFile + lineStatement.getComment() + "\n";
            } else {
                listingFile = listingFile + "\n";
            }

            line++;
            //machine.add(machineCodeString.toUpperCase());

            if (lineStatement.getInstruction() != null) {
                if (lineStatement.getInstruction().getMnemonic().getName().contains(".i8") || lineStatement.getInstruction().getMnemonic().getName().contains(".u8")) {
                    addressInt = addressInt.add(BigInteger.ONE);
                    addressInt = addressInt.add(BigInteger.ONE); //add 2 to address
                } else if (lineStatement.getInstruction().getMnemonic().getName().contains(".i16")) {
                    addressInt = addressInt.add(BigInteger.ONE);
                    addressInt = addressInt.add(BigInteger.ONE);
                    addressInt = addressInt.add(BigInteger.ONE); //add 3 to address
                } else if (lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase(".cstring")){
                    for (int j = 0; j < lineStatement.getInstruction().getOperands().peek().length() - 2 + 1; j++) {
                        addressInt = addressInt.add(BigInteger.ONE); //add length of string to address plus one
                    }
                } else {
                    addressInt = addressInt.add(BigInteger.ONE); //add 1 in all other cases
                }
            }
        }

        line = 1;
        addressInt = null;
        addressInt = new BigInteger("0", 2);
        listingFile = "";
        listingFile = listingFile + ("Line Addr Machine Code\tLabel\t\tMne\t\tOperands\t\t\tComments\t\t\n"); //header of the listing file

        for (LineStatement lineStatement : IR) { //parse through the IR
            String lineString = "" + line;

            String address = addressInt.toString(16).toUpperCase();
            if (address.length() < 4)
                address = "000".substring(address.length() - 1) + address; //padding out the address with heading 0's

            String lineSpaces = " ";
            for (int i = lineString.length(); i < 4; i++) {
                lineSpaces = lineSpaces + " "; //spaces for formatting
            }

            listingFile = listingFile + (line + lineSpaces + address + " ");

            if (lineStatement.getInstruction() != null) {
                String machineCodeString = "";

                if (lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("br.i8") ||
                        lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("brf.i8") ||
                        lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("ldc.i8") ||
                        lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("ldv.u8") ||
                        lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("stv.u8") ||
                        lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("lda.i16")) {
                    //relative addressing
                    machineCodeString = Integer.toHexString(lookUpTable.get(lineStatement.getInstruction().getMnemonic().getName()).getOpcodeCode()).toUpperCase();
                    if (lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("ldv.u8") ||
                            lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("stv.u8") ||
                            lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("ldc.i8")) {
                        String operandString = Integer.toHexString(Integer.parseInt(lineStatement.getInstruction().getOperands().peek()));
                        machineCodeString = machineCodeString + " " + "0".substring(operandString.length() - 1) + operandString;
                    } else {
                        String relativeAddr = "";
                        if (lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("lda.i16")) {
                            relativeAddr = "00 ";
                        }
                        if (symbolTable.get(lineStatement.getInstruction().getOperands().peek()).equalsIgnoreCase("null") == false) {
                            int difference = Integer.parseInt(symbolTable.get(lineStatement.getInstruction().getOperands().peek()), 16) - Integer.parseInt(address,16);
                            if (difference < 0) {
                                if (lineStatement.getInstruction().getMnemonic().getName().contains(".i8")) {
                                    difference = difference + 256;
                                }
                            }
                            relativeAddr = relativeAddr + "0".substring(Integer.toHexString(difference).length() - 1) + Integer.toHexString(difference);
                        } else {
                            relativeAddr = relativeAddr + "??";
                        }

                        machineCodeString = machineCodeString + " " + relativeAddr;

                    }
                } else if (lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase(".cstring") == true) {
                    String operand = lineStatement.getInstruction().getOperands().peek();
                    operand = operand.replaceAll("\"", "");
                    for (int i = 0; i < operand.length(); i++) {
                        machineCodeString = machineCodeString + Integer.toHexString(operand.charAt(i)) + " ";
                    }
                    machineCodeString = machineCodeString + "00";
                } else if (lineStatement.getInstruction().getMnemonic().getName().contains(".") == true) { //inherent addressing
                    int immediateOffset = 0;
                    if (lineStatement.getInstruction().getMnemonic().getName().contains(".i3")) {
                        immediateOffset = Integer.parseInt(lineStatement.getInstruction().getOperands().peek()) + 4;
                    } else {
                        immediateOffset = Integer.parseInt(lineStatement.getInstruction().getOperands().peek());
                    }
                    int machineCoder = lookUpTable.get(lineStatement.getInstruction().getMnemonic().getName()).getOpcodeCode() + immediateOffset;
                    machineCodeString = "" + Integer.toHexString(machineCoder);
                } else {
                    machineCodeString = Integer.toHexString(lookUpTable.get(lineStatement.getInstruction().getMnemonic().getName()).getOpcodeCode());
                }

                listingFile = listingFile + machineCodeString.toUpperCase();
                machine.add(machineCodeString.toUpperCase());
            } else {
                listingFile = listingFile + "\t\t";
            }

            if (lineStatement.getLabel() != null) {
                symbolTable.delete(lineStatement.getLabel());
                symbolTable.put(lineStatement.getLabel(), address);
                if (lineStatement.getLabel().length() < 4) {
                    listingFile = listingFile + "\t\t" + lineStatement.getLabel() + "\t\t\t";
                } else {
                    listingFile = listingFile + "\t\t" + lineStatement.getLabel() + "\t\t";
                }
            } else {
                listingFile = listingFile + "\t\t\t" + "\t" + "\t\t";
            }

            if (lineStatement.getInstruction() != null) {
                listingFile = listingFile + lineStatement.getInstruction().getMnemonic().getName() + "\t";
                if (lineStatement.getInstruction().getOperands() != null) {
                    listingFile = listingFile + lineStatement.getInstruction().getOperands().toString();
                    if (lineStatement.getInstruction().getOperands().toString().length() < 4) {
                        listingFile = listingFile + "\t\t\t\t";
                    } else {
                        listingFile = listingFile + "\t\t\t";
                    }
                }
            } else {
                listingFile = listingFile + "\t\t\t\t";
            }

            if (lineStatement.getComment() != null) {
                listingFile = listingFile + lineStatement.getComment() + "\n";
            } else {
                listingFile = listingFile + "\n";
            }

            line++;

            if (lineStatement.getInstruction() != null) {
                if (lineStatement.getInstruction().getMnemonic().getName().contains(".i8") || lineStatement.getInstruction().getMnemonic().getName().contains(".u8")) {
                    addressInt = addressInt.add(BigInteger.ONE);
                    addressInt = addressInt.add(BigInteger.ONE); //add 2 to address
                } else if (lineStatement.getInstruction().getMnemonic().getName().contains(".i16")) {
                    addressInt = addressInt.add(BigInteger.ONE);
                    addressInt = addressInt.add(BigInteger.ONE);
                    addressInt = addressInt.add(BigInteger.ONE); //add 3 to address
                } else if (lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase(".cstring")){
                    for (int j = 0; j < lineStatement.getInstruction().getOperands().peek().length() - 2 + 1; j++) {
                        addressInt = addressInt.add(BigInteger.ONE); //add length of string to address plus one
                    }
                } else {
                    addressInt = addressInt.add(BigInteger.ONE); //add 1 in all other cases
                }
            }
        }

        System.out.println(listingFile);

        try {
            File dotLst = new File("" + outputFileName);

            if (dotLst.createNewFile()) {
                FileWriter writer = new FileWriter(""+ outputFileName);
                writer.write(listingFile);
                writer.close();
                System.out.println("File created: " + dotLst.getName());
            } else {
                dotLst.delete();
                dotLst.createNewFile();
                FileWriter writer = new FileWriter(""+ outputFileName);
                writer.write(listingFile);
                writer.close();
                System.out.println("File created: " + dotLst.getName());
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void VerboseMode(Queue<LineStatement> IR, ST<String, Mnemonic> lookUpTable, ST<String, String> symbolTable, String outputFileName) {
        int line = 1;
        BigInteger addressInt = new BigInteger("0", 2);
        String listingFile = "";
        listingFile = listingFile + ("Line Addr Machine Code\tLabel\t\tMne\t\tOperands\t\t\tComments\t\t\n"); //header of the listing file

        for (LineStatement lineStatement : IR) { //parse through the IR
            String lineString = "" + line;

            String address = addressInt.toString(16).toUpperCase();
            if (address.length() < 4)
                address = "000".substring(address.length() - 1) + address; //padding out the address with heading 0's

            String lineSpaces = " ";
            for (int i = lineString.length(); i < 4; i++) {
                lineSpaces = lineSpaces + " "; //spaces for formatting
            }

            listingFile = listingFile + (line + lineSpaces + address + " ");

            if (lineStatement.getInstruction() != null) {
                String machineCodeString = "";

                if (lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("br.i8") ||
                        lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("brf.i8") ||
                        lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("ldc.i8") ||
                        lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("ldv.u8") ||
                        lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("stv.u8") ||
                        lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("lda.i16")) {
                    //relative addressing
                    machineCodeString = Integer.toHexString(lookUpTable.get(lineStatement.getInstruction().getMnemonic().getName()).getOpcodeCode()).toUpperCase();
                    if (lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("ldv.u8") ||
                            lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("stv.u8") ||
                            lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("ldc.i8")) {
                        String operandString = Integer.toHexString(Integer.parseInt(lineStatement.getInstruction().getOperands().peek()));
                        machineCodeString = machineCodeString + " " + "0".substring(operandString.length() - 1) + operandString;
                    } else {
                        String relativeAddr = "";
                        if (lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("lda.i16")) {
                            relativeAddr = "00 ";
                        }
                        if (symbolTable.get(lineStatement.getInstruction().getOperands().peek()).equalsIgnoreCase("null") == false) {
                            int difference = Integer.parseInt(symbolTable.get(lineStatement.getInstruction().getOperands().peek())) - Integer.parseInt(address);
                            if (difference < 0) {
                                if (lineStatement.getInstruction().getMnemonic().getName().contains(".i8")) {
                                    difference = difference + 256;
                                }
                            }
                            relativeAddr = relativeAddr + Integer.toHexString(difference);
                        } else {
                            relativeAddr = relativeAddr + "??";
                        }

                        machineCodeString = machineCodeString + " " + relativeAddr;

                    }
                } else if (lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase(".cstring") == true) {
                    String operand = lineStatement.getInstruction().getOperands().peek();
                    operand = operand.replaceAll("\"", "");
                    for (int i = 0; i < operand.length(); i++) {
                        machineCodeString = machineCodeString + Integer.toHexString(operand.charAt(i)) + " ";
                    }
                    machineCodeString = machineCodeString + "00";
                    } else if (lineStatement.getInstruction().getMnemonic().getName().contains(".") == true) { //inherent addressing
                    int immediateOffset = 0;
                    if (lineStatement.getInstruction().getMnemonic().getName().contains(".i3")) {
                        immediateOffset = Integer.parseInt(lineStatement.getInstruction().getOperands().peek()) + 4;
                    } else {
                        immediateOffset = Integer.parseInt(lineStatement.getInstruction().getOperands().peek());
                    }
                    int machineCoder = lookUpTable.get(lineStatement.getInstruction().getMnemonic().getName()).getOpcodeCode() + immediateOffset;
                    machineCodeString = "" + Integer.toHexString(machineCoder);
                } else {
                    machineCodeString = Integer.toHexString(lookUpTable.get(lineStatement.getInstruction().getMnemonic().getName()).getOpcodeCode());
                }

                //listingFile = listingFile + String.format("%02X", (lookUpTable.get(lineStatement.getInstruction().getMnemonic().getName())).getOpcodeCode());
                listingFile = listingFile + machineCodeString.toUpperCase();
            } else {
                listingFile = listingFile + "\t\t";
            }

            if (lineStatement.getLabel() != null) {
                symbolTable.delete(lineStatement.getLabel());
                symbolTable.put(lineStatement.getLabel(), address);
                if (lineStatement.getLabel().length() < 4) {
                    listingFile = listingFile + "\t\t" + lineStatement.getLabel() + "\t\t\t";
                } else {
                    listingFile = listingFile + "\t\t" + lineStatement.getLabel() + "\t\t";
                }
            } else {
                listingFile = listingFile + "\t\t\t" + "\t" + "\t\t";
            }

            if (lineStatement.getInstruction() != null) {
                listingFile = listingFile + lineStatement.getInstruction().getMnemonic().getName() + "\t";
                if (lineStatement.getInstruction().getOperands() != null) {
                    listingFile = listingFile + lineStatement.getInstruction().getOperands().toString();
                    if (lineStatement.getInstruction().getOperands().toString().length() < 4) {
                        listingFile = listingFile + "\t\t\t\t";
                    } else {
                        listingFile = listingFile + "\t\t\t";
                    }
                }
            } else {
                listingFile = listingFile + "\t\t\t\t";
            }

            if (lineStatement.getComment() != null) {
                listingFile = listingFile + lineStatement.getComment() + "\n";
            } else {
                listingFile = listingFile + "\n";
            }

            line++;
            //machine.add(machineCodeString.toUpperCase());

            if (lineStatement.getInstruction() != null) {
                if (lineStatement.getInstruction().getMnemonic().getName().contains(".i8") || lineStatement.getInstruction().getMnemonic().getName().contains(".u8")) {
                    addressInt = addressInt.add(BigInteger.ONE);
                    addressInt = addressInt.add(BigInteger.ONE); //add 2 to address
                } else if (lineStatement.getInstruction().getMnemonic().getName().contains(".i16")) {
                    addressInt = addressInt.add(BigInteger.ONE);
                    addressInt = addressInt.add(BigInteger.ONE);
                    addressInt = addressInt.add(BigInteger.ONE); //add 3 to address
                } else if (lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase(".cstring")){
                    for (int j = 0; j < lineStatement.getInstruction().getOperands().peek().length() - 2 + 1; j++) {
                        addressInt = addressInt.add(BigInteger.ONE); //add length of string to address plus one
                    }
                } else {
                    addressInt = addressInt.add(BigInteger.ONE); //add 1 in all other cases
                }
            }
        }

        System.out.println("Pass 1 done.\n");
        System.out.println("Symbol table: (after the first pass)");
        System.out.println("Symbol name\t\tAddr/code");
        System.out.println(symbolTable);
        System.out.println("Listing file: (after the first pass)\n");
        System.out.println(listingFile);

        line = 1;
        addressInt = null;
        addressInt = new BigInteger("0", 2);
        listingFile = "";
        listingFile = listingFile + ("Line Addr Machine Code\tLabel\t\tMne\t\tOperands\t\t\tComments\t\t\n"); //header of the listing file

        for (LineStatement lineStatement : IR) { //parse through the IR
            String lineString = "" + line;

            String address = addressInt.toString(16).toUpperCase();
            if (address.length() < 4)
                address = "000".substring(address.length() - 1) + address; //padding out the address with heading 0's

            String lineSpaces = " ";
            for (int i = lineString.length(); i < 4; i++) {
                lineSpaces = lineSpaces + " "; //spaces for formatting
            }

            listingFile = listingFile + (line + lineSpaces + address + " ");

            if (lineStatement.getInstruction() != null) {
                String machineCodeString = "";

                if (lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("br.i8") ||
                        lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("brf.i8") ||
                        lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("ldc.i8") ||
                        lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("ldv.u8") ||
                        lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("stv.u8") ||
                        lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("lda.i16")) {
                    //relative addressing
                    machineCodeString = Integer.toHexString(lookUpTable.get(lineStatement.getInstruction().getMnemonic().getName()).getOpcodeCode()).toUpperCase();
                    if (lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("ldv.u8") ||
                            lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("stv.u8") ||
                            lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("ldc.i8")) {
                        String operandString = Integer.toHexString(Integer.parseInt(lineStatement.getInstruction().getOperands().peek()));
                        machineCodeString = machineCodeString + " " + "0".substring(operandString.length() - 1) + operandString;
                    } else {
                        String relativeAddr = "";
                        if (lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase("lda.i16")) {
                            relativeAddr = "00 ";
                        }
                        if (symbolTable.get(lineStatement.getInstruction().getOperands().peek()).equalsIgnoreCase("null") == false) {
                            int difference = Integer.parseInt(symbolTable.get(lineStatement.getInstruction().getOperands().peek()), 16) - Integer.parseInt(address,16);
                            if (difference < 0) {
                                if (lineStatement.getInstruction().getMnemonic().getName().contains(".i8")) {
                                    difference = difference + 256;
                                }
                            }
                            relativeAddr = relativeAddr + "0".substring(Integer.toHexString(difference).length() - 1) + Integer.toHexString(difference);
                        } else {
                            relativeAddr = relativeAddr + "??";
                        }

                        machineCodeString = machineCodeString + " " + relativeAddr;

                    }
                } else if (lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase(".cstring") == true) {
                    String operand = lineStatement.getInstruction().getOperands().peek();
                    operand = operand.replaceAll("\"", "");
                    for (int i = 0; i < operand.length(); i++) {
                        machineCodeString = machineCodeString + Integer.toHexString(operand.charAt(i)) + " ";
                    }
                    machineCodeString = machineCodeString + "00";
                } else if (lineStatement.getInstruction().getMnemonic().getName().contains(".") == true) { //inherent addressing
                    int immediateOffset = 0;
                    if (lineStatement.getInstruction().getMnemonic().getName().contains(".i3")) {
                        immediateOffset = Integer.parseInt(lineStatement.getInstruction().getOperands().peek()) + 4;
                    } else {
                        immediateOffset = Integer.parseInt(lineStatement.getInstruction().getOperands().peek());
                    }
                    int machineCoder = lookUpTable.get(lineStatement.getInstruction().getMnemonic().getName()).getOpcodeCode() + immediateOffset;
                    machineCodeString = "" + Integer.toHexString(machineCoder);
                } else {
                    machineCodeString = Integer.toHexString(lookUpTable.get(lineStatement.getInstruction().getMnemonic().getName()).getOpcodeCode());
                }

                listingFile = listingFile + machineCodeString.toUpperCase();
                machine.add(machineCodeString.toUpperCase());
            } else {
                listingFile = listingFile + "\t\t";
            }

            if (lineStatement.getLabel() != null) {
                symbolTable.delete(lineStatement.getLabel());
                symbolTable.put(lineStatement.getLabel(), address);
                if (lineStatement.getLabel().length() < 4) {
                    listingFile = listingFile + "\t\t" + lineStatement.getLabel() + "\t\t\t";
                } else {
                    listingFile = listingFile + "\t\t" + lineStatement.getLabel() + "\t\t";
                }
            } else {
                listingFile = listingFile + "\t\t\t" + "\t" + "\t\t";
            }

            if (lineStatement.getInstruction() != null) {
                listingFile = listingFile + lineStatement.getInstruction().getMnemonic().getName() + "\t";
                if (lineStatement.getInstruction().getOperands() != null) {
                    listingFile = listingFile + lineStatement.getInstruction().getOperands().toString();
                    if (lineStatement.getInstruction().getOperands().toString().length() < 4) {
                        listingFile = listingFile + "\t\t\t\t";
                    } else {
                        listingFile = listingFile + "\t\t\t";
                    }
                }
            } else {
                listingFile = listingFile + "\t\t\t\t";
            }

            if (lineStatement.getComment() != null) {
                listingFile = listingFile + lineStatement.getComment() + "\n";
            } else {
                listingFile = listingFile + "\n";
            }

            line++;

            if (lineStatement.getInstruction() != null) {
                if (lineStatement.getInstruction().getMnemonic().getName().contains(".i8") || lineStatement.getInstruction().getMnemonic().getName().contains(".u8")) {
                    addressInt = addressInt.add(BigInteger.ONE);
                    addressInt = addressInt.add(BigInteger.ONE); //add 2 to address
                } else if (lineStatement.getInstruction().getMnemonic().getName().contains(".i16")) {
                    addressInt = addressInt.add(BigInteger.ONE);
                    addressInt = addressInt.add(BigInteger.ONE);
                    addressInt = addressInt.add(BigInteger.ONE); //add 3 to address
                } else if (lineStatement.getInstruction().getMnemonic().getName().equalsIgnoreCase(".cstring")){
                    for (int j = 0; j < lineStatement.getInstruction().getOperands().peek().length() - 2 + 1; j++) {
                        addressInt = addressInt.add(BigInteger.ONE); //add length of string to address plus one
                    }
                } else {
                    addressInt = addressInt.add(BigInteger.ONE); //add 1 in all other cases
                }
            }
        }

        System.out.println("Pass 2 done.\n");
        System.out.println("Listing file: (after the first pass)\n");
        System.out.println(listingFile);

        for (String s : machine) {
            System.out.print(s + " ");
        }

        System.out.println("\n");

        try {
            File dotLst = new File("" + outputFileName);

            if (dotLst.createNewFile()) {
                FileWriter writer = new FileWriter(""+ outputFileName);
                writer.write(listingFile);
                writer.close();
                System.out.println("File created: " + dotLst.getName());
            } else {
                dotLst.delete();
                dotLst.createNewFile();
                FileWriter writer = new FileWriter(""+ outputFileName);
                writer.write(listingFile);
                writer.close();
                System.out.println("File created: " + dotLst.getName());
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void GenerateExecutableFile(String outputFileName) throws IOException {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(outputFileName));
        for (String s : machine) {
            java.util.Scanner sc = new java.util.Scanner(s);
            sc.useDelimiter(" ");
            String bytecode;
            while (sc.hasNext() == true) {
                bytecode = sc.next();
                int byter = Integer.parseInt(bytecode, 16);
                dos.write(byter);
                dos.flush();
            }
        }
        dos.close();
    }
}
