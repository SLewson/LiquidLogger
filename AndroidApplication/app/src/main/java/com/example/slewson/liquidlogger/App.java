package com.example.slewson.liquidlogger;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Marie on 5/21/2015.
 */
public class App extends Application {
    @Override public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "H4kwvMBZVHWXIzmfvHVqaEtLTXPQR8B495PqJkRt", "btY3RPW977jhi5yUyqbXPflEMzFlOFxPz0vDBjsL");
    }
}
