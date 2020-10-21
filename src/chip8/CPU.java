package chip8;

import exceptions.UnknownOpcodeException;

public class CPU {

    private Memory memory;
    private Registers registers;

    private short currentOpcode;

    public CPU(Memory memory, Registers registers)
    {
        this.memory = memory;
        this.registers = registers;
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

    public void decodeAndRunOpcode() throws UnknownOpcodeException
    {
        switch(getCurrentOpcodeFirstDigit())
        {
            case 0x0000:
                if(getCurrentOpcodeLastDigit() == 0x000E) { ret(); }
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
                if(getCurrentOpcodeLastDigit()== 0x0000) { duplicateRegister(); }
                else if(getCurrentOpcodeLastDigit() == 0x0004) { addToRegCarry(); }
                break;
            case (short)0xA000:
                loadToI();
                break;
            case (short)0xB000:
                jumpSum();
                break;
            case (short)0xF000:
                if(getCurrentOpcodeLastTwoDigit() == 0x0015) { loadRegisterOnDT(); }
                else if(getCurrentOpcodeLastTwoDigit() == 0x0033) { loadVXasBCDtoMemory(); }
                break;

            default:
                throw new UnknownOpcodeException();
        }
    }


    ////TODO: Move all the opcodes to a different class
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

    ///FX15
    ///DT is set equal to the value of Vx.
    private void loadRegisterOnDT()
    {
        byte xValue = registers.getVAtAddress(getX());

        registers.setDT(xValue);
    }

    ///FX33
    ///Store BCD representation of Vx in memory locations I, I+1, and I+2
    ///Taken from ismael rodriguez's implementation
    private void loadVXasBCDtoMemory()
    {
        byte x = (byte)((currentOpcode & 0x0F00) >> 8);
        byte vx = registers.getVAtAddress(x);
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
}
