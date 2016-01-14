package com.example.takoyaki.newshyboys;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

/**
 * Created by LG on 2016-01-12.
 */
public class FontApplication extends Application {

    @Override

    public void onCreate() {
        super.onCreate();

        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "seoul.ttf");
    }
}
