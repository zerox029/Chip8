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

    ///6XKK
    ///Puts the value of KK into Vx
    @Test
    void loadToRegister() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        memory.setMemoryAtAddress((short) 0x200, (byte)0x62);
        memory.setMemoryAtAddress((short) 0x201, (byte)0xD4);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertNotEquals(0xD4, registers.getVAtAddress(2));
    }

    ///7XKK
    ///Adds the value kk to the value of register Vx, then stores the result in Vx.
    @Test
    void addOnRegister() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x2, (byte) 0xA);

        memory.setMemoryAtAddress((short) 0x200, (byte)0x72);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x34);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals(0x3E, registers.getVAtAddress(2));
    }

    ///8XY0
    ///Stores the value of register Vy in register Vx.
    @Test
    void duplicateRegister() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x2, (byte) 0xA);

        memory.setMemoryAtAddress((short) 0x200, (byte)0x8B);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x20);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals(0xA, registers.getVAtAddress(0xB));
    }

    ///8XY1
    ///Performs a bitwise OR on the values of Vx and Vy, then stores the result in Vx
    @Test
    void orRegister() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x2, (byte) 0xB);
        registers.setVAtAddress(0xA, (byte) 0x3);

        memory.setMemoryAtAddress((short) 0x200, (byte)0x82);
        memory.setMemoryAtAddress((short) 0x201, (byte)0xA1);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals(0xB, registers.getVAtAddress(0x2));
    }

    ///8XY2
    ///Performs a bitwise OR on the values of Vx and Vy, then stores the result in Vx
    @Test
    void andRegister() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x2, (byte) 0xB);
        registers.setVAtAddress(0xA, (byte) 0x3);

        memory.setMemoryAtAddress((short) 0x200, (byte)0x82);
        memory.setMemoryAtAddress((short) 0x201, (byte)0xA2);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals(0x3, registers.getVAtAddress(0x2));
    }

    ///8XY3
    ///Performs a bitwise exclusive OR on the values of Vx and Vy, then stores the result in Vx
    @Test
    void xorRegister() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x2, (byte) 0xB);
        registers.setVAtAddress(0xA, (byte) 0x3);

        memory.setMemoryAtAddress((short) 0x200, (byte)0x82);
        memory.setMemoryAtAddress((short) 0x201, (byte)0xA3);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals(0x8, registers.getVAtAddress(0x2));
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

    ///8XY5
    ///If Vx > Vy, then VF is set to 1, otherwise 0. Then Vy is subtracted from Vx, and the results stored in Vx
    @Test
    void subIsGreater() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x3, (byte)0x5);
        registers.setVAtAddress(0xA, (byte)0x2);

        memory.setMemoryAtAddress((short) 0x200, (byte)0x83);
        memory.setMemoryAtAddress((short) 0x201, (byte)0xA5);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals(0x3, registers.getVAtAddress(0x3));
        assertEquals(0x1, registers.getVAtAddress(0xF));
    }

    ///8XY5
    ///If Vx > Vy, then VF is set to 1, otherwise 0. Then Vy is subtracted from Vx, and the results stored in Vx
    @Test
    void subIsSmaller() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x3, (byte)0x1);
        registers.setVAtAddress(0xA, (byte)0xA);

        memory.setMemoryAtAddress((short) 0x200, (byte)0x83);
        memory.setMemoryAtAddress((short) 0x201, (byte)0xA5);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals((byte)0xF7, registers.getVAtAddress(0x3));
        assertEquals((byte)0x00, registers.getVAtAddress(0xF));
    }

    ///8XY6
    ///Shifts VX right by one. VF is set to the value of the least significant bit of VX before the shift.
    @Test
    void shr() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x0, (byte)0xFF);

        memory.setMemoryAtAddress((short) 0x200, (byte)0x80);
        memory.setMemoryAtAddress((short) 0x201, (byte)0xA6);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals((byte)0x7F, registers.getVAtAddress(0x0));
        assertEquals((byte)0x01, registers.getVAtAddress(0xF));
    }

    ///8XY7
    ///If Vy > Vx, then VF is set to 1, otherwise 0. Then Vx is subtracted from Vy, and the results stored in Vx
    @Test
    void subnIsGreater() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x3, (byte)0x5);
        registers.setVAtAddress(0xA, (byte)0x2);

        memory.setMemoryAtAddress((short) 0x200, (byte)0x83);
        memory.setMemoryAtAddress((short) 0x201, (byte)0xA7);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals(0x3, registers.getVAtAddress(0x3));
        assertEquals(0x0, registers.getVAtAddress(0xF));
    }

    ///8XY7
    ///If Vy > Vx, then VF is set to 1, otherwise 0. Then Vx is subtracted from Vy, and the results stored in Vx
    @Test
    void subnIsSmaller() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x3, (byte)0x1);
        registers.setVAtAddress(0xA, (byte)0xA);

        memory.setMemoryAtAddress((short) 0x200, (byte)0x83);
        memory.setMemoryAtAddress((short) 0x201, (byte)0xA7);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals((byte)0xF7, registers.getVAtAddress(0x3));
        assertEquals((byte)0x01, registers.getVAtAddress(0xF));
    }

    ///8XYE
    ///If the most-significant bit of Vx is 1, then VF is set to 1, otherwise to 0. Then Vx is multiplied by 2.
    @Test
    void shl() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x0, (byte)0xFF);

        memory.setMemoryAtAddress((short) 0x200, (byte)0x80);
        memory.setMemoryAtAddress((short) 0x201, (byte)0xAE);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals((byte)0xFE, registers.getVAtAddress(0x0));
        assertEquals((byte)0x01, registers.getVAtAddress(0xF));
    }

    ///9XY0
    ///If the most-significant bit of Vx is 1, then VF is set to 1, otherwise to 0. Then Vx is multiplied by 2.
    @Test
    void sneRegisterSkip() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x0, (byte)0xAA);
        registers.setVAtAddress(0x1, (byte)0x2F);

        memory.setMemoryAtAddress((short) 0x200, (byte)0x90);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x10);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals(0x202, registers.getPC());
    }

    ///9XY0
    ///If the most-significant bit of Vx is 1, then VF is set to 1, otherwise to 0. Then Vx is multiplied by 2.
    @Test
    void sneRegisterNoSkip() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x0, (byte)0xAA);
        registers.setVAtAddress(0x1, (byte)0xAA);

        memory.setMemoryAtAddress((short) 0x200, (byte)0x90);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x10);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals(0x200, registers.getPC());
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

    ///BNNN
    ///Jump to location NNN + V0
    //The PC is set to NNN plus the value of V0.
    @Test
    void jumpSum() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0, (byte)0x4D);

        memory.setMemoryAtAddress((short) 0x200, (byte)0xB6);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x2E);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals(0x67B, registers.getPC());
    }

    ///FX15
    ///DT is set equal to the value of Vx.
    @Test
    void loadRegisterOnDT() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(6, (byte)0x4D);

        memory.setMemoryAtAddress((short) 0x200, (byte)0xF6);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x15);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals(0x4d, registers.getDT());
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