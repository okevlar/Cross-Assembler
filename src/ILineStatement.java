public interface ILineStatement {
    String getLabel();
    Instruction getInstruction();
    String getComment();
    String toString();
    void setLabel(String label);
    void setInstruction(Instruction instruction);
    void setComment(String comment);
    int getRowNumber();
    void setRowNumber(int rowNumber);
}
