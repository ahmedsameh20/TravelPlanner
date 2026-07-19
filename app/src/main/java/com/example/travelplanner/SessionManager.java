package com.example.travelplanner;

import android.content.Context;
import android.content.SharedPreferences;

public final class SessionManager {
    private SessionManager(){}
    private static SharedPreferences sp(Context c){ return c.getSharedPreferences("session", Context.MODE_PRIVATE); }
    public static void saveUser(Context c, String userId, String name){
        sp(c).edit().putString("user_id", userId).putString("user_name", name==null? "": name).apply();
    }
    public static String getUserId(Context c){ return sp(c).getString("user_id", ""); }
    public static String getUserName(Context c){ return sp(c).getString("user_name", ""); }
    public static void clear(Context c){ sp(c).edit().clear().apply(); }
}
