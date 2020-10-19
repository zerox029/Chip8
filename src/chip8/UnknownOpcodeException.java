package chip8;

public class UnknownOpcodeException extends Exception {
    public UnknownOpcodeException() {}
    public UnknownOpcodeException(String msg) { super(msg); }
}
