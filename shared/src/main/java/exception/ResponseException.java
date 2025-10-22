package exception;

public class ResponseException extends RuntimeException{
    private final int statusCode;
    private final String message;

    public ResponseException(int statusCode, String message) {
        super("Error " + statusCode + ": " + message);
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage(){
        return message;
    }

    @Override
    public String toString() {
        return "ResponseException{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}