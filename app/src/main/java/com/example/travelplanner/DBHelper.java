
package com.example.travelplanner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "travelplanner.db";
    public static final int VERSION = 5;

    public DBHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT UNIQUE, password TEXT)");
            db.execSQL("CREATE TABLE cities(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)");
            db.execSQL("CREATE TABLE hotels(id INTEGER PRIMARY KEY AUTOINCREMENT, city_id INTEGER, name TEXT, price REAL)");
            db.execSQL("CREATE TABLE flights(id INTEGER PRIMARY KEY AUTOINCREMENT, from_city TEXT, to_city TEXT, class TEXT, price REAL)");
            db.execSQL("CREATE TABLE favorites(id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, type TEXT, ref_id INTEGER)");
            db.execSQL("CREATE TABLE bookings(id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, type TEXT, ref_id INTEGER, date TEXT)");

            db.execSQL("INSERT INTO users(name,email,password) VALUES ('Test User','test@example.com','123456')");
            db.execSQL("INSERT INTO cities(name) VALUES ('Cairo'),('Paris'),('Dubai')");
            db.execSQL("INSERT INTO hotels(city_id,name,price) VALUES (1,'Cairo Hotel',100),(2,'Paris Inn',200),(3,'Dubai Resort',300)");
            db.execSQL("INSERT INTO flights(from_city,to_city,class,price) VALUES ('Cairo','Paris','Economy',400),('Paris','Dubai','Business',800),('Dubai','Cairo','Economy',500)");
        } catch (Exception e) {
            Log.e("DBHELPER", "onCreate error", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS bookings");
        db.execSQL("DROP TABLE IF EXISTS favorites");
        db.execSQL("DROP TABLE IF EXISTS flights");
        db.execSQL("DROP TABLE IF EXISTS hotels");
        db.execSQL("DROP TABLE IF EXISTS cities");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }
}
