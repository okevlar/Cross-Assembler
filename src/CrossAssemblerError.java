public class CrossAssemblerError implements ICrossAssemblerError{

    private Position p;
    private String message;

    public CrossAssemblerError(Position p, String message) {
        this.p = p;
        this.message = message;
    }

    public Position getP() {
        return p;
    }

    public String getMessage() {
        return message;
    }

    public void setP(Position p) {
        this.p = p;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        return "Error at position " + p.toString() + ": " + message;
    }
}
