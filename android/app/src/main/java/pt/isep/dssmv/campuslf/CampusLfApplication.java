package pt.isep.dssmv.campuslf;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import pt.isep.dssmv.campuslf.data.FoundItemRepository;
import pt.isep.dssmv.campuslf.data.CampusRepository;

/** Mantém dependências partilhadas e executa trabalho de disco fora da thread da interface. */
public final class CampusLfApplication extends Application {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private FoundItemRepository repository;
    private CampusRepository campusRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        repository = new FoundItemRepository(this);
        campusRepository = CampusRepository.getInstance(this);
    }

    public FoundItemRepository getRepository() {
        return repository;
    }

    public CampusRepository getCampusRepository() {
        return campusRepository;
    }

    public <T> void executeAsync(Callable<T> task,
                                 Consumer<T> onSuccess,
                                 Consumer<Exception> onError) {
        executor.execute(() -> {
            try {
                T result = task.call();
                mainHandler.post(() -> onSuccess.accept(result));
            } catch (Exception exception) {
                mainHandler.post(() -> onError.accept(exception));
            }
        });
    }
}
