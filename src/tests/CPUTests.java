package tests;

import chip8.*;
import exceptions.UnknownOpcodeException;
import org.junit.jupiter.api.Test;

import java.security.Key;

import static org.junit.jupiter.api.Assertions.*;

class CPUTests {

    Memory memory = new Memory();
    Registers registers = new Registers();
    Keyboard keyboard = new Keyboard();
    CPU cpu = new CPU(memory, registers, keyboard);

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
    ///00E0
    ///Clear the display
    @Test
    void cls() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        memory.setPixelAtPosition((byte)2, (byte)5, true);
        memory.setPixelAtPosition((byte)23, (byte)14, true);

        memory.setMemoryAtAddress((short) 0x200, (byte)0x00);
        memory.setMemoryAtAddress((short) 0x201, (byte)0xE0);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        for(byte x = 0; x < Utils.SCREEN_WIDTH; x++)
        {
            for(byte y = 0; y < Utils.SCREEN_HEIGHT; y++)
            {
                assertFalse(memory.getPixelAtPosition(x, y));
            }
        }
    }

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

        assertEquals(0x5C3, registers.getPC());
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

    ///CXKK
    ///Generates a random byte and ANDs it to KK. Stores the result in Vx
    @Test
    void rand() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        cpu.toggleRandom();

        memory.setMemoryAtAddress((short) 0x200, (byte)0xC0);
        memory.setMemoryAtAddress((short) 0x201, (byte)0xDC);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        cpu.toggleRandom();

        assertEquals(0x4C, registers.getVAtAddress(0x0));
    }

    ///DXYN
    ///Display n-byte sprite starting at memory location I at (Vx, Vy), set VF = collision.
    @Test
    void drawNoCollision() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x0, (byte) 0xA);
        registers.setVAtAddress(0x1, (byte) 0xA);
        registers.setVAtAddress(0x2, (byte) 0xA);

        //Load A
        memory.setMemoryAtAddress((short) 0x200, (byte)0xF0);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x29);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        //Display A
        memory.setMemoryAtAddress((short) 0x200, (byte)0xD1);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x25);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertTrue(isSameByte((byte)0xF0,10,10));
        assertTrue(isSameByte((byte)0x90,10,11));
        assertTrue(isSameByte((byte)0xF0,10,12));
        assertTrue(isSameByte((byte)0x90,10,13));
        assertTrue(isSameByte((byte)0x90,10,14));

        assertEquals(0x0, registers.getVAtAddress(0xF));
    }

    ///DXYN
    ///Display n-byte sprite starting at memory location I at (Vx, Vy), set VF = collision.
    @Test
    void drawCollision() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        /**DISPLAYING THE FIRST A**/
        registers.setVAtAddress(0x0, (byte) 0xA);
        registers.setVAtAddress(0x1, (byte) 0xA);
        registers.setVAtAddress(0x2, (byte) 0xA);
        //Load A
        memory.setMemoryAtAddress((short) 0x200, (byte)0xF0);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x29);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();
        //Display A
        memory.setMemoryAtAddress((short) 0x200, (byte)0xD1);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x25);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        /**DISPLAYING THE SECOND A**/
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertTrue(isSameByte((byte)0x00,10,10));
        assertTrue(isSameByte((byte)0x00,10,11));
        assertTrue(isSameByte((byte)0x00,10,12));
        assertTrue(isSameByte((byte)0x00,10,13));
        assertTrue(isSameByte((byte)0x00,10,14));

        assertEquals(0x1, registers.getVAtAddress(0xF));
    }

    ///DXYN
    ///Display n-byte sprite starting at memory location I at (Vx, Vy), set VF = collision.
    @Test
    void drawOverflow() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x0, (byte) 0xA);
        registers.setVAtAddress(0x1, (byte) 62);
        registers.setVAtAddress(0x2, (byte) 0x0);

        //Load A
        memory.setMemoryAtAddress((short) 0x200, (byte)0xF0);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x29);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        //Display A
        memory.setMemoryAtAddress((short) 0x200, (byte)0xD1);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x25);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertTrue(isSameByte((byte)0xF0,62,0));
        assertTrue(isSameByte((byte)0x90,62,1));
        assertTrue(isSameByte((byte)0xF0,62,2));
        assertTrue(isSameByte((byte)0x90,62,3));
        assertTrue(isSameByte((byte)0x90,62,4));
    }

    ///EX9E
    ///Skip next instruction if key with the value of Vx is pressed.
    @Test
    void skipIfPressed() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        keyboard.toggleKeyPressed((byte) 0xA);

        memory.setMemoryAtAddress((short) 0x200, (byte)0xEA);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x9E);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals(0x202, registers.getPC());
    }

    ///FX07
    ///The value of DT is placed into Vx.
    @Test
    void loadDTOnRegister() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setDT((byte) 0x3A);

        memory.setMemoryAtAddress((short) 0x200, (byte)0xF0);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x07);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals(0x3A, registers.getVAtAddress(0));
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

    ///FX18
    ///DT is set equal to the value of Vx.
    @Test
    void loadRegisterOnST() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(6, (byte)0x4D);

        memory.setMemoryAtAddress((short) 0x200, (byte)0xF6);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x18);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals(0x4d, registers.getST());
    }

    ///FX1E
    ///The values of I and Vx are added, and the results are stored in I.
    @Test
    void addRegisterToI() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x0, (byte)0x04);
        registers.setI((short) 0x04);

        memory.setMemoryAtAddress((short) 0x200, (byte)0xF0);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x1E);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals(0x08, registers.getI());
    }

    ///FX29
    ///The values of I and Vx are added, and the results are stored in I.
    @Test
    void loadHexSpriteToI() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        registers.setVAtAddress(0x0, (byte) 0xB);

        memory.setMemoryAtAddress((short) 0x200, (byte)0xF0);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x29);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        assertEquals((short)(Utils.SPRITES_STORAGE_STARTING_ADDRESS +0x00B*5),registers.getI());
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

    ///FX55
    ///Store registers V0 through Vx in memory starting at location I
    @Test
    void loadMultipleRegistersToMemory() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        for(byte i = 0x0; i <= 0xF; i++)
        {
            registers.setVAtAddress(i, i);
        }

        memory.setMemoryAtAddress((short) 0x200, (byte)0xFF);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x55);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        for(byte i = 0x0; i <= 0xF; i++)
        {
            assertEquals(i, memory.getMemoryAtAddress((short) (registers.getI() + i)));
        }
    }

    ///FX65
    ///Read registers V0 through Vx from memory starting at location I
    @Test
    void loadMemoryToRegisters() throws UnknownOpcodeException
    {
        registers.resetAllRegisters();

        for(byte i = 0x0; i <= 0xF; i++)
        {
            memory.setMemoryAtAddress((short) (registers.getI() + i), i);
        }


        memory.setMemoryAtAddress((short) 0x200, (byte)0xFF);
        memory.setMemoryAtAddress((short) 0x201, (byte)0x65);
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();

        for(byte i = 0x0; i <= 0xF; i++)
        {
            assertEquals(i, registers.getVAtAddress(i));
        }
    }

    //Taken from ismael rodriguez's implementation
    private boolean isSameByte(byte b, int x, int y)
    {
        boolean same = true;
        for(int i = 0; i <=7; i++)
        {
            same = same && (isBitSet(b,7-i) == memory.getPixelAtPosition((byte)((x+i)%64), (byte)y));
        }
        return same;
    }

    private  Boolean isBitSet(byte b, int bit)
    {
        return (b & (1 << bit)) != 0;
    }
}