package chip8;

public class Memory {
    private byte[] memory;
    private short[] stack;

    public Memory()
    {
        memory = new byte[Utils.MEMORY_SIZE];
        stack = new short[Utils.STACK_SIZE];
    }

    public byte getMemoryAtAddress(short address)
    {
        if(address > Utils.MAX_MEMORY_ADDRESS)
        {
            throw new IllegalArgumentException("Memory address out of range. 0x000 to 0xFFF are allowed");
        }
        else
        {
            return memory[address];
        }
    }

    public void setMemoryAtAddress(short address, byte value)
    {
        if(address > Utils.MAX_MEMORY_ADDRESS)
        {
            throw new IllegalArgumentException("Memory address out of range. 0x000 to 0xFFF are allowed");
        }
        else
        {
            memory[address] = value;
        }
    }

    public short getStackAtValue(byte pointer) { return stack[pointer]; }
    public void setStackAtValue(byte pointer, short value)
    {
        if(pointer < Utils.STACK_SIZE)
        {
            stack[pointer] = value;
        }
    }
}
