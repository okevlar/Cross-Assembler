import java.io.IOException;

public class Parser implements IParser {
    private Token aToken;
    private TokenType type;
    private Scanner scanner;
    private ST lookUpTable;
    private ST symbolTable;
    private String FileName;
    private ErrorReporter ER;

    /**
     * Constructor that takes in a scanner and ST to assess the grammar.
     *
     * @param lookUpTable     used to look up variables when checking for grammar errors.
     * @param FileName, used to initialize scanner object in method.
     */
    public Parser(ST lookUpTable, String FileName, ErrorReporter ER, ST symbolTable, Scanner scanner) {
        this.FileName = FileName;
        this.lookUpTable = lookUpTable;
        this.ER = ER;
        this.symbolTable = symbolTable;
        this.scanner = scanner;
    }

    public Parser(ST lookUpTable, String FileName, ErrorReporter ER, ST symbolTable) {
        this.FileName = FileName;
        this.lookUpTable = lookUpTable;
        this.ER = ER;
        this.symbolTable = symbolTable;
    }

    /**
     * Used to check a token, when it's a label or mnemonic.
     *
     * @return true if it was correctly identifies as a label or mnemonic.
     * else it sets type variable to null and returns false.
     */
    private boolean check() {
        //check if label or mnemonic
        int columnNumb = aToken.getPosition().getColumnNumber();
        int lineNumb = aToken.getPosition().getLineNumber();
        if (aToken.getType() != null && aToken.getType() == TokenType.Comment) {
            type = TokenType.Comment;
            return true;
        } else if (columnNumb == 1) {
            if (lookUpTable.contains(aToken.getName())) {
                type = TokenType.Mnemonic;

                if (symbolTable.contains(aToken.getName()) == false) {
                    symbolTable.put(aToken.getName(), Integer.toHexString(((Mnemonic)(lookUpTable.get(aToken.getName()))).getOpcodeCode()).toUpperCase());
                }
                scanner.setColumnNumb(scanner.getColumnNumb() + 1);
                return true;
            } else {
                type = TokenType.Label; //return true
                if (symbolTable.contains(aToken.getName()) == false) {
                    symbolTable.put(aToken.getName(), "null");
                } else if (symbolTable.contains(aToken.getName()) == true) {
                    Position p = new Position(lineNumb, columnNumb);
                    ER.addError(p, "Error: " + aToken.getName() + " label already defined.");
                    type = null;
                    return false;
                }
                return true;
            }
        } else if (columnNumb == 2) {
            //if in lookUpTable for mnemonics then
            if (lookUpTable.contains(aToken.getName())) {
                type = TokenType.Mnemonic;

                if (symbolTable.contains(aToken.getName()) == false) {
                    symbolTable.put(aToken.getName(), Integer.toHexString(((Mnemonic)(lookUpTable.get(aToken.getName()))).getOpcodeCode()).toUpperCase());
                }
                return true;
            } else {
                Position p = new Position(lineNumb, columnNumb);
                ER.addError(p, "Invalid mnemonic.");
                type = null;
                return false;
            }
        } else if (columnNumb >= 3) {
            type = TokenType.Operand;
            return true;
        }
        type = null;
        return false;
    }

    public Queue<LineStatement> createLineStatement() {

        Queue<LineStatement> IR = new Queue<LineStatement>();

        try {
            if (scanner == null) {
                scanner = new Scanner(FileName);
            }
            //int count = 0;
            while (true) {
                Instruction instruction = new Instruction();
                LineStatement line = new LineStatement();
                Queue<String> operands = new Queue<String>();
                scanner.nextToken();
                aToken = scanner.getToken();

                while (true) {
                    if (aToken == null) {
                        break;
                    }

                    if (check()) {
                        if (type == TokenType.Label) {
                            line.setLabel(aToken.getName());
                            line.setRowNumber(aToken.getPosition().getLineNumber());
                        } else if (type == TokenType.Mnemonic) {
                            instruction.setMnemonic(new Mnemonic(aToken.getName()));
                            line.setInstruction(instruction);
                            line.setRowNumber(aToken.getPosition().getLineNumber());
                        } else if(type == TokenType.Operand) {
                            operands.enqueue(aToken.getName());
                            instruction.setOperands(operands);
                        } else if (type == TokenType.Comment) {
                            line.setComment(aToken.getName());
                            line.setRowNumber(aToken.getPosition().getLineNumber());
                        }
                    }

                    if (scanner.getIsNL() || scanner.getIsEOF()) {
                        break;
                    } else {
                        scanner.nextToken();
                        aToken = scanner.getToken();
                    }
                }

                if (scanner.getIsNL()) {
                    IR.enqueue(line);
                } else if (scanner.getIsEOF()) {
                    IR.enqueue(line);
                    break;
                }

                /*
                System.out.println(count);
                count += 1;
                //The above statements are only used for debugging! Ignore otherwise!
                 */
            }

        } catch (IOException e) {
            System.out.print(e.getMessage());
        }

        for (LineStatement line : IR) {
            if (line.getInstruction() != null) {
                //relative addressing:  br.i8, brf.i8, ldc.i8, ldv.u8, stv.u8, and lda.i16
                if (line.getInstruction().getMnemonic().getName().equalsIgnoreCase("br.i8") ||
                        line.getInstruction().getMnemonic().getName().equalsIgnoreCase("brf.i8") ||
                        line.getInstruction().getMnemonic().getName().equalsIgnoreCase("lda.i16")) {
                    if (symbolTable.contains(line.getInstruction().getOperands().peek()) == false) {
                        Position p = new Position(line.getRowNumber(), 3);
                        ER.addError(p, "Instructions with relative mode addressing must refer to an existing label.");
                    }
                }
                //inherent addressing
                else if (line.getInstruction().getMnemonic().getName().contains(".") == true) {
                    if (VerifyFormatAndOperands(line) == false) {
                        continue;
                    }
                } else if (line.getInstruction().getMnemonic().getName().contains(".") == false && line.getInstruction().getOperands() != null) {
                    Position p = new Position(line.getRowNumber(), 3);
                    ER.addError(p, "Instructions with inherent mode addressing do not have an operand field.");
                }
            }
        }

        return IR;
    }

    public String getFile() {
        return FileName;
    }

    public boolean VerifyFormatAndOperands(LineStatement line) {
        String format = line.getInstruction().getMnemonic().getName().substring(line.getInstruction().getMnemonic().getName().indexOf('.') + 1, line.getInstruction().getMnemonic().getName().length());

        if (line.getInstruction().getOperands() == null) {
            ER.addError(new Position(line.getRowNumber(), 3), "This immediate instruction must have a number in the operand field.");
            return false;
        }

        if (format.equalsIgnoreCase("u3")) {
            for (String oper : line.getInstruction().getOperands()) {
                if (Integer.parseInt(oper) < 0 || 7 < Integer.parseInt(oper)) {
                    ER.addError(new Position(line.getRowNumber(), 3), "The immediate instruction '" + line.getInstruction().getMnemonic().getName() + "' must have a 3-bit unsigned operand number ranging from 0 to 7.");
                    return false;
                }
            }
        }

        else if (format.equalsIgnoreCase("i3")) {
            for (String oper : line.getInstruction().getOperands()) {
                if (Integer.parseInt(oper) < -4 || 3 < Integer.parseInt(oper)) {
                    ER.addError(new Position(line.getRowNumber(), 3), "The immediate instruction '" + line.getInstruction().getMnemonic().getName() + "' must have a 3-bit signed operand number ranging from -4 to 3.");
                    return false;
                }
            }
        }
        else if (format.equalsIgnoreCase("u4")) {
            for (String oper : line.getInstruction().getOperands()) {
                if (Integer.parseInt(oper) < 0 || 15 < Integer.parseInt(oper)) {
                    ER.addError(new Position(line.getRowNumber(), 3), "The immediate instruction '" + line.getInstruction().getMnemonic().getName() + "' must have a 4-bit unsigned operand number ranging from 0 to 15.");
                    return false;
                }
            }
        }
        else if (format.equalsIgnoreCase("i4")) {
            for (String oper : line.getInstruction().getOperands()) {
                if (Integer.parseInt(oper) < -8 || 7 < Integer.parseInt(oper)) {
                    ER.addError(new Position(line.getRowNumber(), 3), "The immediate instruction '" + line.getInstruction().getMnemonic().getName() + "' must have a 4-bit signed operand number ranging from -8 to 7.");
                    return false;
                }
            }
        }
        else if (format.equalsIgnoreCase("u5")) {
            for (String oper : line.getInstruction().getOperands()) {
                if (Integer.parseInt(oper) < 0 || 31 < Integer.parseInt(oper)) {
                    ER.addError(new Position(line.getRowNumber(), 3), "The immediate instruction '" + line.getInstruction().getMnemonic().getName() + "' must have a 5-bit unsigned operand number ranging from 0 to 31.");
                    return false;
                }
            }
        }
        else if (format.equalsIgnoreCase("i5")) {
            for (String oper : line.getInstruction().getOperands()) {
                if (Integer.parseInt(oper) < -16 || 15 < Integer.parseInt(oper)) {
                    ER.addError(new Position(line.getRowNumber(), 3), "The immediate instruction '" + line.getInstruction().getMnemonic().getName() + "' must have a 5-bit signed operand number ranging from -16 to 15.");
                    return false;
                }
            }
        }
        else if (format.equalsIgnoreCase("u8")) {
            for (String oper : line.getInstruction().getOperands()) {
                if (Integer.parseInt(oper) < 0 || 255 < Integer.parseInt(oper)) {
                    ER.addError(new Position(line.getRowNumber(), 3), "The immediate instruction '" + line.getInstruction().getMnemonic().getName() + "' must have a 8-bit unsigned operand number ranging from 0 to 255.");
                    return false;
                }
            }
        }
        else if (format.equalsIgnoreCase("i8")) {
            for (String oper : line.getInstruction().getOperands()) {
                if (Integer.parseInt(oper) < -128 || 127 < Integer.parseInt(oper)) {
                    ER.addError(new Position(line.getRowNumber(), 3), "The immediate instruction '" + line.getInstruction().getMnemonic().getName() + "' must have a 8-bit signed operand number ranging from -128 to 127.");
                    return false;
                }
            }
        }
        else if (format.equalsIgnoreCase("u16")) {
            for (String oper : line.getInstruction().getOperands()) {
                if (Integer.parseInt(oper) < 0 || 65535 < Integer.parseInt(oper)) {
                    ER.addError(new Position(line.getRowNumber(), 3), "The immediate instruction '" + line.getInstruction().getMnemonic().getName() + "' must have a 16-bit unsigned operand number ranging from 0 to 65535.");
                    return false;
                }
            }
        }
        else if (format.equalsIgnoreCase("i16")) {
            for (String oper : line.getInstruction().getOperands()) {
                if (Integer.parseInt(oper) < -32768 || 32767 < Integer.parseInt(oper)) {
                    ER.addError(new Position(line.getRowNumber(), 3), "The immediate instruction '" + line.getInstruction().getMnemonic().getName() + "' must have a 16-bit signed operand number ranging from -32768 to 32767.");
                    return false;
                }
            }
        }
        else if (format.equalsIgnoreCase("u32")) {
            for (String oper : line.getInstruction().getOperands()) {
                if (Integer.parseInt(oper) < 0 || 4294967295L < Integer.parseInt(oper)) {
                    ER.addError(new Position(line.getRowNumber(), 3), "The immediate instruction '" + line.getInstruction().getMnemonic().getName() + "' must have a 32-bit unsigned operand number ranging from 0 to 4294967295.");
                    return false;
                }
            }
        }
        else if (format.equalsIgnoreCase("i32")) {
            for (String oper : line.getInstruction().getOperands()) {
                if (Integer.parseInt(oper) < -2147483648 || 2147483647 < Integer.parseInt(oper)) {
                    ER.addError(new Position(line.getRowNumber(), 3), "The immediate instruction '" + line.getInstruction().getMnemonic().getName() + "' must have a 32-bit signed operand number ranging from -2147483648 to 2147483647.");
                    return false;
                }
            }
        }
        else if (format.equalsIgnoreCase("cstring")) {
            for (String oper : line.getInstruction().getOperands()) {
                if (oper.charAt(0) != '"' && oper.charAt(oper.length()-1) != '"') {
                    ER.addError(new Position(line.getRowNumber(), 3), "The immediate instruction '" + line.getInstruction().getMnemonic().getName() + "' must have a string with the correct syntax.");
                    return false;
                }
            }
        }
        
            return true;
    }

    public ErrorReporter getER(){
        return ER;
    }

    public ST getTable(){
        return lookUpTable;
    }

    public boolean checkRelativeInstruction(LineStatement line){
        return true;
    }
    //IM NOT SURE ABOUT THE BELOW ONE.
    /**
     *  Checks if offsets are smaller, fit or larger than current instruction size, adds an error if it is.
     * @param line LineStatement object used to access instruction size
     * @return a String that symbolizes whether the offset fits is smaller or larger than the current instruction size.
     */
    public String checkSize(LineStatement line){
        return "fit";
    }
}


