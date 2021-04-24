/**
 * SOEN 341    W21
 * GAMESTOP - GROUP 13
 * Filename: Mnemonic.java
 * Creation: 2021-02-24
 */

public class Mnemonic {
    private String name;
    private int opcode;


    Mnemonic(String name) {
        this.name = name;
    }

    Mnemonic(String name, int opcode) {
        this.name = name;
        this.opcode = opcode;
    }

    public String getName() { return name; }
    public int getOpcodeCode() { return opcode; }
    public String toString() {return "name "+name+"";};

}
