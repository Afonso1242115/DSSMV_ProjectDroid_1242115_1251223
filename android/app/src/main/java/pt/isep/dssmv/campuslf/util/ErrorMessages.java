package pt.isep.dssmv.campuslf.util;

import com.google.gson.JsonParseException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import retrofit2.HttpException;

public final class ErrorMessages {
    private ErrorMessages() { }

    public static String describe(Throwable error) {
        if (error instanceof HttpException) {
            HttpException http = (HttpException) error;
            int code = http.code();
            if (code == 401 || code == 403) return "Access denied (HTTP " + code
                    + ")" + requestDescription(http)
                    + ". Check the API key permissions for this operation.";
            if (code == 404) return "The record or collection was not found (HTTP 404).";
            if (code == 429) return "Too many requests (HTTP 429). Wait before refreshing again.";
            if (code == 400) return "The server rejected the fields (HTTP 400). Check the lookup and photo fields.";
            return "The server returned HTTP " + code + ".";
        }
        if (error instanceof JsonParseException) {
            return "Unexpected API data. Check the collection fields and lookup types.";
        }
        if (error instanceof IllegalArgumentException) return error.getMessage();
        if (error instanceof SocketTimeoutException) {
            return "Request timed out. Check connectivity. Previously saved items remain available.";
        }
        if (error instanceof UnknownHostException || error instanceof ConnectException) {
            return "Network unavailable. Previously saved items remain available offline.";
        }
        if (error instanceof IOException) {
            String message = error.getMessage();
            if (message != null && (message.startsWith("Configure ") || message.startsWith("Photo ")))
                return message;
            return "Connection failed. Saved items may be available offline if already synchronized. "
                    + "Check the list before resubmitting an item.";
        }
        return "The operation could not be completed. Please try again.";
    }

    /** Only the method and path are shown; headers and query parameters may contain credentials. */
    private static String requestDescription(HttpException error) {
        retrofit2.Response<?> response = error.response();
        if (response == null) return "";
        okhttp3.Request request = response.raw().request();
        return " for " + request.method() + " " + request.url().encodedPath();
    }
}
