package pt.isep.dssmv.campuslf.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import pt.isep.dssmv.campuslf.CampusLfApplication;
import pt.isep.dssmv.campuslf.data.CampusRepository;
import pt.isep.dssmv.campuslf.data.local.entity.BuildingEntity;
import pt.isep.dssmv.campuslf.data.local.entity.CategoryEntity;
import pt.isep.dssmv.campuslf.data.local.entity.FoundItemEntity;
import pt.isep.dssmv.campuslf.util.ErrorMessages;

// mantém a pesquisa e os filtros quando o ecrã é recriado
public final class MainViewModel extends AndroidViewModel {
    private final CampusRepository repository;
    private final SavedStateHandle savedState;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<OperationState> operation = new MutableLiveData<>(
            OperationState.idle("Showing saved items."));
    private final MutableLiveData<Filters> filters;
    private final LiveData<List<FoundItemEntity>> items;

    public MainViewModel(@NonNull Application application, SavedStateHandle savedState) {
        super(application);
        this.savedState = savedState;
        repository = ((CampusLfApplication) application).getCampusRepository();
        filters = new MutableLiveData<>(new Filters(savedState.get("filter_building"),
                savedState.get("filter_category"), savedState.get("filter_search")));
        // refaz a consulta local quando a pesquisa ou os filtros mudam
        items = Transformations.switchMap(filters,
                selection -> repository.observeItems(selection.buildingId, selection.categoryId,
                        selection.searchText));
        refresh();
    }

    public LiveData<List<FoundItemEntity>> getItems() { return items; }
    public LiveData<List<BuildingEntity>> getBuildings() { return repository.observeBuildings(); }
    public LiveData<List<CategoryEntity>> getCategories() { return repository.observeCategories(); }
    public LiveData<OperationState> getOperation() { return operation; }
    public String getBuildingId() {
        Filters current = filters.getValue();
        return current == null ? null : current.buildingId;
    }
    public String getCategoryId() {
        Filters current = filters.getValue();
        return current == null ? null : current.categoryId;
    }

    public String getSearchText() {
        Filters current = filters.getValue();
        return current == null ? "" : current.searchText;
    }

    public void setSearchText(String searchText) {
        Filters previous = filters.getValue();
        String query = normalize(searchText);
        if (previous != null && previous.searchText.equals(query)) return;
        savedState.set("filter_search", query);
        filters.setValue(new Filters(getBuildingId(), getCategoryId(), query));
    }

    public boolean hasActiveFilters() {
        Filters current = filters.getValue();
        return current != null && (current.buildingId != null || current.categoryId != null
                || !current.searchText.isEmpty());
    }

    public void setFilters(String buildingId, String categoryId) {
        Filters previous = filters.getValue();
        if (previous != null && Objects.equals(previous.buildingId, buildingId)
                && Objects.equals(previous.categoryId, categoryId)) return;
        savedState.set("filter_building", buildingId);
        savedState.set("filter_category", categoryId);
        filters.setValue(new Filters(buildingId, categoryId, getSearchText()));
    }

    private static String normalize(String query) {
        return query == null ? "" : query.trim();
    }

    public void refresh() {
        OperationState current = operation.getValue();
        if (current != null && current.isRunning()) return;
        operation.setValue(OperationState.running("Refreshing items..."));
        disposables.add(repository.refresh().observeOn(AndroidSchedulers.mainThread())
                .subscribe(count -> operation.setValue(OperationState.success(
                                "Items updated (" + count + "). " + lastSyncDescription())),
                        error -> operation.setValue(OperationState.error(
                                ErrorMessages.describe(error) + " " + lastSyncDescription()))));
    }

    private String lastSyncDescription() {
        long when = repository.getLastSuccessfulRefreshMillis();
        if (when <= 0) return "No completed full synchronization yet.";
        return "Last sync: " + DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT).format(new Date(when)) + ".";
    }

    // deixa de observar os pedidos quando o ViewModel é destruído
    @Override protected void onCleared() { disposables.clear(); }

    private static final class Filters {
        final String buildingId;
        final String categoryId;
        final String searchText;
        Filters(String buildingId, String categoryId, String searchText) {
            this.buildingId = buildingId;
            this.categoryId = categoryId;
            this.searchText = normalize(searchText);
        }
    }
}
