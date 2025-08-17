package com.example.travelplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "travelplanner.db";
    private static final int DB_VERSION = 1;

    // Favorites Table Columns
    public static final String TABLE_FAVORITES = "favorites";
    public static final String COL_FAV_ID = "id";
    public static final String COL_FAV_TYPE = "type";
    public static final String COL_FAV_REF = "ref_id";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE cities(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, country TEXT)");
        db.execSQL("CREATE TABLE hotels(id INTEGER PRIMARY KEY AUTOINCREMENT, city_id INTEGER, name TEXT, price REAL)");
        db.execSQL("CREATE TABLE flights(id INTEGER PRIMARY KEY AUTOINCREMENT, from_city TEXT, to_city TEXT, class TEXT, price REAL)");
        db.execSQL("CREATE TABLE " + TABLE_FAVORITES + "(" +
                COL_FAV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FAV_TYPE + " TEXT, " +
                COL_FAV_REF + " INTEGER)");

        // Seed Data
        db.execSQL("INSERT INTO cities(name, country) VALUES ('Cairo','Egypt'),('Giza','Egypt'),('Paris','France')");
        db.execSQL("INSERT INTO hotels(city_id, name, price) VALUES (1,'Cairo Hotel',50),(2,'Giza Inn',40),(3,'Paris Luxury',120)");
        db.execSQL("INSERT INTO flights(from_city,to_city,class,price) VALUES ('Cairo','Paris','Economy',300),('Cairo','Paris','Business',600)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS cities");
        db.execSQL("DROP TABLE IF EXISTS hotels");
        db.execSQL("DROP TABLE IF EXISTS flights");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        onCreate(db);
    }

    // Add a favorite
    public long insertFavorite(String type, long refId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FAV_TYPE, type);
        values.put(COL_FAV_REF, refId);
        return db.insert(TABLE_FAVORITES, null, values);
    }

    // Remove a favorite by ID
    public int deleteFavorite(long favId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_FAVORITES, COL_FAV_ID + "=?", new String[]{String.valueOf(favId)});
    }

    // Get all favorites
    public Cursor getAllFavorites() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_FAVORITES, null, null, null, null, null, null);
    }
}