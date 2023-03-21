package pmm.ignacio.codernotes;

import android.app.Application;

import pmm.ignacio.codernotes.db.AppDatabase;

public class RoomApplication extends Application {

    public AppDatabase appDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        appDatabase = AppDatabase.getInstance(this);
    }
}