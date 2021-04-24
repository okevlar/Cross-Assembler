public interface IInstruction {
    Mnemonic getMnemonic();
    Queue<String> getOperands();
    void setMnemonic(Mnemonic mnemonic);
    void setOperands(Queue<String> anOperand);
    String toString();
}
