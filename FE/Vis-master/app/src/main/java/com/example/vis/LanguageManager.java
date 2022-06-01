package com.example.vis;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LanguageManager {
    private final Context ct;
    private final SharedPreferences preferences;

    public LanguageManager(Context ctx){
        ct = ctx;
        preferences = ctx.getSharedPreferences(ctx.getString(R.string.SHARED_PREFERENCES_KEY), Context.MODE_PRIVATE);
    }

    public String getLanguage() {
        return preferences.getString(ct.getString(R.string.SHARED_PREFERENCES_LANGUAGE_KEY), "sk");
    }

    public void updateResource() {
        updateResource(getLanguage());
    }

    public void updateResource(String code){
        Locale locale = new Locale(code);
        Locale.setDefault(locale);
        Resources resources = ct.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        ct.createConfigurationContext(configuration);
        resources.updateConfiguration(configuration,resources.getDisplayMetrics());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ct.getString(R.string.SHARED_PREFERENCES_LANGUAGE_KEY), code);
        editor.apply();
    }
}
