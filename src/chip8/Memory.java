package chip8;

public class Memory {
    private byte[] memory;
    private short[] stack;

    private boolean[][] screenMemory;

    public Memory()
    {
        memory = new byte[Utils.MEMORY_SIZE];
        stack = new short[Utils.STACK_SIZE];
        screenMemory = new boolean[Utils.SCREEN_WIDTH][Utils.SCREEN_HEIGHT];

        loadHexSpritesToMemory();
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

    public boolean getPixelAtPosition(byte x, byte y)
    {
        return screenMemory[x][y];
    }
    public void setPixelAtPosition(byte x, byte y, boolean state)
    {
        screenMemory[x][y] = state;
    }

    private void loadHexSpritesToMemory()
    {
        for(byte i = 0; i < Utils.sprite_0.length; i++)//0
        {
            setMemoryAtAddress((short) (Utils.SPRITES_STORAGE_STARTING_ADDRESS + i), Utils.sprite_0[i]);
        }
        for(byte i = 0; i < Utils.sprite_1.length; i++)//1
        {
            setMemoryAtAddress((short) (Utils.SPRITES_STORAGE_STARTING_ADDRESS + 5 + i), Utils.sprite_1[i]);
        }
        for(byte i = 0; i < Utils.sprite_2.length; i++)//2
        {
            setMemoryAtAddress((short) (Utils.SPRITES_STORAGE_STARTING_ADDRESS + 10 + i), Utils.sprite_2[i]);
        }
        for(byte i = 0; i < Utils.sprite_3.length; i++)//3
        {
            setMemoryAtAddress((short) (Utils.SPRITES_STORAGE_STARTING_ADDRESS + 15 + i), Utils.sprite_3[i]);
        }
        for(byte i = 0; i < Utils.sprite_4.length; i++)//4
        {
            setMemoryAtAddress((short) (Utils.SPRITES_STORAGE_STARTING_ADDRESS + 20 + i), Utils.sprite_4[i]);
        }
        for(byte i = 0; i < Utils.sprite_5.length; i++)//5
        {
            setMemoryAtAddress((short) (Utils.SPRITES_STORAGE_STARTING_ADDRESS + 25 + i), Utils.sprite_5[i]);
        }
        for(byte i = 0; i < Utils.sprite_6.length; i++)//6
        {
            setMemoryAtAddress((short) (Utils.SPRITES_STORAGE_STARTING_ADDRESS + 30 + i), Utils.sprite_6[i]);
        }
        for(byte i = 0; i < Utils.sprite_7.length; i++)//7
        {
            setMemoryAtAddress((short) (Utils.SPRITES_STORAGE_STARTING_ADDRESS + 35 + i), Utils.sprite_7[i]);
        }
        for(byte i = 0; i < Utils.sprite_8.length; i++)//8
        {
            setMemoryAtAddress((short) (Utils.SPRITES_STORAGE_STARTING_ADDRESS + 40 + i), Utils.sprite_8[i]);
        }
        for(byte i = 0; i < Utils.sprite_9.length; i++)//9
        {
            setMemoryAtAddress((short) (Utils.SPRITES_STORAGE_STARTING_ADDRESS + 45 + i), Utils.sprite_9[i]);
        }
        for(byte i = 0; i < Utils.sprite_a.length; i++)//A
        {
            setMemoryAtAddress((short) (Utils.SPRITES_STORAGE_STARTING_ADDRESS + 50 + i), Utils.sprite_a[i]);
        }
        for(byte i = 0; i < Utils.sprite_b.length; i++)//B
        {
            setMemoryAtAddress((short) (Utils.SPRITES_STORAGE_STARTING_ADDRESS + 55 + i), Utils.sprite_b[i]);
        }
        for(byte i = 0; i < Utils.sprite_c.length; i++)//C
        {
            setMemoryAtAddress((short) (Utils.SPRITES_STORAGE_STARTING_ADDRESS + 60 + i), Utils.sprite_c[i]);
        }
        for(byte i = 0; i < Utils.sprite_d.length; i++)//D
        {
            setMemoryAtAddress((short) (Utils.SPRITES_STORAGE_STARTING_ADDRESS + 65 + i), Utils.sprite_d[i]);
        }
        for(byte i = 0; i < Utils.sprite_e.length; i++)//E
        {
            setMemoryAtAddress((short) (Utils.SPRITES_STORAGE_STARTING_ADDRESS + 70 + i), Utils.sprite_e[i]);
        }
        for(byte i = 0; i < Utils.sprite_f.length; i++)//F
        {
            setMemoryAtAddress((short) (Utils.SPRITES_STORAGE_STARTING_ADDRESS + 75 + i), Utils.sprite_f[i]);
        }
    }
}
