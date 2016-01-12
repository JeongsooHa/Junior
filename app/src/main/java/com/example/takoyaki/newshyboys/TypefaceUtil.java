package com.example.takoyaki.newshyboys;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

/**
 * Created by LG on 2016-01-13.
 */
public class TypefaceUtil {

    public static void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets){

        try {
            final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);

            final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
        }
        catch(Exception e){

        }
    }
}
