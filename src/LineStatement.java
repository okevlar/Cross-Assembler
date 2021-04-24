public class LineStatement implements ILineStatement {
    private String label;
    private Instruction instruction;
    private String comment;
    private int rowNumber;

    public LineStatement(Instruction instruction, String label, String comment) {
        this.instruction = instruction;
        this.label = label;
        this.comment = comment;
    }

    public LineStatement(Instruction instruction, String comment) {
        this.instruction = instruction;
        this.comment = comment;
    }

    public LineStatement() {
    } //empty constructor

    public LineStatement(Instruction instruction) {
        this.instruction = instruction;
    }

    public LineStatement(String comment) {
        this.comment = comment;
    }

    public String getLabel() {
        return label;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        String returnValue = "";
        if (comment != null && instruction != null) {
            returnValue = "Line Statement at row " + rowNumber + ": instruction {" + instruction.toString() + "} comment " + comment;
        } else if (comment != null && instruction == null) {
            returnValue = "Line Statement at row " + rowNumber + ": comment " + comment;
        } else if (comment == null && instruction != null) {
            returnValue = "Line Statement at row " + rowNumber + ": instruction {" + instruction.toString() + "}";
        }
        return returnValue;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setInstruction(Instruction instruction) {
        this.instruction = instruction;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }
}