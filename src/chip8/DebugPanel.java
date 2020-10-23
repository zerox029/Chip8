package chip8;

import javax.swing.*;
import java.awt.*;

public class DebugPanel extends JPanel {
    private Graphics graphics;
    private int width = Utils.DEBUG_PANEL_WIDTH;
    private int height = Utils.DEBUG_PANEL_HEIGHT;

    private Memory memory;
    private Registers registers;
    private Keyboard keyboard;
    private CPU cpu;

    public DebugPanel(Memory memory, Registers registers, Keyboard keyboard, CPU cpu)
    {
        this.memory = memory;
        this.registers = registers;
        this.keyboard = keyboard;
        this.cpu = cpu;
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(width, height);
    }

    public void paintScreen()
    {
        repaint();
    }

    ///TODO: Replace those with JLabels
    public void paintCurrentOpcode()
    {
        String opcode = String.format("0x%04X", cpu.getCurrentOpcode());
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Arial", 0, 14));
        graphics.drawString("Current opcode: " + opcode, 20, 20);
    }

    public void paintRegisters()
    {
        String[] nonVRegs = new String[]{ "PC: " + String.format("0x%04X", registers.getPC()),
                "I:  " + String.format("0x%04X", registers.getI()),
                "DT:  " + String.format("0x%04X", registers.getDT()),
                "ST:  " + String.format("0x%04X", registers.getST()) };

        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Arial", 0, 14));

        graphics.drawString(nonVRegs[0], 20, 60);
        graphics.drawString(nonVRegs[1], 120, 60);
        graphics.drawString(nonVRegs[2], 20, 80);
        graphics.drawString(nonVRegs[3], 120, 80);

        int currentReg = 0;
        for(int x = 0; x < 4; x++)
        {
            for(int y = 0; y < 4; y++)
            {
                String text = "V" + String.format("%01X", currentReg) + ":  " + String.format("0x%04X", registers.getVAtAddress(currentReg));
                graphics.drawString(text, x * 100 + 230, y * 20 + 20);

                currentReg++;
            }
        }
    }

    public void paintKeyboardInput()
    {
        graphics.drawString("Pressed keys: " + keyboard.getPressedCount(), 20, 120);
        graphics.drawString("Last key pressed: " + keyboard.getLastPressed(), 150, 120);
    }

    @Override
    public void paintComponent(Graphics graphics)
    {
        super.paintComponent(graphics);
        this.graphics = graphics;
        this.setBackground(Utils.WINDOW_COLOR);

        paintCurrentOpcode();
        paintRegisters();
        paintKeyboardInput();
    }
}
