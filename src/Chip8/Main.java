package Chip8;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException
    {
        Chip8 chip8 = new Chip8();
        chip8.loadRom("IBM Logo.ch8");
    }
}
