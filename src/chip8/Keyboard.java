package chip8;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.security.Key;

public class Keyboard extends KeyAdapter
{
    private int currentKeyPressed;
    private boolean stepModeActive = false;

    public boolean nextInstructionPressed = false;

    public int getCurrentKeyPressed() { return currentKeyPressed; }

    public boolean isStepModeActive() { return stepModeActive; }

    public void setCurrentKeyPressed(int value) { currentKeyPressed = value; }

    public Keyboard()
    {
        currentKeyPressed = -1;
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        switch (e.getKeyCode())
        {
            case Utils.DEBUG_TOGGLE_STEP_MODE:
                stepModeActive ^= true;
            default:
                currentKeyPressed = mapKeyCodeToChip8Key(e.getKeyCode());
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        currentKeyPressed = -1;
    }

    public int mapKeyCodeToChip8Key(int keycode)
    {
        for(int i = 0; i < Utils.keyCodes.length; i++)
        {
            if(Utils.keyCodes[i] == keycode)
            {
                System.out.println(String.format("0x%01X", i));
                return i;
            }
        }

        if(keycode == Utils.DEBUG_NEXT_INSTRUCTION_KEY) { nextInstructionPressed = true; }
        return 0;
    }

    public int keyboardInterrupt()
    {
        while(currentKeyPressed == -1)
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

        return currentKeyPressed;
    }
}

/*
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

public class Keyboard {
    private boolean[] pressedKeys;
    private int pressedCount;
    private byte lastPressed;

    private boolean stepModeActive = false;
    public boolean nextInstructionPressed = false;

    public Keyboard()
    {
        pressedKeys = new boolean[16];
        setUpListeners();
    }

    public byte getLastPressed() { return lastPressed; }
    public boolean isKeyPressed(byte keyValue) { return pressedKeys[keyValue]; }
    public int getPressedCount() { return pressedCount; }

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
                            System.out.println(pressedCount);
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
        boolean returnValue = true;


        switch(keycode){
            case KeyEvent.VK_1:
                returnValue = pressedKeys[0x1] != value ? true : false;
                pressedKeys[0x1] = value;
                lastPressed=0x1;
                break;
            case KeyEvent.VK_2:
                returnValue = pressedKeys[0x2] != value ? true : false;
                pressedKeys[0x2] = value;
                lastPressed=0x2;
                break;
            case KeyEvent.VK_3:
                returnValue = pressedKeys[0x3] != value ? true : false;
                pressedKeys[0x3] = value;
                lastPressed=0x3;
                break;
            case KeyEvent.VK_4:
                returnValue = pressedKeys[0xC] != value ? true : false;
                pressedKeys[0xC] = value;
                lastPressed=0xC;
                break;
            case KeyEvent.VK_Q:
                returnValue = pressedKeys[0x4] != value ? true : false;
                pressedKeys[0x4] = value;
                lastPressed=0x4;
                break;
            case KeyEvent.VK_W:
                returnValue = pressedKeys[0x5] != value ? true : false;
                pressedKeys[0x5] = value;
                lastPressed=0x5;
                break;
            case KeyEvent.VK_E:
                returnValue = pressedKeys[0x6] != value ? true : false;
                pressedKeys[0x6] = value;
                lastPressed=0x6;
                break;
            case KeyEvent.VK_R:
                returnValue = pressedKeys[0xD] != value ? true : false;
                pressedKeys[0xD] = value;
                lastPressed=0xD;
                break;
            case KeyEvent.VK_A:
                returnValue = pressedKeys[0x7] != value ? true : false;
                pressedKeys[0x7] = value;
                lastPressed=0x7;
                break;
            case KeyEvent.VK_S:
                returnValue = pressedKeys[0x8] != value ? true : false;
                pressedKeys[0x8] = value;
                lastPressed=0x8;
                break;
            case KeyEvent.VK_D:
                returnValue = pressedKeys[0x9] != value ? true : false;
                pressedKeys[0x9] = value;
                lastPressed=0x9;
                break;
            case KeyEvent.VK_F:
                returnValue = pressedKeys[0xE] != value ? true : false;
                pressedKeys[0xE] = value;
                lastPressed=0xE;
                break;
            case KeyEvent.VK_Z:
                returnValue = pressedKeys[0xA] != value ? true : false;
                pressedKeys[0xA] = value;
                lastPressed=0xA;
                break;
            case KeyEvent.VK_X:
                returnValue = pressedKeys[0x0] != value ? true : false;
                pressedKeys[0x0] = value;
                lastPressed=0x0;
                break;
            case KeyEvent.VK_C:
                returnValue = pressedKeys[0xB] != value ? true : false;
                pressedKeys[0xB] = value;
                lastPressed=0xB;
                break;
            case KeyEvent.VK_V:
                returnValue = pressedKeys[0xF] != value ? true : false;
                pressedKeys[0xF] = value;
                lastPressed=0xF;
                break;
            default:
                return false;
        }

        return returnValue;
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
*/