package exception;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import static exception.ResponseException.Code.ServerError;

public class ResponseException extends RuntimeException{
    private final int statusCode;
    private final String message;


    public enum Code {
        ServerError,
        ClientError,
        Unauthorized,
        Forbidden
    }

    public ResponseException(int statusCode, String message) {
        super("Error " + statusCode + ": " + message);
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }




    public static Code fromHttpStatusCode(int httpStatusCode) {
        return switch (httpStatusCode) {
            case 500 -> ServerError;
            case 400 -> Code.ClientError;
            default -> throw new IllegalArgumentException("Unknown HTTP status code: " + httpStatusCode);
        };
    }

//    public int toHttpStatusCode() {
//        return switch (statusCode) {
//            case ServerError -> 500;
//            case ClientError -> 400;
//            case Forbidden -> 403;
//            case Unauthorized -> 401;
//        };
//    }

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