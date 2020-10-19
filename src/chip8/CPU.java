package chip8;

public class CPU {

    private Memory memory;
    private Registers registers;

    private short currentOpcode;

    CPU(Memory memory, Registers registers)
    {
        this.memory = memory;
        this.registers = registers;
    }

    public void fetchOpcode()
    {
        short PC = registers.getPC();

        byte msb = memory.getMemoryAtAddress(PC);
        byte lsb = memory.getMemoryAtAddress((short) (PC + 0x1));

        currentOpcode = (short)(msb << 8 | lsb & 0x00FF);
    }

    public void incrementPC()
    {
        registers.setPC((short)(registers.getPC() + 0x2));
    }

    public void decodeAndRunOpcode()
    {
        switch (currentOpcode)
        {
            case 0x00e0:
                cl();
                break;
            default:
                break;
        }
    }

    ///Opcode 0x00e0
    ///Clears the screen
    private void cl()
    {
        System.out.println("Clear screen");
    }
}
