import java.io.IOException;

public class Scanner implements IScanner{
    private int lineNumb = 1;
    private static int columnNumb = 1;
    private String name = "";
    private Token CurrentToken;
    private Reader reader;
    private static boolean isNL;
    private boolean isEF;

    Scanner(String FileName) throws IOException {
        this.reader = new Reader(FileName);
        isNL = true;
        //reader.nextChar();
    }

    public void nextToken() throws IOException {
        char CurrentChar = reader.getCurrentChar();
        CurrentToken = null;
        boolean leadingSpaces = false;
        if (isNL == true) {
            isNL = false;
            leadingSpaces = true;
            columnNumb = 1;
        }
        reader.nextChar();
        CurrentChar = reader.getCurrentChar();
        String chars = "";
        while (true) {
            //if statement for comment
            if (reader.isComment()) {
                while (!reader.isEOF() && !reader.isEOL() && !reader.isCR()) {
                    chars += CurrentChar;
                    reader.nextChar();
                    CurrentChar = reader.getCurrentChar();
                }
                name = chars;
                chars = "";
                //System.out.println(name);
                CurrentToken = new Token(new Position(lineNumb, columnNumb), name, TokenType.Comment);
                lineNumb += 1;
                break;
            } else if (!reader.isCR() && !reader.isSpace() && !reader.isEOL() && !reader.isEOF()) {
                chars += CurrentChar;
            } else if (!chars.equals("") && (reader.isEOL() || reader.isCR() || reader.isEOF() || reader.isSpace())) {
                name = chars;

                CurrentToken = new Token(new Position(lineNumb, columnNumb), name, null);

                if (!reader.isSpace()) {
                    lineNumb += 1;
                }
                columnNumb++;
                chars = "";
                break;
                //System.out.println(name);
            } else if (reader.isSpace()) {
                while (CurrentChar == ' ') {
                    reader.nextChar();
                    CurrentChar = reader.getCurrentChar();
                }

                if (leadingSpaces == true) {
                    leadingSpaces = false;
                }

                continue;
            } else if (reader.isCR()) {
                //just skip this character, it's a headache
            } else if (reader.isEOF() || reader.isEOL()) {
                lineNumb += 1;
                break;
            }

            reader.nextChar();
            CurrentChar = reader.getCurrentChar();
        }

        if (reader.isEOF()) {
            isEF = true;
        } else if (reader.isEOL()) {
            isNL = true;
        }

    }

    public Token getToken() {
        return CurrentToken;
    }

    public int getColumnNumb() {
        return columnNumb;
    }

    public int setColumnNumb(int num) {
        return columnNumb = num;
    }

    public boolean getIsNL() {
        return isNL;
    }

    public int getLineNumb() {
        return lineNumb;
    }

    public boolean getIsEOF() {
        return isEF;
    }

}