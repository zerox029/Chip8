package chip8;

import javax.swing.*;
import java.awt.*;

public class DebugPanel extends JPanel {
    private Graphics graphics;
    private int width = Utils.DEBUG_PANEL_WIDTH;
    private int height = Utils.DEBUG_PANEL_HEIGHT;

    Memory memory;
    Registers registers;
    Keyboard keyboard;
    CPU cpu;

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

    public void paintCurrentOpcode()
    {
        String opcode = String.format("0x%04X", cpu.getCurrentOpcode());
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Arial", 0, 16));
        graphics.drawString("Current opcode: " + opcode, 10, 20);
    }

    @Override
    public void paintComponent(Graphics graphics)
    {
        super.paintComponent(graphics);
        this.graphics = graphics;
        this.setBackground(Utils.WINDOW_COLOR);

        paintCurrentOpcode();
    }
}
