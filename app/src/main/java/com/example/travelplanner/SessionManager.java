package com.example.travelplanner;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "travel_session";
    public static void saveUser(Context ctx, int id, String name) {
        SharedPreferences sp = ctx.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt("user_id", id).putString("user_name", name).putBoolean("is_logged", true).apply();
    }
    public static int getUserId(Context ctx) {
        return ctx.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt("user_id", -1);
    }
    public static String getUserName(Context ctx) {
        return ctx.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString("user_name", "");
    }
    public static boolean isLogged(Context ctx) {
        return ctx.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getBoolean("is_logged", false);
    }
    public static void logout(Context ctx) {
        ctx.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply();
    }
}
