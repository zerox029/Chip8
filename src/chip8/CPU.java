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

    public void decodeAndRunOpcode() throws UnknownOpcodeException
    {
        switch(currentOpcode & 0xF000)
        {
            case 0x0000:
                switch (currentOpcode & 0x000F)
                {
                    case 0x0000:
                        cls();
                        break;
                    case 0x00e:
                        ret();
                        break;
                }
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
            case 0x8000:
                switch (currentOpcode & 0x000F)
                {
                    case 0x0000:
                        duplicateRegister();
                        break;
                    case 0x0004:
                        addToRegCarry();
                        break;
                }
                break;
            case 0xA000:
                loadToI();
                break;
            case 0xF000:
                switch (currentOpcode & 0x00FF)
                {
                    case 0x0033:
                        loadVXasBCDtoMemory();
                        break;
                }
                break;

            default:
                throw new UnknownOpcodeException();
        }
    }


    ////TODO: Move all the opcodes to a different class
    ///00E0
    ///Clears the screen
    private void cls()
    {
        System.out.println("Clear screen");
    }

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
        short value = (short)(currentOpcode & 0x0FFF);

        registers.setPC(value);
    }

    ///2NNN
    ///Calls Calls subroutine at NNN
    private void call()
    {
        short value = (short)(currentOpcode & 0x0FFF); //set value to NNN

        registers.setSP((byte)(registers.getSP() + 1));
        memory.setStackAtValue(registers.getSP(), registers.getPC());

        registers.setPC(value);
    }

    ///3XKK
    ///Compares register Vx to kk, if they are equal, skip the next instruction
    private void skipOnEqualByte()
    {
        byte kk = (byte)(currentOpcode & 0x00FF);
        byte x = (byte)((currentOpcode & 0x0F00) >> 8);
        byte xValue = registers.getVAtAddress(x);

        if(kk == xValue) { registers.setPC((short) (registers.getPC() + 0x2)); }
    }

    ///4XKK
    ///Compares register Vx to kk, if they are equal, skip the next instruction
    private void skipOnNonEqualByte()
    {
        byte kk = (byte)(currentOpcode & 0x00FF);
        byte x = (byte)((currentOpcode & 0x0F00) >> 8);
        byte xValue = registers.getVAtAddress(x);

        if(kk != xValue) { registers.setPC((short) (registers.getPC() + 0x2)); }
    }

    ///5XY0
    ///Compares register Vx to Vy, if equal, skip the next instruction
    private void skipOnEqualRegister()
    {
        byte x = (byte)((currentOpcode & 0x0F00) >> 8);
        byte y = (byte)((currentOpcode & 0x00F0) >> 4);
        short xValue = registers.getVAtAddress(x);
        short yValue = registers.getVAtAddress(y);

        if(xValue == yValue) { registers.setPC((short) (registers.getPC() + 0x2)); }
    }

    ///6XKK
    ///Puts the value of KK into Vx
    private void loadToRegister()
    {
        byte kk = (byte)((currentOpcode & 0x00FF));
        byte x = (byte)((currentOpcode & 0x0F00) >> 8);

        registers.setVAtAddress(x, kk);
    }

    ///7XKK
    ///Adds the value kk to the value of register Vx, then stores the result in Vx.
    private void addOnRegister()
    {
        byte kk = (byte)((currentOpcode & 0x00FF));
        byte x = (byte)((currentOpcode & 0x0F00) >> 8);
        byte xValue = registers.getVAtAddress(x);
        byte sum = (byte) (kk + xValue);

        registers.setVAtAddress(x, sum);
    }

    ///8XY0
    ///Stores the value of register Vy in register Vx.
    private void duplicateRegister()
    {
        byte x = (byte)((currentOpcode & 0x0F00) >> 8);
        byte y = (byte)((currentOpcode & 0x00F0) >> 4);
        byte yValue = registers.getVAtAddress(y);

        registers.setVAtAddress(x, yValue);
    }

    ///8XY4
    ///The values of Vx and Vy are added together. If the result is greater than 8 bits (i.e., > 255,) VF is set to 1,
    ///otherwise 0. Only the lowest 8 bits of the result are kept, and stored in Vx
    private void addToRegCarry()
    {
        byte x = (byte)((currentOpcode & 0x0F00) >> 8);
        byte y = (byte)((currentOpcode & 0x00F0) >> 4);

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
        short value = (short)(currentOpcode & 0x0FFF);
        registers.setI(value);
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
