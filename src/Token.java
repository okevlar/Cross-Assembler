// Token.java - (c) 2000-2021 by Michel de Champlain

public class Token implements IToken, Comparable<Token> {
    private Position  pos;
    private TokenType type;
    private String    name;
    Token(Position pos, String name, TokenType type) {
        this.pos = pos;
        this.name = name;
        this.type = type;
    }

    Token() {
        pos = null;
        name = "";
        type = null;
    }

    public  Position  getPosition()  { return pos; }
    public  String    getName()      { return name; }
    public  TokenType getType()      { return type; }
    public  String    toString()     { return "["+getName()+pos+"="+type+"]"; }


    @Override
    public int compareTo(Token o) {
        return 0;
    }
}
