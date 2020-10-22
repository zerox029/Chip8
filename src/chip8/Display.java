package chip8;

import javax.swing.*;
import java.awt.*;

public class Display extends JPanel {
    private Graphics graphics;
    private int width = Utils.SCREEN_WIDTH * Utils.SCREEN_SCALE;
    private int height = Utils.SCREEN_HEIGHT * Utils.SCREEN_SCALE;

    private Memory memory;

    public Display(Memory memory)
    {
        this.memory = memory;
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(width, height);
    }

    public void setPixel(int x, int y, boolean state)
    {
        if(state) { graphics.setColor(Utils.ON_COLOR); }
        else { graphics.setColor(Utils.OFF_COLOR); }

        graphics.fillRect(x * Utils.SCREEN_SCALE, y * Utils.SCREEN_SCALE, Utils.SCREEN_SCALE, Utils.SCREEN_SCALE);
    }

    private void fillScreen()
    {
        for(byte x = 0; x < Utils.SCREEN_WIDTH; x++)
        {
            for(byte y = 0; y < Utils.SCREEN_HEIGHT; y++)
            {
                setPixel(x, y, memory.getPixelAtPosition(x, y));
            }
        }
    }

    public void paintScreen()
    {
        repaint();
    }

    @Override
    public void paintComponent(Graphics graphics)
    {
        super.paintComponent(graphics);
        this.graphics = graphics;

        graphics.setColor(Utils.OFF_COLOR);
        graphics.fillRect(0, 0, width, height);

        fillScreen();
    }
}
