package pt.isep.dssmv.campuslf.ui;

import android.app.Activity;
import android.os.Bundle;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

import pt.isep.dssmv.campuslf.CampusLfApplication;
import pt.isep.dssmv.campuslf.data.FoundItemRepository;

/** Funcionalidade comum às Activities da aplicação. */
public abstract class BaseActivity extends Activity {
    protected FoundItemRepository repository;
    private CampusLfApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (CampusLfApplication) getApplication();
        repository = application.getRepository();
    }

    protected <T> void runAsync(Callable<T> task,
                                Consumer<T> onSuccess,
                                Consumer<Exception> onError) {
        application.executeAsync(task, result -> {
            if (!isFinishing() && !isDestroyed()) {
                onSuccess.accept(result);
            }
        }, exception -> {
            if (!isFinishing() && !isDestroyed()) {
                onError.accept(exception);
            }
        });
    }
}
