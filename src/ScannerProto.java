import java.io.IOException;
//is scanner, no interface
public class ScannerProto {
    private String inputFileName;
    private ST table;
    private Instruction instruction;

    ScannerProto(String FileName, ST table) {
        inputFileName=FileName;
        this.table = table;
    }

    void PopulateTokenList(Queue<Token> iTokenQueue) throws IOException{

        Reader CharReader = new Reader(inputFileName);
        int lineNumber = 1;
        String mnemonic = "";
        String comment = "";
        String number = "";
        boolean isComment = false;
        // Counts the number of Spaces to see if it's a label or operand
        int columnNum = 0;
        boolean syntax;
        int anOpcode = 0;
        instruction = new Instruction();
        while (!CharReader.isEOF()) { // I removed true, is that wrong?
            CharReader.nextChar();
            Token token = new Token();
        if ( CharReader.getCurrentChar() == ' ' && !isComment) {//detects space
                columnNum+=1;
                continue;
            } else if ( CharReader.isEOL() || CharReader.isEOF()) {
                if (isComment){
                    System.out.println("Found a comment at line " + lineNumber + ": " + comment);
                    token = new Token(new Position(lineNumber,columnNum), comment, TokenType.Comment);
                    //Parser = new Parser(table, token, columnNum) ignore this
                    //syntax = Parser.check();
                    //if statement to see if its correct syntax

                }
                else if (mnemonic.length() != 0) {
                    if (!CharReader.isEOF())
                        mnemonic = mnemonic.substring(0, mnemonic.length() - 1); // trimming last character because the new line character gets included
                    token = new Token(new Position(lineNumber,columnNum), mnemonic, TokenType.Mnemonic);
                    Mnemonic aMnemonic = new Mnemonic(mnemonic,anOpcode);

                    //return token to the parser
                    //Parser = new Parser(token, columnNum)
                    iTokenQueue.enqueue(token);
                    //checking method
                    //if(syntax){instruction.mnemonic = aMnemonic}
                    //--> You send it to another method that puts it into instruction, then linestatement
                    // the new method, should have EOL parameter
                }

                lineNumber += 1;
                mnemonic = "";
                comment = "";
                columnNum = 0;
                isComment = false;
            } else if ( CharReader.getCurrentChar() == ';' || isComment) {
                isComment = true; // set comment status for next loop
                comment = comment + CharReader.getCurrentChar();
            } else {
                mnemonic = mnemonic + CharReader.getCurrentChar();
            }

            //if (CharReader.isEOF())
                //break;
        }

    }

  /*  iInstruction.Instruction CreateLineStatement(char EOL, Object theToken, TokenType type, Token token){
            if((EOL == 'n')){
                if(type == TokenType.Mnemonic){
                    instruction.mnemonic = (Mnemonic)theToken;
                }
                if(type == TokenType.LabelOperand){
                    instruction.operand = (int)theToken;
                }

            }
            else if(EOL == '\n'){
                // check if there is nothing before it
                if(type == TokenType.Mnemonic){
                    iInstruction.Instruction instruction = new iInstruction.Instruction((Mnemonic)theToken);
                    iLineStatement.LineStatement line = new iLineStatement.LineStatement((iInstruction.Instruction)theInstruction);

                }
                // check if there is nothing before it
                if(type == TokenType.Comment){
                    iLineStatement.LineStatement line = new iLineStatement.LineStatement((String)theToken);
                }
                else{
                    iLineStatement.LineStatement line = new iLineStatement.LineStatement((iInstruction.Instruction)theInstruction);
                }


            }
    }*/
}




