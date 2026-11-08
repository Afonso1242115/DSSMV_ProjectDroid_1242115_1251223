package pt.isep.dssmv.campuslf.util;

import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.Reader;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Locale;
import pt.isep.dssmv.campuslf.BuildConfig;
import retrofit2.HttpException;

// transforma as falhas dos pedidos em mensagens para o utilizador
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

    // inclui o tamanho enviado para ajudar a perceber falhas de publicação
    public static String describePhotoPublication(Throwable error, int jpegBytes, int jsonBytes) {
        String message = String.format(Locale.US,
                "Photo publication failed. JPEG: %d bytes. JSON: %d bytes. %s",
                jpegBytes, jsonBytes, describe(error));
        String reply = serverReply(error);
        return message + (reply.isEmpty() ? "" : " Server reply: " + reply);
    }

    // lê parte da resposta e esconde a chave caso apareça na mensagem
    private static String serverReply(Throwable error) {
        if (!(error instanceof HttpException)) return "";
        retrofit2.Response<?> response = ((HttpException) error).response();
        if (response == null || response.errorBody() == null) return "";
        try (Reader reader = response.errorBody().charStream()) {
            char[] buffer = new char[4096];
            int length = 0;
            while (length < buffer.length) {
                int read = reader.read(buffer, length, buffer.length - length);
                if (read <= 0) break;
                length += read;
            }
            String text = new String(buffer, 0, length);
            String apiKey = BuildConfig.RESTDB_API_KEY;
            if (!apiKey.isEmpty()) text = text.replace(apiKey, "[redacted]");
            text = text.replaceAll("<[^>]*>", " ").replaceAll("\\s+", " ").trim();
            return text.length() > 360 ? text.substring(0, 360) + "…" : text;
        } catch (IOException | RuntimeException ignored) {
            return "";
        }
    }

    // identifica o pedido que falhou sem mostrar os dados de autenticação
    private static String requestDescription(HttpException error) {
        retrofit2.Response<?> response = error.response();
        if (response == null) return "";
        okhttp3.Request request = response.raw().request();
        return " for " + request.method() + " " + request.url().host() + request.url().encodedPath();
    }
}
