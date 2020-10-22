package chip8;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.security.Key;

public class Keyboard {
    private boolean[] pressedKeys;
    private int pressedCount;
    private byte lastPressed;

    public Keyboard()
    {
        pressedKeys = new boolean[16];
        setUpListeners();
    }

    //https://stackoverflow.com/questions/18037576/how-do-i-check-if-the-user-is-pressing-a-key
    private void setUpListeners()
    {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e)
            {
                synchronized (Keyboard.class)
                {
                    switch (e.getID())
                    {
                        case KeyEvent.KEY_PRESSED:
                            System.out.println("ds");
                            break;
                        default:
                            break;
                    }
                }

                return false;
            }
        });
    }
}
