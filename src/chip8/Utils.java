package chip8;

public class Utils {
    public static final int MEMORY_SIZE = 4096;
    public static final int STACK_SIZE = 16;

    public static final short FIRST_PROGRAM_SPACE_ADDRESS = 0x200;
    public static final short MAX_MEMORY_ADDRESS = 0xFFF;

    public static final String ROMS_PATH = "roms/";

    private Utils(){}; //Private constructor to hide the public one
}
