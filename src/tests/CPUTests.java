package tests;

import chip8.CPU;
import chip8.Memory;
import chip8.Registers;
import chip8.UnknownOpcodeException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CPUTests {

    Memory memory = new Memory();
    Registers registers = new Registers();
    CPU cpu = new CPU(memory, registers);

    @Test
    void fetchOpcode()
    {
        registers.resetAllRegisters();

        memory.setMemoryAtAddress((short) 0x200, (byte)0x0);
        memory.setMemoryAtAddress((short) 0x201, (byte)0xe0);
        cpu.fetchOpcode();

        short expected = (short)0x00e0;
        short actual = cpu.getCurrentOpcode();

        assertEquals(expected, actual);
    }
    @Test
    void incrementPC()
    {
        registers.resetAllRegisters();

        registers.setPC((short)0x200);
        cpu.incrementPC();

        short expected = (short)0x202;
        short actual = registers.getPC();

        assertEquals(expected, actual);
    }


    /** Specific opcodes **/
    @Test
    void call() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();


        memory.setMemoryAtAddress((short) 0x200, (byte)0x2B);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x20);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();


        //Stack check
        assertEquals(0x200, memory.getStackAtValue((byte)0x00));

        //Current PC check
        assertEquals(0xB20, registers.getPC());

        //Current SP check
        assertEquals(1, registers.getSP());
    }

    @Test
    void loadToI() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        memory.setMemoryAtAddress((short) 0x200, (byte)0xAB);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x20);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals(0xB20, registers.getI());
    }

    @Test
    void addToRegWithCarry() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x3, (byte)0xFF);
        registers.setVAtAddress(0xA, (byte)0x02);

        memory.setMemoryAtAddress((short) 0x200, (byte)0x83);
        memory.setMemoryAtAddress((short) 0x201, (byte)0xA4);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals(0x1, registers.getVAtAddress(3));
        assertEquals(1, registers.getVAtAddress(0xF));
    }

    @Test
    void addToRegWithoutCarry() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x3, (byte)0x5);
        registers.setVAtAddress(0xA, (byte)0x2);

        memory.setMemoryAtAddress((short) 0x200, (byte)0x83);
        memory.setMemoryAtAddress((short) 0x201, (byte)0xA4);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals(0x7, registers.getVAtAddress(3));
        assertEquals(0, registers.getVAtAddress(0xF));
    }

    @Test
    void loadVXasBCDtoMemory() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x2, (byte)0xE);

        memory.setMemoryAtAddress((short) 0x200, (byte)0xF2);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x33);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals(0x0, memory.getMemoryAtAddress(registers.getI()));
        assertEquals(0x1, memory.getMemoryAtAddress((short)(registers.getI() + 1)));
        assertEquals(0x4, memory.getMemoryAtAddress((short)(registers.getI() + 2)));
    }
}