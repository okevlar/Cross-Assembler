public interface ICrossAssemblerError {
    String toString();
    Position getP();
    String getMessage();
    void setP(Position p);
    void setMessage(String message);
}
