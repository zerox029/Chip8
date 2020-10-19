package chip8;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Chip8 {
    private Registers registers;
    private Memory memory;
    private CPU cpu;
    private boolean running;

    public Chip8()
    {
        try
        {
            init();
            loadRom("IBM Logo.ch8");
            emulationLoop();
        }
        catch (Exception err)
        {
            err.printStackTrace();
        }
    }

    private void init()
    {
        registers = new Registers();
        memory = new Memory();
        cpu = new CPU(memory, registers);

        running = true;
    }

    public void loadRom(String title) throws IOException
    {
        File file = new File(Utils.ROMS_PATH + title);
        byte[] bytes = Files.readAllBytes(file.toPath());
        short currentAddress = Utils.FIRST_PROGRAM_SPACE_ADDRESS;

        for (byte b : bytes)
        {
            memory.setMemoryAtAddress(currentAddress, b);
            currentAddress += 0x1;
        }

        System.out.println("Successfully loaded rom " + title);
    }

    public void emulationLoop()
    {/*
        while(running)
        {
            emulateCycle();
        }*/
        emulateCycle();
    }

    private void emulateCycle()
    {
        cpu.fetchOpcode();
        cpu.decodeAndRunOpcode();
        cpu.incrementPC();
    }
}
