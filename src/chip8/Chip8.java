package chip8;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

public class Chip8 {
    private Registers registers;
    private Memory memory;
    private CPU cpu;
    private Display display;
    private Keyboard keyboard;

    private static final Logger LOGGER = Logger.getLogger(Chip8.class.getName());

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
        keyboard = new Keyboard();
        createDisplay(memory);
    }

    private void createDisplay(Memory memory)
    {
        display = new Display(memory);
        JFrame frame = new JFrame("Chip8");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(display);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
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

        LOGGER.info("Successfully loaded rom");
    }

    public void emulationLoop()
    {
        emulateCycle();
    }

    private void emulateCycle()
    {
        try
        {
            cpu.fetchOpcode();
            cpu.decodeAndRunOpcode();
            cpu.incrementPC();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
