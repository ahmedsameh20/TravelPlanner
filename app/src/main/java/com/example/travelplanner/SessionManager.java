package com.example.travelplanner;

import android.content.Context;
import android.content.SharedPreferences;

public final class SessionManager {
    private SessionManager(){}
    private static SharedPreferences sp(Context c){ return c.getSharedPreferences("session", Context.MODE_PRIVATE); }
    public static void saveUser(Context c, int userId, String name){
        sp(c).edit().putInt("user_id", userId).putString("user_name", name==null? "": name).apply();
    }
    public static int getUserId(Context c){ return sp(c).getInt("user_id", -1); }
    public static String getUserName(Context c){ return sp(c).getString("user_name", ""); }
    public static void clear(Context c){ sp(c).edit().clear().apply(); }
}