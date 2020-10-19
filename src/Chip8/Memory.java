package Chip8;

public class Memory {
    private byte[] memory;
    private short[] stack;

    public Memory()
    {
        memory = new byte[4096];
        stack = new short[16];
    }

    public byte getMemoryAtAddress(short address)
    {
        if(address > 0xFFF)
        {
            System.err.println("Memory address out of range. 0x000 to 0xFFF are allowed");
            return 0x00;
        }
        else
        {
            return memory[address];
        }
    }

    public void setMemoryAtAddress(short address, byte value)
    {
        if(address > 0xFFF)
        {
            System.err.println("Memory address out of range. 0x000 to 0xFFF are allowed");
        }
        else
        {
            memory[address] = value;
        }
    }

    public void printProgramMemory()
    {
        for(short i = 0x200; i < 0xFFF; i += 0x1)
        {
            System.out.println(String.format("0x%02x", memory[i]));
        }
    }
}
