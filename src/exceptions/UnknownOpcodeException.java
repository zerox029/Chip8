package exceptions;

public class UnknownOpcodeException extends Exception {
    public UnknownOpcodeException() {}
    public UnknownOpcodeException(String msg) { super(msg); }
}
