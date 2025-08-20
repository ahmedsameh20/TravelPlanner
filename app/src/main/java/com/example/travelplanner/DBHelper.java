package com.example.travelplanner;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final String DATABASE_NAME = "travelplanner.db";
    private static final int DATABASE_VERSION = 10; // bumped to force recreate during dev

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS cities(id INTEGER PRIMARY KEY, name TEXT);");
            db.execSQL("CREATE TABLE IF NOT EXISTS hotels(id INTEGER PRIMARY KEY, city_id INTEGER, name TEXT, price REAL);");
            db.execSQL("CREATE TABLE IF NOT EXISTS flights(id INTEGER PRIMARY KEY, from_city TEXT, to_city TEXT, cls TEXT, price REAL);");

            // Seed cities (30 example cities)
            db.execSQL("CREATE TABLE cities (id INTEGER PRIMARY KEY, name TEXT);");
            db.execSQL("CREATE TABLE hotels (id INTEGER PRIMARY KEY, city_id INTEGER, name TEXT, price REAL);");
            db.execSQL("CREATE TABLE flights (id INTEGER PRIMARY KEY, from_city TEXT, to_city TEXT, class TEXT, price REAL);");

            // ---------------- Cities ----------------
            db.execSQL("INSERT INTO cities (id, name) VALUES " +
                    "(1,'Cairo'),(2,'Alexandria'),(3,'Giza'),(4,'Luxor'),(5,'Aswan')," +
                    "(6,'Sharm El Sheikh'),(7,'Hurghada'),(8,'Port Said'),(9,'Mansoura'),(10,'Tanta')," +
                    "(11,'Suez'),(12,'Ismailia'),(13,'Fayoum'),(14,'Minya'),(15,'Sohag')," +
                    "(16,'Qena'),(17,'Beni Suef'),(18,'Damietta'),(19,'Assiut'),(20,'Matruh')," +
                    "(21,'Paris'),(22,'London'),(23,'Rome'),(24,'Berlin'),(25,'Istanbul')," +
                    "(26,'New York'),(27,'Los Angeles'),(28,'Dubai'),(29,'Doha'),(30,'Riyadh'));");

            // ---------------- Hotels ----------------
            db.execSQL("INSERT INTO hotels (city_id, name, price) VALUES " +
                    "(1,'Nile View Hotel',120),(1,'Cairo Pyramids Inn',90)," +
                    "(2,'Sea Breeze Alexandria',150),(2,'Mediterranean Hotel',110)," +
                    "(4,'Luxor Palace',200),(5,'Aswan Nubian Lodge',140)," +
                    "(6,'Sharm El Sheikh Resort',250),(7,'Hurghada Paradise',230)," +
                    "(21,'Eiffel Tower Hotel',300),(22,'London Bridge Suites',350)," +
                    "(23,'Rome Colosseum Hotel',280),(26,'NYC Grand Hotel',400)," +
                    "(28,'Dubai Marina Resort',370),(30,'Riyadh Desert Pearl',200);");

            // ---------------- Flights ----------------
            db.execSQL("INSERT INTO flights (from_city,to_city,class,price) VALUES " +
                    "('Cairo','Alexandria','Economy',60),('Cairo','Luxor','Business',150)," +
                    "('Cairo','Aswan','Economy',90),('Cairo','Sharm El Sheikh','Economy',100)," +
                    "('Cairo','Hurghada','Business',180),('Cairo','Paris','Economy',350)," +
                    "('Cairo','London','Economy',400),('Cairo','Dubai','Economy',300)," +
                    "('Cairo','New York','Business',750)," +
                    "('Alexandria','Rome','Economy',320),('Luxor','Berlin','Economy',380)," +
                    "('Aswan','Istanbul','Economy',270),('Sharm El Sheikh','Doha','Economy',250)," +
                    "('Hurghada','Riyadh','Business',310)," +
                    "('London','New York','Business',800),('Paris','Berlin','Economy',220)," +
                    "('Dubai','Riyadh','Economy',180),('Rome','Cairo','Economy',330);");

        } catch (Exception e) {
            Log.e(TAG, "onCreate error", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Upgrading DB from " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS flights");
        db.execSQL("DROP TABLE IF EXISTS hotels");
        db.execSQL("DROP TABLE IF EXISTS cities");
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Downgrading DB from " + oldVersion + " to " + newVersion + ". Recreating schema.");
        onUpgrade(db, oldVersion, newVersion);
    }

    // Return list of City objects
    public List<City> getAllCities() {
        List<City> list = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = getReadableDatabase();
            c = db.query("cities", new String[]{"id", "name"}, null, null, null, null, "name COLLATE NOCASE");
            if (c != null && c.moveToFirst()) {
                do {
                    int id = c.getInt(c.getColumnIndexOrThrow("id"));
                    String name = c.getString(c.getColumnIndexOrThrow("name"));
                    list.add(new City(id, name));
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "getAllCities error", e);
        } finally {
            if (c != null) c.close();
            if (db != null) db.close();
        }
        return list;
    }

    // Return list of Hotel objects
    public List<Hotel> getAllHotels() {
        List<Hotel> list = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = getReadableDatabase();
            c = db.query("hotels", new String[]{"id", "city_id", "name", "price"}, null, null, null, null, "name COLLATE NOCASE");
            if (c != null && c.moveToFirst()) {
                do {
                    int id = c.getInt(c.getColumnIndexOrThrow("id"));
                    int cityId = c.getInt(c.getColumnIndexOrThrow("city_id"));
                    String name = c.getString(c.getColumnIndexOrThrow("name"));
                    double price = c.getDouble(c.getColumnIndexOrThrow("price"));
                    list.add(new Hotel(id, cityId, name, price));
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "getAllHotels error", e);
        } finally {
            if (c != null) c.close();
            if (db != null) db.close();
        }
        return list;
    }

    // Return list of Flight objects
    public List<Flight> getAllFlights() {
        List<Flight> list = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = getReadableDatabase();
            c = db.query("flights", new String[]{"id", "from_city", "to_city", "cls", "price"}, null, null, null, null, "id ASC");
            if (c != null && c.moveToFirst()) {
                do {
                    int id = c.getInt(c.getColumnIndexOrThrow("id"));
                    String from = c.getString(c.getColumnIndexOrThrow("from_city"));
                    String to = c.getString(c.getColumnIndexOrThrow("to_city"));
                    String cls = c.getString(c.getColumnIndexOrThrow("cls"));
                    double price = c.getDouble(c.getColumnIndexOrThrow("price"));
                    list.add(new Flight(id, from, to, cls, price));
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "getAllFlights error", e);
        } finally {
            if (c != null) c.close();
            if (db != null) db.close();
        }
        return list;
    }
}
