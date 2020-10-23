package chip8;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {
    Window(String title, JPanel display, JPanel debugPanel)
    {
        super(title);

        this.add(display, BorderLayout.NORTH);
        this.add(debugPanel, BorderLayout.SOUTH);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        //setMenus();
    }

    private void setMenus()
    {
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem load;

        menuBar = new JMenuBar();
        menu = new JMenu("File");
        load = new JMenuItem("Load");

        menu.add(load);
        menuBar.add(menu);

        //this.setJMenuBar(menuBar);
    }
}
