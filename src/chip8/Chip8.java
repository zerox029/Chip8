package chip8;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

public class Chip8 {
    private Registers registers;
    private Memory memory;
    private CPU cpu;
    private Display display;
    private DebugPanel debugPanel;
    private Keyboard keyboard;

    private static final Logger LOGGER = Logger.getLogger(Chip8.class.getName());

    private boolean running;

    public Chip8()
    {
        try
        {
            init();
            loadRom("Connect 4 [David Winter].ch8");
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
        keyboard = new Keyboard();
        cpu = new CPU(memory, registers, keyboard);
        createDisplay();

        running = true;
    }

    private void createDisplay()
    {
        display = new Display(memory);
        debugPanel = new DebugPanel(memory, registers, keyboard, cpu);

        JFrame frame = new JFrame("Chip8");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(display, BorderLayout.NORTH);
        frame.add(debugPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setResizable(false);
        frame.setBackground(Utils.WINDOW_COLOR);
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

    public void emulationLoop() throws InterruptedException
    {
        long startTime;
        long endTime;
        long passedTime = 0;

        int emulatedCycles = 0;
        int refreshCycles = 0;

        while(running)
        {
            if(!keyboard.isStepModeActive())
            {
                startTime = System.nanoTime();

                emulateCycle();

                if(refreshCycles % Utils.CYCLES_FOR_REFRESHING == 0)
                {
                    refreshCycles = 0;

                    display.paintScreen();
                    debugPanel.paintScreen();
                    registers.setDT((byte) (registers.getDT() - 0x01));

                    //Sound stuff
                }

                endTime = System.nanoTime();
                refreshCycles++;

                waitForEndOfCycle(startTime, endTime);
            }
            else
            {
                if(!keyboard.nextInstructionPressed)
                {
                    Thread.sleep(0);
                }
                else
                {
                    emulateCycle();

                    display.paintScreen();
                    debugPanel.paintScreen();
                    registers.setDT((byte) (registers.getDT() - 0x01));

                    keyboard.nextInstructionPressed = false;
                }
            }
        }
    }

    private void waitForEndOfCycle(long startTime, long endTime)
    {
        long nanosecondsToWait = Utils.PERIOD_NANOSECONDS - (endTime - startTime);
        long initNanoseconds = System.nanoTime();
        long targetNanoseconds = initNanoseconds + nanosecondsToWait;
        while(System.nanoTime() < targetNanoseconds)
        {
            try
            {
                Thread.sleep(0);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void emulateCycle()
    {
        try
        {
            cpu.fetchOpcode();
            cpu.incrementPC();
            cpu.decodeAndRunOpcode();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
