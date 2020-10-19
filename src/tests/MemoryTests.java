package tests;

import chip8.Memory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MemoryTests {

    private Memory memory = new Memory();

    @Test
    void setGet()
    {
        memory.setMemoryAtAddress((short)0x425, (byte)0x10);
        byte result = memory.getMemoryAtAddress((short)0x425);
        assertEquals(result, (byte)0x10);
    }

    @Test
    void getOutOfBounds()
    {
        try
        {
            memory.getMemoryAtAddress((short)0x1000);
            fail("Did not throw an exception");
        }
        catch(IllegalArgumentException e)
        {
            //All good
        }
    }

    @Test
    void setOutOfBounds()
    {
        try
        {
            memory.setMemoryAtAddress((short)0x1000, (byte)0x5F);
            fail("Did not throw an exception");
        }
        catch(IllegalArgumentException e)
        {
            //All good
        }
    }
}