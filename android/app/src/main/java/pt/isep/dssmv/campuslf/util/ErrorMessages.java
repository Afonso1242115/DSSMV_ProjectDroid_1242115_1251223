package pt.isep.dssmv.campuslf.util;

import com.google.gson.JsonParseException;
import java.io.IOException;
import retrofit2.HttpException;

public final class ErrorMessages {
    private ErrorMessages() { }

    public static String describe(Throwable error) {
        if (error instanceof HttpException) {
            int code = ((HttpException) error).code();
            if (code == 401 || code == 403) return "Access denied (HTTP " + code
                    + "). Check the API key and its collection permissions.";
            if (code == 404) return "The record or collection was not found (HTTP 404).";
            if (code == 429) return "Too many requests (HTTP 429). Wait before refreshing again.";
            if (code == 400) return "The server rejected the fields (HTTP 400). Check the lookup and photo fields.";
            return "The server returned HTTP " + code + ".";
        }
        if (error instanceof JsonParseException) {
            return "Unexpected API data. Check the collection fields and lookup types.";
        }
        if (error instanceof IllegalArgumentException) return error.getMessage();
        if (error instanceof IOException) {
            String message = error.getMessage();
            if (message != null && (message.startsWith("Configure ") || message.startsWith("Photo ")))
                return message;
            return "Connection failed. Cached data is still available. Check the list before resubmitting an item.";
        }
        return "The operation could not be completed. Please try again.";
    }
}
