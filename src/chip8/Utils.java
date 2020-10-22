package chip8;

public class Utils {
    public static final int MEMORY_SIZE = 4096;
    public static final int STACK_SIZE = 16;
    public static final int SCREEN_WIDTH = 64;
    public static final int SCREEN_HEIGHT = 32;

    public static final short FIRST_PROGRAM_SPACE_ADDRESS = 0x200;
    public static final short MAX_MEMORY_ADDRESS = 0xFFF;

    public static final String ROMS_PATH = "roms/";

    public static final short SPRITES_STORAGE_STARTING_ADDRESS = 0x000;

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

    private Utils(){}; //Private constructor to hide the public one
}
