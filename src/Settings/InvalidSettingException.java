package Settings;

public class InvalidSettingException extends RuntimeException{
    private String message;
    public InvalidSettingException(String message) {
        super(message);
        this.message = message;
    }
}
