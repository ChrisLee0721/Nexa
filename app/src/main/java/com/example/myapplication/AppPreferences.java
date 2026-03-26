package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

public final class AppPreferences {
    private static final String PREFS_NAME = "desk_monitor_prefs";
    private static final String KEY_SAMPLING_INDEX = "sampling_index";
    private static final String KEY_PRIORITY_INDEX = "priority_index";
    private static final String KEY_BLUR_ENABLED = "blur_enabled";
    private static final String KEY_AZURE_ENDPOINT = "azure_endpoint";
    private static final String KEY_AZURE_API_KEY = "azure_api_key";

    private AppPreferences() {
    }

    public static int getSamplingIndex(Context context) {
        return getPrefs(context).getInt(KEY_SAMPLING_INDEX, 1);
    }

    public static void setSamplingIndex(Context context, int value) {
        getPrefs(context).edit().putInt(KEY_SAMPLING_INDEX, value).apply();
    }

    public static int getPriorityIndex(Context context) {
        return getPrefs(context).getInt(KEY_PRIORITY_INDEX, 2);
    }

    public static void setPriorityIndex(Context context, int value) {
        getPrefs(context).edit().putInt(KEY_PRIORITY_INDEX, value).apply();
    }

    public static boolean isBlurEnabled(Context context) {
        return getPrefs(context).getBoolean(KEY_BLUR_ENABLED, true);
    }

    public static void setBlurEnabled(Context context, boolean enabled) {
        getPrefs(context).edit().putBoolean(KEY_BLUR_ENABLED, enabled).apply();
    }

    public static String getAzureEndpoint(Context context) {
        return getPrefs(context).getString(
                KEY_AZURE_ENDPOINT,
                "https://<your-app>.azurewebsites.net/api/latest"
        );
    }

    public static void setAzureEndpoint(Context context, String endpoint) {
        getPrefs(context).edit().putString(KEY_AZURE_ENDPOINT, endpoint).apply();
    }

    public static String getAzureApiKey(Context context) {
        return getPrefs(context).getString(KEY_AZURE_API_KEY, "");
    }

    public static void setAzureApiKey(Context context, String apiKey) {
        getPrefs(context).edit().putString(KEY_AZURE_API_KEY, apiKey).apply();
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}

