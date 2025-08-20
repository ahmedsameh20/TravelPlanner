
package com.example.travelplanner;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;
public class Prefs {
    private static final String PREF = "travel_prefs";
    private static SharedPreferences sp(Context c){ return c.getSharedPreferences(PREF, Context.MODE_PRIVATE); }
    public static void setLogged(Context c, boolean v){ sp(c).edit().putBoolean("logged", v).apply(); }
    public static boolean isLogged(Context c){ return sp(c).getBoolean("logged", false); }
    public static void setEmail(Context c, String e){ sp(c).edit().putString("email", e).apply(); }
    public static String getEmail(Context c){ return sp(c).getString("email", ""); }
    public static Set<String> getFav(Context c){ return new HashSet<>(sp(c).getStringSet("fav", new HashSet<>())); }
    public static void toggleFav(Context c, String s){ Set<String> st = getFav(c); if(st.contains(s)) st.remove(s); else st.add(s); sp(c).edit().putStringSet("fav", st).apply(); }
}
