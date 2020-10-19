package Chip8;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Chip8 {
    private Registers registers;
    private Memory memory;

    public Chip8()
    {
        init();
    }

    private void init()
    {
        registers = new Registers();
        memory = new Memory();
    }

    public void loadRom(String title) throws IOException
    {
        File file = new File("roms/" + title);
        byte[] bytes = Files.readAllBytes(file.toPath());
        short currentAddress = 0x200;

        for (byte b : bytes)
        {
            memory.setMemoryAtAddress(currentAddress, b);
            currentAddress += 0x1;
        }

        System.out.println("Successfully loaded rom");
    }
}
