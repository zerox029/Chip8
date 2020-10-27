package chip8;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.security.Key;

public class Utils {
    public static final int MEMORY_SIZE = 4096;
    public static final int STACK_SIZE = 16;
    public static final int SCREEN_WIDTH = 64;
    public static final int SCREEN_HEIGHT = 32;
    public static final int SCREEN_SCALE = 10;

    public static final int DEBUG_PANEL_WIDTH = 640;
    public static final int DEBUG_PANEL_HEIGHT = 150;

    public static final Color ON_COLOR = new Color(160, 179, 141);
    public static final Color OFF_COLOR = new Color(41, 51, 30);
    public static final Color WINDOW_COLOR = new Color(48, 48, 48);

    public static final short FIRST_PROGRAM_SPACE_ADDRESS = 0x200;
    public static final short MAX_MEMORY_ADDRESS = 0xFFF;

    public static final String ROMS_PATH = "roms/";

    public static final short SPRITES_STORAGE_STARTING_ADDRESS = 0x000;

    public static final int CPU_FREQUENCY = 500;
    public static final int PERIOD_NANOSECONDS = 1000000000 / CPU_FREQUENCY; //2 000 000
    public static final int CYCLES_FOR_REFRESHING = CPU_FREQUENCY / 60; //8.3333

    public static final float AUDIO_SAMPLE_RATE = 48500f;
    public static final int AUDIO_SAMPLE_SIZE = 8;
    public static final int AUDIO_CHANNELS = 1;

    //Number sprites
    public static final byte[] sprite_0 = new byte[]{(byte)0xF0,(byte)0x90,(byte)0x90,(byte)0x90,(byte)0xF0};
    public static final byte[] sprite_1 = new byte[]{(byte)0x20,(byte)0x60,(byte)0x20,(byte)0x20,(byte)0x70};
    public static final byte[] sprite_2 = new byte[]{(byte)0xF0,(byte)0x10,(byte)0xF0,(byte)0x80,(byte)0xF0};
    public static final byte[] sprite_3 = new byte[]{(byte)0xF0,(byte)0x10,(byte)0xF0,(byte)0x10,(byte)0xF0};
    public static final byte[] sprite_4 = new byte[]{(byte)0x90,(byte)0x90,(byte)0xF0,(byte)0x10,(byte)0x10};
    public static final byte[] sprite_5 = new byte[]{(byte)0xF0,(byte)0x80,(byte)0xF0,(byte)0x10,(byte)0xF0};
    public static final byte[] sprite_6 = new byte[]{(byte)0xF0,(byte)0x80,(byte)0xF0,(byte)0x90,(byte)0xF0};
    public static final byte[] sprite_7 = new byte[]{(byte)0xF0,(byte)0x10,(byte)0x20,(byte)0x40,(byte)0x40};
    public static final byte[] sprite_8 = new byte[]{(byte)0xF0,(byte)0x90,(byte)0xF0,(byte)0x90,(byte)0xF0};
    public static final byte[] sprite_9 = new byte[]{(byte)0xF0,(byte)0x90,(byte)0xF0,(byte)0x10,(byte)0xF0};

    //Letter sprites from A to F
    public static final byte[] sprite_a = new byte[]{(byte)0xF0,(byte)0x90,(byte)0xF0,(byte)0x90,(byte)0x90};
    public static final byte[] sprite_b = new byte[]{(byte)0xE0,(byte)0x90,(byte)0xE0,(byte)0x90,(byte)0xE0};
    public static final byte[] sprite_c = new byte[]{(byte)0xF0,(byte)0x80,(byte)0x80,(byte)0x80,(byte)0xF0};
    public static final byte[] sprite_d = new byte[]{(byte)0xE0,(byte)0x90,(byte)0x90,(byte)0x90,(byte)0xE0};
    public static final byte[] sprite_e = new byte[]{(byte)0xF0,(byte)0x80,(byte)0xF0,(byte)0x80,(byte)0xF0};
    public static final byte[] sprite_f = new byte[]{(byte)0xF0,(byte)0x80,(byte)0xF0,(byte)0x80,(byte)0x80};

    public static final int[] keyCodes = {
            KeyEvent.VK_X, //F
            KeyEvent.VK_1, //1
            KeyEvent.VK_2, //2
            KeyEvent.VK_3, //3
            KeyEvent.VK_Q, //4
            KeyEvent.VK_W, //5
            KeyEvent.VK_E, //6
            KeyEvent.VK_A, //7
            KeyEvent.VK_S, //8
            KeyEvent.VK_D, //9
            KeyEvent.VK_Z, //A
            KeyEvent.VK_C, //B
            KeyEvent.VK_4, //C
            KeyEvent.VK_R, //D
            KeyEvent.VK_F, //E
            KeyEvent.VK_V, //F
    };

    public static final int DEBUG_TOGGLE_STEP_MODE = KeyEvent.VK_F1;
    public static final int DEBUG_NEXT_INSTRUCTION_KEY = KeyEvent.VK_RIGHT;

    private Utils(){}; //Private constructor to hide the public one
}
