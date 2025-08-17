
package com.example.travelplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "travelplanner.db";
    public static final int DB_VERSION = 2;

    // Cities table
    public static final String TABLE_CITIES = "cities";
    public static final String COL_CITY_ID = "id";
    public static final String COL_CITY_NAME = "name";
    public static final String COL_CITY_COUNTRY = "country";
    public static final String COL_CITY_DESC = "description";

    // Hotels table
    public static final String TABLE_HOTELS = "hotels";
    public static final String COL_HOTEL_ID = "id";
    public static final String COL_HOTEL_CITY_ID = "city_id";
    public static final String COL_HOTEL_NAME = "name";
    public static final String COL_HOTEL_PRICE = "price_per_night";

    // Flights table
    public static final String TABLE_FLIGHTS = "flights";
    public static final String COL_FLIGHT_ID = "id";
    public static final String COL_FLIGHT_FROM = "from_city";
    public static final String COL_FLIGHT_TO = "to_city";
    public static final String COL_FLIGHT_CLASS = "class";
    public static final String COL_FLIGHT_PRICE = "price";

    // Favorites table (generic: type 'city' or 'hotel' and ref_id references the id)
    public static final String TABLE_FAVORITES = "favorites";
    public static final String COL_FAV_ID = "id";
    public static final String COL_FAV_TYPE = "type";
    public static final String COL_FAV_REF = "ref_id";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CITIES + " (" +
                COL_CITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CITY_NAME + " TEXT NOT NULL, " +
                COL_CITY_COUNTRY + " TEXT, " +
                COL_CITY_DESC + " TEXT" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_HOTELS + " (" +
                COL_HOTEL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_HOTEL_CITY_ID + " INTEGER, " +
                COL_HOTEL_NAME + " TEXT, " +
                COL_HOTEL_PRICE + " REAL" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_FLIGHTS + " (" +
                COL_FLIGHT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FLIGHT_FROM + " TEXT, " +
                COL_FLIGHT_TO + " TEXT, " +
                COL_FLIGHT_CLASS + " TEXT, " +
                COL_FLIGHT_PRICE + " REAL" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_FAVORITES + " (" +
                COL_FAV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FAV_TYPE + " TEXT, " +
                COL_FAV_REF + " INTEGER" +
                ");");

        // Seed sample data
        insertCity(db, "Cairo", "Egypt", "Pyramids, Nile, museums");
        insertCity(db, "Dubai", "UAE", "Skyscrapers and desert");
        insertCity(db, "Paris", "France", "Eiffel Tower and art");
        insertCity(db, "Istanbul", "Turkey", "Bosporus, history, markets");

        insertHotel(db, 1, "Pyramid View Hotel", 120.0);
        insertHotel(db, 1, "Nile Breeze", 85.5);
        insertHotel(db, 2, "Desert Sands Resort", 200.0);
        insertHotel(db, 3, "Eiffel Stay", 150.0);

        insertFlight(db, "Cairo", "Paris", "Economy", 350.0);
        insertFlight(db, "Dubai", "Istanbul", "Business", 420.0);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For simplicity in development, drop and recreate
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLIGHTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOTELS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITIES);
        onCreate(db);
    }

    // --- CRUD for City ---
    public long insertCity(String name, String country, String description) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CITY_NAME, name);
        values.put(COL_CITY_COUNTRY, country);
        values.put(COL_CITY_DESC, description);
        return db.insert(TABLE_CITIES, null, values);
    }

    private long insertCity(SQLiteDatabase db, String name, String country, String description) {
        ContentValues values = new ContentValues();
        values.put(COL_CITY_NAME, name);
        values.put(COL_CITY_COUNTRY, country);
        values.put(COL_CITY_DESC, description);
        return db.insert(TABLE_CITIES, null, values);
    }

    public Cursor getAllCities() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_CITIES, null, null, null, null, null, COL_CITY_NAME + " ASC");
    }

    public Cursor getCitiesByCountry(String country) {
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_CITY_COUNTRY + "=?";
        String[] args = new String[]{country};
        return db.query(TABLE_CITIES, null, selection, args, null, null, COL_CITY_NAME + " ASC");
    }

    public int updateCity(long id, String name, String country, String description) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CITY_NAME, name);
        values.put(COL_CITY_COUNTRY, country);
        values.put(COL_CITY_DESC, description);
        return db.update(TABLE_CITIES, values, COL_CITY_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int deleteCity(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_CITIES, COL_CITY_ID + "=?", new String[]{String.valueOf(id)});
    }

    // --- Hotels CRUD ---
    public long insertHotel(long cityId, String name, double price) {
        SQLiteDatabase db = getWritableDatabase();
        return insertHotel(db, cityId, name, price);
    }

    private long insertHotel(SQLiteDatabase db, long cityId, String name, double price) {
        ContentValues values = new ContentValues();
        values.put(COL_HOTEL_CITY_ID, cityId);
        values.put(COL_HOTEL_NAME, name);
        values.put(COL_HOTEL_PRICE, price);
        return db.insert(TABLE_HOTELS, null, values);
    }

    public Cursor getHotelsByCity(long cityId) {
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_HOTEL_CITY_ID + "=?";
        return db.query(TABLE_HOTELS, null, selection, new String[]{String.valueOf(cityId)}, null, null, COL_HOTEL_NAME + " ASC");
    }

    // --- Flights CRUD ---
    public long insertFlight(String from, String to, String cls, double price) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FLIGHT_FROM, from);
        values.put(COL_FLIGHT_TO, to);
        values.put(COL_FLIGHT_CLASS, cls);
        values.put(COL_FLIGHT_PRICE, price);
        return db.insert(TABLE_FLIGHTS, null, values);
    }

    public Cursor getFlights(String from, String to) {
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_FLIGHT_FROM + "=? AND " + COL_FLIGHT_TO + "=?";
        return db.query(TABLE_FLIGHTS, null, selection, new String[]{from, to}, null, null, COL_FLIGHT_PRICE + " ASC");
    }

    // --- Favorites CRUD ---
    public long addFavorite(String type, long refId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FAV_TYPE, type);
        values.put(COL_FAV_REF, refId);
        return db.insert(TABLE_FAVORITES, null, values);
    }

    public Cursor getAllFavorites() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_FAVORITES, null, null, null, null, null, COL_FAV_ID + " DESC");
    }

    public int deleteFavorite(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_FAVORITES, COL_FAV_ID + "=?", new String[]{String.valueOf(id)});
    }
}
