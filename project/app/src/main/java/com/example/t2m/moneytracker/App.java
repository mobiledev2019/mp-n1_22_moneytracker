package com.example.t2m.moneytracker;

import android.app.Application;
import com.google.gson.Gson;

public class App extends Application {

    private static App sInstance;
    private Gson mGSon;

    public static App self() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mGSon = new Gson();
    }

    public Gson getGSon() {
        return mGSon;
    }
}
