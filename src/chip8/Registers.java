package chip8;

public class Registers {
    private byte[] V; /* 16 general purpose 8bit registers */
    private short I; /* 16bit register, generally used to store memory addresses */
    private byte DT; /* Delay timer */
    private byte ST; /* Sound timer */
    private short PC; /* Program counter, stores the currently executing address */
    private byte SP; /* Stack pointer, points to the topmost level of the stack */

    public Registers()
    {
        resetAllRegisters();
    }

    public byte getVAtAddress(int address) { return V[address]; }
    public void setVAtAddress(int address, byte value) { V[address] = value; }

    public short getI() { return I; }
    public void setI(short value) { I = value; }

    public byte getDT() { return DT; }
    public void setDT(byte value) { DT = value; }

    public byte getST() { return ST; }
    public void setST(byte value) { ST = value; }

    public short getPC() { return PC; }
    public void setPC(short value) { PC = value; }

    public byte getSP() { return SP; }
    public void setSP(byte value) { SP = value; }

    public void resetAllRegisters()
    {
        V = new byte[Utils.STACK_SIZE];
        I = 0x0000;
        DT = 0x00;
        ST = 0x00;
        PC = Utils.FIRST_PROGRAM_SPACE_ADDRESS;
        SP = 0x00;
    }
}
