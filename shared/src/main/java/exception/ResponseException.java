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