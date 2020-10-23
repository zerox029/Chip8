package chip8;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

public class Keyboard {
    private boolean[] pressedKeys;
    private int pressedCount;
    private byte lastPressed;

    private boolean stepModeActive = true;
    public boolean nextInstructionPressed = false;

    public Keyboard()
    {
        pressedKeys = new boolean[16];
        setUpListeners();
    }

    public byte getLastPressed() { return lastPressed; }
    public boolean isKeyPressed(byte keyValue) { return pressedKeys[keyValue]; }

    public boolean isStepModeActive() { return stepModeActive; }

    //For the unit tests to work
    public void toggleKeyPressed(byte keyValue)
    {
        pressedKeys[keyValue] ^= true;
        pressedCount++;
        lastPressed = keyValue;
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
                            if(stepModeActive)
                            {
                                if(e.getKeyCode() == KeyEvent.VK_RIGHT) { nextInstructionPressed = true; };
                            }
                            else
                            {
                                if(e.getKeyCode() == KeyEvent.VK_F1) { stepModeActive ^= true; }
                                else if(setKeyValue(e.getKeyCode(), true)) { pressedCount++; }
                            }
                            break;
                        case KeyEvent.KEY_RELEASED:
                            if(setKeyValue(e.getKeyCode(), false)) { pressedCount--; }
                            break;
                        default:
                            break;
                    }
                }

                return false;
            }
        });
    }

    //Taken from ismael rodriguez's implementation
    private boolean setKeyValue(int keycode, boolean value)
    {
        switch(keycode){
            case KeyEvent.VK_1:
                pressedKeys[0x1] = value;
                lastPressed=0x1;
                break;
            case KeyEvent.VK_2:
                pressedKeys[0x2] = value;
                lastPressed=0x2;
                break;
            case KeyEvent.VK_3:
                pressedKeys[0x3] = value;
                lastPressed=0x3;
                break;
            case KeyEvent.VK_4:
                pressedKeys[0xC] = value;
                lastPressed=0xC;
                break;
            case KeyEvent.VK_Q:
                pressedKeys[0x4] = value;
                lastPressed=0x4;
                break;
            case KeyEvent.VK_W:
                pressedKeys[0x5] = value;
                lastPressed=0x5;
                break;
            case KeyEvent.VK_E:
                pressedKeys[0x6] = value;
                lastPressed=0x6;
                break;
            case KeyEvent.VK_R:
                pressedKeys[0xD] = value;
                lastPressed=0xD;
                break;
            case KeyEvent.VK_A:
                pressedKeys[0x7] = value;
                lastPressed=0x7;
                break;
            case KeyEvent.VK_S:
                pressedKeys[0x8] = value;
                lastPressed=0x8;
                break;
            case KeyEvent.VK_D:
                pressedKeys[0x9] = value;
                lastPressed=0x9;
                break;
            case KeyEvent.VK_F:
                pressedKeys[0xE] = value;
                lastPressed=0xE;
                break;
            case KeyEvent.VK_Z:
                pressedKeys[0xA] = value;
                lastPressed=0xA;
                break;
            case KeyEvent.VK_X:
                pressedKeys[0x0] = value;
                lastPressed=0x0;
                break;
            case KeyEvent.VK_C:
                pressedKeys[0xB] = value;
                lastPressed=0xB;
                break;
            case KeyEvent.VK_V:
                pressedKeys[0xF] = value;
                lastPressed=0xF;
                break;
            default:
                return false;
        }
        return true;
    }

    public byte keyboardInterrupt()
    {
        while(pressedCount == 0)
        {
            try
            {
                Thread.sleep(0);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        return lastPressed;
    }
}
