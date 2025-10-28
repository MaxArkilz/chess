package exception;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ResponseException extends RuntimeException{
    private final int statusCode;
    private final String message;


    public enum Code {
        ServerError,
        ClientError,
    }

    final private Code code;

    public ResponseException(int statusCode, String message, Code code) {
        super("Error " + statusCode + ": " + message);
        this.statusCode = statusCode;
        this.message = message;
        this.code = code;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", code));
    }

    public static ResponseException fromJson(String json) {
        var map = new Gson().fromJson(json, HashMap.class);
        var status = Code.valueOf(map.get("status").toString());
        String message = map.get("message").toString();
        return new ResponseException(status, message);
    }

    public Code code() {
        return code;
    }

    public static Code fromHttpStatusCode(int httpStatusCode) {
        return switch (httpStatusCode) {
            case 500 -> Code.ServerError;
            case 400 -> Code.ClientError;
            default -> throw new IllegalArgumentException("Unknown HTTP status code: " + httpStatusCode);
        };
    }

    public int toHttpStatusCode() {
        return switch (code) {
            case ServerError -> 500;
            case ClientError -> 400;
        };
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