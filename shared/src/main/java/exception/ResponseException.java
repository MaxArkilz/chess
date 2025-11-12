package exception;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import static exception.ResponseException.Code.ServerError;

public class ResponseException extends RuntimeException{
    private final int statusCode;
    private final String message;

    public static ResponseException fromJson(String json) {
        var map = new Gson().fromJson(json, HashMap.class);
        Object messageObj = map.get("message");
        String message = (messageObj != null) ? messageObj.toString() : "Unknown error";
        return new ResponseException(500, message); // Use actual status code if available
    }

    public static int fromHttpStatusCode(int status) {
        return status;
    }


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