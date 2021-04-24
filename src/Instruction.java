public class Instruction implements IInstruction {
    private Mnemonic mnemonic;
    private Queue<String> operands;

    public Instruction(){
        this.mnemonic = null;
        this.operands = null;
    }

    public Instruction(Mnemonic mnemonic){
        this.mnemonic = mnemonic;
    }

    public Instruction(Mnemonic mnemonic, Queue<String> operands){
        this.mnemonic = mnemonic;
        this.operands = operands;
    }

    public Mnemonic getMnemonic(){ return mnemonic; }

    public Queue<String> getOperands() { return operands; }

    public void setMnemonic(Mnemonic mnemonic){ this.mnemonic = mnemonic; }

    public void setOperands(Queue<String> anOperand){
        operands = anOperand;
    }

    @Override
    public String toString(){
        return "mnemonic " + mnemonic;
    }

}