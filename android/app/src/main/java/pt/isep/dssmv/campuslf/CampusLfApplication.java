package pt.isep.dssmv.campuslf;

import android.app.Application;
import pt.isep.dssmv.campuslf.data.CampusRepository;

/** Application-scoped access to the Room/Retrofit repository. */
public final class CampusLfApplication extends Application {
    private CampusRepository campusRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        campusRepository = CampusRepository.getInstance(this);
    }

    public CampusRepository getCampusRepository() {
        return campusRepository;
    }
}
