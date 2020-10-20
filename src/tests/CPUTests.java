package tests;

import chip8.CPU;
import chip8.Memory;
import chip8.Registers;
import exceptions.UnknownOpcodeException;
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
    ///00EE
    ///Returns from a subroutine
    @Test
    void ret() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setSP((byte)0x1);
        memory.setStackAtValue((byte)0x1, (short)0x05C3);

        memory.setMemoryAtAddress((short) 0x200, (byte)0x00);
        memory.setMemoryAtAddress((short) 0x201, (byte)0xEE);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals(0x05C3, registers.getPC());
        assertEquals(0x0, registers.getSP());
    }

    ///1NNN
    ///Sets the PC to NNN
    @Test
    void jump() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        memory.setMemoryAtAddress((short) 0x200, (byte)0x14);
        memory.setMemoryAtAddress((short) 0x201, (byte)0xC7);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals(0x04C7, registers.getPC());
    }

    ///2NNN
    ///Calls Calls subroutine at NNN
    @Test
    void call() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        memory.setMemoryAtAddress((short) 0x200, (byte)0x2B);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x20);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();


        //Stack check
        assertEquals(0x200, memory.getStackAtValue((byte)0x01));

        //Current PC check
        assertEquals(0xB20, registers.getPC());

        //Current SP check
        assertEquals(0x1, registers.getSP());
    }

    ///3XKK
    ///Compares register Vx to kk, if they are equal, skip the next instruction
    @Test
    void skipOnEqualByte() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x2, (byte) 0x4D);

        memory.setMemoryAtAddress((short) 0x200, (byte)0x32);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x4D);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals(0x0202, registers.getPC());
    }

    ///3XKK
    ///Compares register Vx to kk, if they are equal, skip the next instruction
    @Test
    void dontSkipOnEqualByte() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x2, (byte) 0x3F);

        memory.setMemoryAtAddress((short) 0x200, (byte)0x32);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x4D);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertNotEquals(0x0202, registers.getPC());
    }

    ///4XKK
    ///Compares register Vx to kk, if they are not equal, skip the next instruction
    @Test
    void skipOnNonEqualByte() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x2, (byte) 0x3F);

        memory.setMemoryAtAddress((short) 0x200, (byte)0x42);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x4D);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals(0x0202, registers.getPC());
    }

    ///4XKK
    ///Compares register Vx to kk, if they are not equal, skip the next instruction
    @Test
    void dontSkipOnNonEqualByte() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x2, (byte) 0x4D);

        memory.setMemoryAtAddress((short) 0x200, (byte)0x42);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x4D);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertNotEquals(0x0202, registers.getPC());
    }

    ///5XY0
    ///Compares register Vx to Vy, if equal, skip the next instruction
    @Test
    void skipOnEqualRegister() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x2, (byte) 0x4D);
        registers.setVAtAddress(0x7, (byte) 0x4D);

        memory.setMemoryAtAddress((short) 0x200, (byte)0x52);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x70);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals(0x0202, registers.getPC());
    }

    ///5XY0
    ///Compares register Vx to Vy, if equal, skip the next instruction
    @Test
    void dontSkipOnEqualRegister() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x2, (byte) 0x4D);
        registers.setVAtAddress(0x7, (byte) 0x7F);

        memory.setMemoryAtAddress((short) 0x200, (byte)0x52);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x70);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertNotEquals(0x0202, registers.getPC());
    }

    ///ANNN
    ///Sets I to the address NNN.
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

    ///8XY4
    ///The values of Vx and Vy are added together. If the result is greater than 8 bits (i.e., > 255,) VF is set to 1,
    ///otherwise 0. Only the lowest 8 bits of the result are kept, and stored in Vx
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

    ///8XY4
    ///The values of Vx and Vy are added together. If the result is greater than 8 bits (i.e., > 255,) VF is set to 1,
    ///otherwise 0. Only the lowest 8 bits of the result are kept, and stored in Vx
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

    ///FX33
    ///Store BCD representation of Vx in memory locations I, I+1, and I+2
    ///Taken from ismael rodriguez's implementation
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