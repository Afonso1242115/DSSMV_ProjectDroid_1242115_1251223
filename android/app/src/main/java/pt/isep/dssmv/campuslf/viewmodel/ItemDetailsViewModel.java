package pt.isep.dssmv.campuslf.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import pt.isep.dssmv.campuslf.CampusLfApplication;
import pt.isep.dssmv.campuslf.data.CampusRepository;
import pt.isep.dssmv.campuslf.data.local.entity.FoundItemEntity;
import pt.isep.dssmv.campuslf.util.ErrorMessages;

public final class ItemDetailsViewModel extends AndroidViewModel {
    public static final String ITEM_ID_KEY = "item_id";
    private final CampusRepository repository;
    private final String itemId;
    private final LiveData<FoundItemEntity> item;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<OperationState> operation = new MutableLiveData<>(
            OperationState.idle("Showing saved details."));

    public ItemDetailsViewModel(@NonNull Application application, SavedStateHandle state) {
        super(application);
        repository = ((CampusLfApplication) application).getCampusRepository();
        itemId = state.get(ITEM_ID_KEY);
        item = itemId == null ? new MutableLiveData<>(null) : repository.observeItem(itemId);
        if (itemId == null || itemId.isEmpty()) operation.setValue(OperationState.error("No item was selected."));
        else refresh();
    }

    public LiveData<FoundItemEntity> getItem() { return item; }
    public LiveData<OperationState> getOperation() { return operation; }

    public void refresh() {
        if (itemId == null || itemId.isEmpty() || operation.getValue().isRunning()) return;
        operation.setValue(OperationState.running("Refreshing details..."));
        disposables.add(repository.refreshItem(itemId).observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> operation.setValue(OperationState.success("Details updated.")),
                        error -> operation.setValue(OperationState.error(ErrorMessages.describe(error)))));
    }

    @Override protected void onCleared() { disposables.clear(); }
}
