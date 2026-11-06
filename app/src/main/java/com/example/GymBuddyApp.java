package com.example;

import android.app.Application;
import com.example.repository.GymBuddyRepository;
import com.example.utils.AppPreferences;
import com.example.utils.ThemeHelper;

public class GymBuddyApp extends Application {

    private static GymBuddyApp instance;
    private GymBuddyRepository repository;
    private AppPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        preferences = new AppPreferences(this);
        
        // Apply saved theme preference (Light / Dark / System)
        ThemeHelper.applyTheme(preferences.getThemeMode());
        
        repository = new GymBuddyRepository(this);
    }

    public static GymBuddyApp getInstance() {
        return instance;
    }

    public GymBuddyRepository getRepository() {
        return repository;
    }

    public AppPreferences getPreferences() {
        return preferences;
    }
}
