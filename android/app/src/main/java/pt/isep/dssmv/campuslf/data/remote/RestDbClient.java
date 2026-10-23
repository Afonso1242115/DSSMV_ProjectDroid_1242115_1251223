package pt.isep.dssmv.campuslf.data.remote;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import pt.isep.dssmv.campuslf.BuildConfig;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/** Initialization-on-demand holder: one Retrofit and one HTTP client per application process. */
public final class RestDbClient {
    private static final Object REQUEST_LOCK = new Object();
    private static long lastRequestNanos;

    private RestDbClient() { }

    private static final class Holder {
        private static final OkHttpClient HTTP = new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    if (BuildConfig.RESTDB_API_KEY.isEmpty()
                            || BuildConfig.RESTDB_BASE_URL.contains("example.restdb.io")) {
                        throw new IOException("Configure restdb.url and restdb.apiKey in the root local.properties.");
                    }
                    throttle();
                    return chain.proceed(chain.request().newBuilder()
                            .header("x-apikey", BuildConfig.RESTDB_API_KEY)
                            .header("Accept", "application/json").build());
                }).build();
        private static final Retrofit RETROFIT = new Retrofit.Builder()
                .baseUrl(BuildConfig.RESTDB_BASE_URL).client(HTTP)
                .addConverterFactory(GsonConverterFactory.create(RestDbJson.create()))
                // Synchronous calls stay on the Repository's serialized background scheduler.
                .addCallAdapterFactory(RxJava3CallAdapterFactory.createSynchronous()).build();
        private static final RestDbService SERVICE = RETROFIT.create(RestDbService.class);
    }

    public static RestDbService service() { return Holder.SERVICE; }

    /** Serializes request start times without ever blocking the Android UI thread. */
    private static void throttle() throws IOException {
        synchronized (REQUEST_LOCK) {
            long wait = 1_100_000_000L - (System.nanoTime() - lastRequestNanos);
            if (lastRequestNanos != 0 && wait > 0) {
                try {
                    TimeUnit.NANOSECONDS.sleep(wait);
                } catch (InterruptedException error) {
                    Thread.currentThread().interrupt();
                    throw new IOException("The request was interrupted.", error);
                }
            }
            lastRequestNanos = System.nanoTime();
        }
    }
}
