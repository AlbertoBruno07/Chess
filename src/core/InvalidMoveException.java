package core;

public class InvalidMoveException extends RuntimeException{

    private String msg;

    public InvalidMoveException(String msg) {
        super(msg);
        this.msg = msg;
    }
}
