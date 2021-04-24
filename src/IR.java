public class IR implements IIR {

    Queue<LineStatement> IR;

    IR() {
    }

    IR(Queue<LineStatement> IR) {
        this.IR = IR;
    }

    public Queue<LineStatement> getIR() {
        return IR;
    }

    public void setIR(Queue<LineStatement> IR) {
        this.IR = IR;
    }
}
