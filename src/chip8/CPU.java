package chip8;

import java.util.Random;
import exceptions.UnknownOpcodeException;

public class CPU {

    private Memory memory;
    private Registers registers;

    private short currentOpcode;

    private boolean randomEnabled = true;
    private Random random;

    public CPU(Memory memory, Registers registers)
    {
        this.memory = memory;
        this.registers = registers;

        this.random = new Random();
    }

    public short getCurrentOpcode() { return currentOpcode; }

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

    private short getCurrentOpcodeFirstDigit() { return (short)(currentOpcode & 0xF000); }
    private short getCurrentOpcodeLastDigit() { return (short)(currentOpcode & 0x000F); }
    private short getCurrentOpcodeLastTwoDigit() { return (short)(currentOpcode & 0x00FF); }
    private short getNNN() { return (short)(currentOpcode & 0x0FFF); }
    private byte getKK() { return (byte)(currentOpcode & 0x00FF); }
    private byte getX() { return (byte)((currentOpcode & 0x0F00) >> 8); }
    private byte getY() { return (byte)((currentOpcode & 0x00F0) >> 4); }
    private int byteToUnsignedInt(byte val) { return val & 0xFF; }

    public void toggleRandom() { randomEnabled ^= true; }

    public void decodeAndRunOpcode() throws UnknownOpcodeException
    {
        switch(getCurrentOpcodeFirstDigit())
        {
            case 0x0000:
                if(getCurrentOpcodeLastDigit() == 0xE) { ret(); }
                break;
            case 0x1000:
                jump();
                break;
            case 0x2000:
                call();
                break;
            case 0x3000:
                skipOnEqualByte();
                break;
            case 0x4000:
                skipOnNonEqualByte();
                break;
            case 0x5000:
                skipOnEqualRegister();
                break;
            case 0x6000:
                loadToRegister();
                break;
            case 0x7000:
                addOnRegister();
                break;
            case (short)0x8000:
                if(getCurrentOpcodeLastDigit()== 0x0) { duplicateRegister(); }
                else if(getCurrentOpcodeLastDigit() == 0x1) { orRegister(); }
                else if(getCurrentOpcodeLastDigit() == 0x2) { andRegister(); }
                else if(getCurrentOpcodeLastDigit() == 0x3) { xorRegister(); }
                else if(getCurrentOpcodeLastDigit() == 0x4) { addToRegCarry(); }
                else if(getCurrentOpcodeLastDigit() == 0x5) { sub(); }
                else if(getCurrentOpcodeLastDigit() == 0x6) { shr(); }
                else if(getCurrentOpcodeLastDigit() == 0x7) { subn(); }
                else if(getCurrentOpcodeLastDigit() == 0xE) { shl(); }
                break;
            case (short)0x9000:
                sneRegister();
                break;
            case (short)0xA000:
                loadToI();
                break;
            case (short)0xB000:
                jumpSum();
                break;
            case (short)0xC000:
                rand();
                break;
            case (short)0xF000:
                if(getCurrentOpcodeLastTwoDigit() == 0x07) { loadDTOnRegister(); }
                else if(getCurrentOpcodeLastTwoDigit() == 0x15) { loadRegisterOnDT(); }
                else if(getCurrentOpcodeLastTwoDigit() == 0x18) { loadRegisterOnST(); }
                else if(getCurrentOpcodeLastTwoDigit() == 0x1E) { addRegisterToI(); }
                else if(getCurrentOpcodeLastTwoDigit() == 0x33) { loadVXasBCDtoMemory(); }
                else if(getCurrentOpcodeLastTwoDigit() == 0x55) { loadMultipleRegistersToMemory(); }
                else if(getCurrentOpcodeLastTwoDigit() == 0x65) { loadMemoryToRegisters(); }
                break;

            default:
                throw new UnknownOpcodeException();
        }
    }

    //region OPCODES
    ///00EE
    ///Returns from a subroutine
    private void ret()
    {
        short stackTopAddress = memory.getStackAtValue(registers.getSP());
        registers.setPC(stackTopAddress);
        registers.setSP((byte)(registers.getSP() - 0x01));
    }

    ///1NNN
    ///Sets the PC to NNN
    private void jump()
    {
        registers.setPC(getNNN());
    }

    ///2NNN
    ///Calls Calls subroutine at NNN
    private void call()
    {
        registers.setSP((byte)(registers.getSP() + 1));
        memory.setStackAtValue(registers.getSP(), registers.getPC());

        registers.setPC(getNNN());
    }

    ///3XKK
    ///Compares register Vx to kk, if they are equal, skip the next instruction
    private void skipOnEqualByte()
    {
        byte xValue = registers.getVAtAddress(getX());

        if(getKK() == xValue) { registers.setPC((short) (registers.getPC() + 0x2)); }
    }

    ///4XKK
    ///Compares register Vx to kk, if they are equal, skip the next instruction
    private void skipOnNonEqualByte()
    {
        byte xValue = registers.getVAtAddress(getX());

        if(getKK() != xValue) { registers.setPC((short) (registers.getPC() + 0x2)); }
    }

    ///5XY0
    ///Compares register Vx to Vy, if equal, skip the next instruction
    private void skipOnEqualRegister()
    {
        short xValue = registers.getVAtAddress(getX());
        short yValue = registers.getVAtAddress(getY());

        if(xValue == yValue) { registers.setPC((short) (registers.getPC() + 0x2)); }
    }

    ///6XKK
    ///Puts the value of KK into Vx
    private void loadToRegister()
    {
        registers.setVAtAddress(getX(), getKK());
    }

    ///7XKK
    ///Adds the value kk to the value of register Vx, then stores the result in Vx.
    private void addOnRegister()
    {
        byte x = getX();
        byte xValue = registers.getVAtAddress(x);
        byte sum = (byte) (getKK() + xValue);

        registers.setVAtAddress(x, sum);
    }

    ///8XY0
    ///Stores the value of register Vy in register Vx.
    private void duplicateRegister()
    {
        byte yValue = registers.getVAtAddress(getY());

        registers.setVAtAddress(getX(), yValue);
    }

    ///8XY1
    ///Performs a bitwise OR on the values of Vx and Vy, then stores the result in Vx
    private void orRegister()
    {
        byte xValue = registers.getVAtAddress(getX());
        byte yValue = registers.getVAtAddress(getY());
        byte orResult = (byte) (xValue | yValue);

        registers.setVAtAddress(getX(), orResult);
    }

    ///8XY2
    ///Performs a bitwise OR on the values of Vx and Vy, then stores the result in Vx
    private void andRegister()
    {
        byte xValue = registers.getVAtAddress(getX());
        byte yValue = registers.getVAtAddress(getY());
        byte andResult = (byte) (xValue & yValue);

        registers.setVAtAddress(getX(), andResult);
    }

    ///8XY3
    ///Performs a bitwise exclusive OR on the values of Vx and Vy, then stores the result in Vx
    private void xorRegister()
    {
        byte xValue = registers.getVAtAddress(getX());
        byte yValue = registers.getVAtAddress(getY());
        byte xorResult = (byte) (xValue ^ yValue);

        registers.setVAtAddress(getX(), xorResult);
    }

    ///8XY4
    ///The values of Vx and Vy are added together. If the result is greater than 8 bits (i.e., > 255,) VF is set to 1,
    ///otherwise 0. Only the lowest 8 bits of the result are kept, and stored in Vx
    private void addToRegCarry()
    {
        byte x = getX();
        byte y = getY();

        int uSign_xValue = registers.getVAtAddress(x) & 0xff;
        int uSign_yValue = registers.getVAtAddress(y) & 0xff;
        int uSign_product = (registers.getVAtAddress(x) + registers.getVAtAddress(y)) & 0xff;

        if(uSign_yValue > (0xFF - uSign_xValue))
        {
            registers.setVAtAddress(0xF, (byte) 1);
        }
        else
        {
            registers.setVAtAddress(0xF, (byte) 0);
        }

        registers.setVAtAddress(x, (byte)(uSign_product));
    }

    ///8XY5
    ///If Vx > Vy, then VF is set to 1, otherwise 0. Then Vy is subtracted from Vx, and the results stored in Vx
    private void sub()
    {
        byte vx = registers.getVAtAddress(getX());
        byte vy = registers.getVAtAddress(getY());
        int uSign_vx = byteToUnsignedInt(vx);
        int uSign_vy = byteToUnsignedInt(vy);

        if(uSign_vx > uSign_vy) { registers.setVAtAddress(0xF, (byte) 0x1); }
        else { registers.setVAtAddress(0xF, (byte) 0x0); }

        byte result = (byte) (vx - vy);
        registers.setVAtAddress(getX(), result);
    }

    ///8XY6
    ///Shifts VX right by one. VF is set to the value of the least significant bit of VX before the shift.
    private void shr()
    {
        byte lsb = (byte)(registers.getVAtAddress(getX()) & (byte)0x01);
        byte vx = registers.getVAtAddress(getX());
        int uSign_vx = byteToUnsignedInt(vx);

        registers.setVAtAddress(0xF, lsb);
        registers.setVAtAddress(getX(), (byte)(uSign_vx >>> 1));
    }

    ///8XY7
    ///If Vy > Vx, then VF is set to 1, otherwise 0. Then Vx is subtracted from Vy, and the results stored in Vx
    private void subn()
    {
        byte vx = registers.getVAtAddress(getX());
        byte vy = registers.getVAtAddress(getY());
        int uSign_vx = byteToUnsignedInt(vx);
        int uSign_vy = byteToUnsignedInt(vy);

        if(uSign_vy > uSign_vx) { registers.setVAtAddress(0xF, (byte) 0x1); }
        else { registers.setVAtAddress(0xF, (byte) 0x0); }

        byte result = (byte) (vx - vy);
        registers.setVAtAddress(getX(), result);
    }

    ///8XYE
    ///If the most-significant bit of Vx is 1, then VF is set to 1, otherwise to 0. Then Vx is multiplied by 2.
    private void shl()
    {
        byte msb = (byte)(registers.getVAtAddress(getX()) & (byte)0x80);
        byte vx = registers.getVAtAddress(getX());
        int uSign_vx = byteToUnsignedInt(vx);

        if(msb != 0) { msb = (byte)0x01; }

        registers.setVAtAddress(0xF, msb);
        registers.setVAtAddress(getX(), (byte)(uSign_vx << 1));
    }

    ///9XY0
    ///The values of Vx and Vy are compared, and if they are not equal, the PC is increased by 2.
    private void sneRegister()
    {
        byte vx = registers.getVAtAddress(getX());
        byte vy = registers.getVAtAddress(getY());

        System.out.println(registers.getPC());
        if(vx != vy) { registers.setPC((short) (registers.getPC() + 0x2)); }
    }

    ///ANNN
    ///Sets I to the address NNN.
    private void loadToI()
    {
        registers.setI(getNNN());
    }

    ///BNNN
    ///Jump to location NNN + V0
    //The PC is set to NNN plus the value of V0.
    private void jumpSum()
    {
        int uSign_v0 = registers.getVAtAddress(0) & 0xFF;
        int uSign_nnn = getNNN() & 0xFFF;

        registers.setPC((short)(uSign_nnn + uSign_v0));
    }

    ///CXKK
    ///Generates a random byte and ANDs it to KK. Stores the result in Vx
    private void rand()
    {
        byte rnd = (byte)random.nextInt(256);

        if(!randomEnabled) { rnd = 0x4E; }

        registers.setVAtAddress(getX(), (byte)(rnd & getKK()));
    }

    ///FX07
    ///The value of DT is placed into Vx.
    private void loadDTOnRegister()
    {
        registers.setVAtAddress(getX(), registers.getDT());
    }

    ///FX15
    ///DT is set equal to the value of Vx.
    private void loadRegisterOnDT()
    {
        byte vx = registers.getVAtAddress(getX());

        registers.setDT(vx);
    }

    ///FX18
    ///ST is set equal to the value of Vx.
    private void loadRegisterOnST()
    {
        byte vx = registers.getVAtAddress(getX());

        registers.setST(vx);
    }

    ///FX1E
    ///ST is set equal to the value of Vx.
    private void addRegisterToI()
    {
        byte vx = registers.getVAtAddress(getX());

        registers.setI((short) (registers.getI() + vx));
    }

    ///FX33
    ///Store BCD representation of Vx in memory locations I, I+1, and I+2
    ///Taken from ismael rodriguez's implementation
    private void loadVXasBCDtoMemory()
    {
        byte vx = registers.getVAtAddress(getX());
        int uSign_vx = vx & 0xFF;

        int hundreds = uSign_vx / 100;
        uSign_vx = uSign_vx - hundreds*100;

        int tens = uSign_vx / 10;
        uSign_vx = uSign_vx - tens*10;

        int units = uSign_vx;

        memory.setMemoryAtAddress(registers.getI(), (byte)hundreds);
        memory.setMemoryAtAddress((short)(registers.getI() + 1), (byte)tens);
        memory.setMemoryAtAddress((short)(registers.getI() + 2), (byte)units);
    }

    ///FX55
    ///Store registers V0 through Vx in memory starting at location I
    private void loadMultipleRegistersToMemory()
    {
        for(byte reg = 0; reg <= getX(); reg++)
        {
            short address = (short)(registers.getI() + reg);
            byte value = registers.getVAtAddress(reg);
            memory.setMemoryAtAddress(address, value);
        }
    }

    ///FX65
    ///Read registers V0 through Vx from memory starting at location I
    private void loadMemoryToRegisters()
    {
        for(byte reg = 0; reg <= getX(); reg++){
            byte value = memory.getMemoryAtAddress((short) (registers.getI() + reg));
            registers.setVAtAddress(reg, value);
        }
    }
    //endregion
}
