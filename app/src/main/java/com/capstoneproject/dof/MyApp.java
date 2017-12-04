package com.capstoneproject.dof;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by mandeepkaur on 2017-11-24.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}