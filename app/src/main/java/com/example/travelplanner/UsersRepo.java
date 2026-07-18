package com.example.travelplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class UsersRepo {
    private static final String TAG = "UsersRepo";
    private UsersRepo(){}

    public static final long DUPLICATE_EMAIL = -2L;
    public static final long DB_ERROR        = -3L;
    public static final long INVALID_INPUT   = -4L;

    public static String lastError = ""; // for quick diagnostics in UI

    public static void ensureSchema(Context ctx){
        DBHelper h = new DBHelper(ctx);
        SQLiteDatabase db = h.getWritableDatabase();
        ensure(db);
    }

    private static void ensure(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS users(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "email TEXT UNIQUE NOT NULL," +
                "password TEXT," +
                "salt TEXT)");
        ensureIndexes(db);
        // attempt to add missing columns
        Set<String> cols = getCols(db);
        if (!cols.contains("name")){
            try { db.execSQL("ALTER TABLE users ADD COLUMN name TEXT"); } catch (Throwable t){ Log.w(TAG,"add name", t); }
        }
        if (!cols.contains("email")){
            try { db.execSQL("ALTER TABLE users ADD COLUMN email TEXT"); } catch (Throwable t){ Log.w(TAG,"add email", t); }
        }
        boolean hasPassAlt = cols.contains("pass") || cols.contains("pwd");
        if (!cols.contains("password")){
            try { db.execSQL("ALTER TABLE users ADD COLUMN password TEXT"); } catch (Throwable t){ Log.w(TAG,"add password", t); }
            if (hasPassAlt){
                try { db.execSQL("UPDATE users SET password = COALESCE(pass, password)"); } catch (Throwable ignore){}
                try { db.execSQL("UPDATE users SET password = COALESCE(pwd, password)"); } catch (Throwable ignore){}
            }
        }
        if (!cols.contains("salt")){
            try { db.execSQL("ALTER TABLE users ADD COLUMN salt TEXT"); } catch (Throwable t){ Log.w(TAG,"add salt", t); }
        }
    }

    private static void ensureIndexes(SQLiteDatabase db){
        try { db.execSQL("CREATE INDEX IF NOT EXISTS idx_users_email ON users(email)"); } catch (Throwable ignore){}
    }

    private static Set<String> getCols(SQLiteDatabase db){
        Set<String> cols = new HashSet<>();
        Cursor ci = null;
        try {
            ci = db.rawQuery("PRAGMA table_info(users)", null);
            while (ci.moveToNext()){
                String col = ci.getString(ci.getColumnIndexOrThrow("name"));
                if (col != null) cols.add(col.toLowerCase(Locale.ROOT));
            }
        } catch (Throwable t){
            Log.w(TAG,"table_info failed", t);
        } finally { if (ci != null) ci.close(); }
        return cols;
    }

    // -------- Password hashing (SHA-256 + per-user salt) --------
    private static String generateSalt(){
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return toHex(bytes);
    }

    private static String hashPassword(String password, String salt){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return toHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e); // SHA-256 is always available on Android
        }
    }

    private static String toHex(byte[] bytes){
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format(Locale.ROOT, "%02x", b));
        return sb.toString();
    }

    /** Returns rowId (>0) on success; DUPLICATE_EMAIL / DB_ERROR / INVALID_INPUT otherwise. */
    public static long register(Context ctx, String name, String email, String password){
        lastError = "";
        if (email == null || password == null) { lastError="invalid input"; return INVALID_INPUT; }
        email = email.trim();
        if (email.isEmpty() || password.trim().isEmpty()) { lastError="invalid input"; return INVALID_INPUT; }

        DBHelper h = new DBHelper(ctx);
        SQLiteDatabase db = h.getWritableDatabase();
        ensure(db);

        Cursor chk = db.rawQuery("SELECT 1 FROM users WHERE email=? LIMIT 1", new String[]{email});
        boolean exists = chk.moveToFirst(); chk.close();
        if (exists){ lastError="duplicate email"; return DUPLICATE_EMAIL; }

        String salt = generateSalt();
        String hash = hashPassword(password, salt);

        ContentValues v = new ContentValues();
        v.put("name", name == null ? "" : name.trim());
        v.put("email", email);
        v.put("password", hash);
        v.put("salt", salt);
        try {
            long id = db.insertOrThrow("users", null, v);
            Log.d(TAG, "register ok id="+id+" email="+email);
            return id;
        } catch (SQLiteException sql1){
            lastError = safeMsg(sql1);
            Log.e(TAG, "register insert failed, will try rebuild. err="+lastError);
            // Try to rebuild users table preserving data, then retry once
            boolean rebuilt = rebuildUsersTable(db);
            if (rebuilt){
                try {
                    long id2 = db.insertOrThrow("users", null, v);
                    Log.d(TAG, "register ok after rebuild id="+id2);
                    return id2;
                } catch (Throwable sql2){
                    lastError = safeMsg(sql2);
                    Log.e(TAG, "insert after rebuild failed: "+lastError, sql2);
                    return DB_ERROR;
                }
            }
            return DB_ERROR;
        } catch (Throwable t){
            lastError = safeMsg(t);
            Log.e(TAG, "register failed: "+lastError, t);
            return DB_ERROR;
        }
    }

    /** Returns Cursor at (id,name) on success (caller must close), null otherwise. */
    public static Cursor login(Context ctx, String email, String password){
        lastError = "";
        if (email == null || password == null) return null;
        email = email.trim();
        if (email.isEmpty() || password.trim().isEmpty()) return null;

        DBHelper h = new DBHelper(ctx);
        SQLiteDatabase db = h.getWritableDatabase();
        ensure(db);

        try {
            Cursor c = db.rawQuery("SELECT id, name, password, salt FROM users WHERE email=? LIMIT 1",
                    new String[]{email});
            if (c != null && c.moveToFirst()) {
                int id = c.getInt(0);
                String name = c.getString(1);
                String storedHash = c.getString(2);
                String salt = c.getString(3);
                c.close();
                if (storedHash != null && salt != null && hashPassword(password, salt).equals(storedHash)) {
                    MatrixCursor mc = new MatrixCursor(new String[]{"id", "name"});
                    mc.addRow(new Object[]{id, name});
                    mc.moveToFirst();
                    return mc;
                }
                return null;
            }
            if (c != null) c.close();
        } catch (Throwable t){
            lastError = safeMsg(t);
            Log.e(TAG, "login query failed: "+lastError, t);
        }
        return null;
    }

    /** Debug helper: dump first 50 users (no password data) to Logcat. */
    public static void dumpUsers(Context ctx){
        try {
            DBHelper h = new DBHelper(ctx);
            SQLiteDatabase db = h.getReadableDatabase();
            Cursor c = db.rawQuery("SELECT id, name, email FROM users ORDER BY id LIMIT 50", null);
            Log.d(TAG, "---- dump users ----");
            while (c.moveToNext()){
                Log.d(TAG, String.format(Locale.ROOT, "id=%d, name=%s, email=%s",
                        c.getInt(0), c.getString(1), c.getString(2)));
            }
            c.close();
        } catch (Throwable t){
            Log.e(TAG, "dumpUsers failed", t);
        }
    }

    private static String safeMsg(Throwable t){
        String m = t==null? "" : t.getMessage();
        return (m==null? t.getClass().getSimpleName() : m);
    }

    /** Attempt to rebuild users table to the canonical schema, copy what we can, then swap. */
    private static boolean rebuildUsersTable(SQLiteDatabase db){
        try {
            Set<String> cols = getCols(db);
            db.beginTransaction();
            db.execSQL("ALTER TABLE users RENAME TO users_old");
            db.execSQL("CREATE TABLE users(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "email TEXT UNIQUE NOT NULL," +
                    "password TEXT," +
                    "salt TEXT)");
            // Build copy statement depending on existing columns
            String selName = cols.contains("name") ? "name" : "'' AS name";
            String selEmail = cols.contains("email") ? "email" : "NULL AS email";
            String selPwd;
            if (cols.contains("password")) selPwd = "password";
            else if (cols.contains("pass")) selPwd = "pass";
            else if (cols.contains("pwd")) selPwd = "pwd";
            else selPwd = "'' AS password";
            String selSalt = cols.contains("salt") ? "salt" : "NULL AS salt";

            // If email didn't exist before, skip copy to avoid null unique violation
            if (cols.contains("email")){
                String sqlCopy = "INSERT INTO users(name,email,password,salt) " +
                        "SELECT "+selName.replace(" AS name","")+"," +
                        selEmail.replace(" AS email","")+"," +
                        selPwd.replace(" AS password","")+"," +
                        selSalt.replace(" AS salt","")+ " FROM users_old";
                db.execSQL(sqlCopy);
            }
            db.execSQL("DROP TABLE IF EXISTS users_old");
            ensureIndexes(db);
            db.setTransactionSuccessful();
            return true;
        } catch (Throwable t){
            Log.e(TAG, "rebuildUsersTable failed", t);
            return false;
        } finally {
            try { db.endTransaction(); } catch (Throwable ignore){}
        }
    }
}
