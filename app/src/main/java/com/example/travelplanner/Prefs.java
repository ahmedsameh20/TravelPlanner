package com.example.travelplanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Prefs {
    private static final String TAG = "Prefs";
    private static final String FILE = "travelplanner_prefs";
    private static final String KEY_FAVS = "favs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_LOGGED = "logged";
    private static final String KEY_MIGRATED = "migrated_favs_v1";
    private static final String FAV_BOOL_PREFIX = "fav__"; // backup flags

    // Accept hotel/flight with :, _, -, optional 'fav' prefix/suffix
    private static final Pattern ANY_FAV_KEY = Pattern.compile("^(?:fav(?:orite)?[_:]?)?(hotel|flight)[_:-]?(\\d+)(?:[_:]?fav(?:orite)?)?$", Pattern.CASE_INSENSITIVE);
    private static final Pattern CANONICAL = Pattern.compile("^(hotel|flight):(\\d+)$", Pattern.CASE_INSENSITIVE);

    private Prefs(){}

    private static SharedPreferences sp(Context c){
        return c.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    // -------- Favorites --------
    public static Set<String> getFav(Context c){
        migrateLegacyIfNeeded(c);
        Set<String> raw = sp(c).getStringSet(KEY_FAVS, Collections.emptySet());
        Set<String> out = new HashSet<>();
        if (raw != null) {
            for (String k : raw) {
                String canon = toCanonical(k);
                if (canon != null) out.add(canon);
            }
        }
        // merge with boolean backups
        try {
            Map<String, ?> all = sp(c).getAll();
            for (Map.Entry<String, ?> e : all.entrySet()) {
                if (e.getKey() != null && e.getKey().startsWith(FAV_BOOL_PREFIX) && e.getValue() instanceof Boolean) {
                    if ((Boolean) e.getValue()) {
                        String kk = e.getKey().substring(FAV_BOOL_PREFIX.length());
                        String canon = toCanonical(kk);
                        if (canon != null) out.add(canon);
                    }
                }
            }
        } catch (Throwable ignore){}
        return out;
    }

    public static boolean isFav(Context c, String key){
        String canon = toCanonical(key);
        if (canon == null) return false;
        if (getFav(c).contains(canon)) return true;
        return sp(c).getBoolean(FAV_BOOL_PREFIX + canon, false);
    }

    public static void toggleFav(Context c, String key){
        String canon = toCanonical(key);
        if (canon == null) return;
        Set<String> copy = new HashSet<>(getFav(c));
        boolean nowFav;
        if (copy.contains(canon)) { copy.remove(canon); nowFav = false; }
        else { copy.add(canon); nowFav = true; }
        SharedPreferences.Editor ed = sp(c).edit();
        ed.putStringSet(KEY_FAVS, new HashSet<>(copy));
        ed.putBoolean(FAV_BOOL_PREFIX + canon, nowFav);
        ed.apply();
        Log.d(TAG, "toggleFav " + canon + " -> " + nowFav + " size=" + copy.size());
    }

    public static void addFav(Context c, String key){
        String canon = toCanonical(key);
        if (canon == null) return;
        Set<String> copy = new HashSet<>(getFav(c));
        if (copy.add(canon)) {
            SharedPreferences.Editor ed = sp(c).edit();
            ed.putStringSet(KEY_FAVS, new HashSet<>(copy));
            ed.putBoolean(FAV_BOOL_PREFIX + canon, true);
            ed.apply();
        }
    }

    public static void removeFav(Context c, String key){
        String canon = toCanonical(key);
        if (canon == null) return;
        Set<String> copy = new HashSet<>(getFav(c));
        if (copy.remove(canon)) {
            SharedPreferences.Editor ed = sp(c).edit();
            ed.putStringSet(KEY_FAVS, new HashSet<>(copy));
            ed.putBoolean(FAV_BOOL_PREFIX + canon, false);
            ed.apply();
        }
    }

    // -------- Session --------
    public static void setEmail(Context c, String email){
        String e = email == null ? "" : email.trim();
        sp(c).edit().putString(KEY_EMAIL, e).apply();
    }
    public static String getEmail(Context c){
        String e = sp(c).getString(KEY_EMAIL, "");
        return e == null ? "" : e;
    }
    public static void setLogged(Context c, boolean logged){
        sp(c).edit().putBoolean(KEY_LOGGED, logged).apply();
    }
    public static boolean isLogged(Context c){
        return sp(c).getBoolean(KEY_LOGGED, false);
    }
    public static void logout(Context c){
        sp(c).edit().putBoolean(KEY_LOGGED, false).apply();
    }

    // -------- Helpers --------
    private static String toCanonical(String key){
        if (key == null) return null;
        String k = key.trim();
        if (k.isEmpty()) return null;
        Matcher mCanon = CANONICAL.matcher(k);
        if (mCanon.matches()) {
            return (mCanon.group(1).toLowerCase() + ":" + mCanon.group(2));
        }
        Matcher m = ANY_FAV_KEY.matcher(k);
        if (m.matches()) {
            String type = m.group(1).toLowerCase();
            String id = m.group(2);
            return type + ":" + id;
        }
        return null;
    }

    private static void migrateLegacyIfNeeded(Context c){
        SharedPreferences prefs = sp(c);
        if (prefs.getBoolean(KEY_MIGRATED, false)) return;

        Map<String, ?> all = new HashMap<>(prefs.getAll());
        Set<String> found = new HashSet<>();
        for (Map.Entry<String, ?> e : all.entrySet()) {
            Object v = e.getValue();
            if (v instanceof Boolean && (Boolean) v) {
                String canon = toCanonical(e.getKey());
                if (canon != null) found.add(canon);
            }
        }
        String[] legacySetNames = new String[]{"favorites","favs","fav_set","likes","liked"};
        for (String name : legacySetNames) {
            Object v = all.get(name);
            if (v instanceof Set) {
                try {
                    @SuppressWarnings("unchecked")
                    Set<Object> s = (Set<Object>) v;
                    for (Object o : s) {
                        if (o != null) {
                            String canon = toCanonical(String.valueOf(o));
                            if (canon != null) found.add(canon);
                        }
                    }
                } catch (Throwable ignore){}
            }
        }
        Set<String> current = prefs.getStringSet(KEY_FAVS, Collections.emptySet());
        if (current != null) {
            for (String k : current) {
                String canon = toCanonical(k);
                if (canon != null) found.add(canon);
            }
        }
        SharedPreferences.Editor ed = prefs.edit();
        for (String canon : found) {
            ed.putBoolean(FAV_BOOL_PREFIX + canon, true);
        }
        ed.putStringSet(KEY_FAVS, new HashSet<>(found));
        ed.putBoolean(KEY_MIGRATED, true);
        ed.apply();
    }
}
