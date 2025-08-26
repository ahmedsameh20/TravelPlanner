package com.example.travelplanner;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final String DATABASE_NAME = "travelplanner.db";
    private static final int DATABASE_VERSION = 14;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // cities
            db.execSQL("CREATE TABLE IF NOT EXISTS cities(" +
                    "id INTEGER PRIMARY KEY, " +
                    "name TEXT);");

            // hotels
            db.execSQL("CREATE TABLE IF NOT EXISTS hotels(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "city_id INTEGER, " +
                    "name TEXT, " +
                    "price REAL);");

            //flights
            db.execSQL("CREATE TABLE IF NOT EXISTS flights(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "from_city TEXT, " +
                    "to_city TEXT, " +
                    "cls TEXT, " +
                    "price REAL);");

            //booking
            db.execSQL("CREATE TABLE IF NOT EXISTS bookings(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER, " +
                    "type TEXT, " +
                    "ref_id INTEGER, " +
                    "date TEXT);");

            // Cities
            db.execSQL("INSERT INTO cities (id, name) VALUES " +
                    "(1,'Cairo'),(2,'Alexandria'),(3,'Giza'),(4,'Luxor'),(5,'Aswan')," +
                    "(6,'Sharm El Sheikh'),(7,'Hurghada'),(8,'Port Said'),(9,'Mansoura'),(10,'Tanta')," +
                    "(11,'Suez'),(12,'Ismailia'),(13,'Fayoum'),(14,'Minya'),(15,'Sohag')," +
                    "(16,'Qena'),(17,'Beni Suef'),(18,'Damietta'),(19,'Assiut'),(20,'Matruh')," +
                    "(21,'Paris'),(22,'London'),(23,'Rome'),(24,'Berlin'),(25,'Istanbul')," +
                    "(26,'New York'),(27,'Los Angeles'),(28,'Dubai'),(29,'Doha'),(30,'Riyadh');");

            // Hotels
            db.execSQL("INSERT INTO hotels (city_id, name, price) VALUES " +
                    "(1,'Nile View Hotel',120),(1,'Cairo Pyramids Inn',90)," +
                    "(2,'Sea Breeze Alexandria',150),(2,'Mediterranean Hotel',110)," +
                    "(4,'Luxor Palace',200),(5,'Aswan Nubian Lodge',140)," +
                    "(6,'Sharm El Sheikh Resort',250),(7,'Hurghada Paradise',230)," +
                    "(21,'Eiffel Tower Hotel',300),(22,'London Bridge Suites',350)," +
                    "(23,'Rome Colosseum Hotel',280),(26,'NYC Grand Hotel',400)," +
                    "(28,'Dubai Marina Resort',370),(30,'Riyadh Desert Pearl',200);");

            // Flights
            db.execSQL("INSERT INTO flights (from_city,to_city,cls,price) VALUES " +
                    "('Cairo','Alexandria','Economy',60)," +
                    "('Cairo','Luxor','Business',150)," +
                    "('Cairo','Aswan','Economy',90)," +
                    "('Cairo','Sharm El Sheikh','Economy',100)," +
                    "('Cairo','Hurghada','Business',180)," +
                    "('Cairo','Paris','Economy',350)," +
                    "('Cairo','London','Economy',400)," +
                    "('Cairo','Dubai','Economy',300)," +
                    "('Cairo','New York','Business',750)," +
                    "('Alexandria','Rome','Economy',320)," +
                    "('Luxor','Berlin','Economy',380)," +
                    "('Aswan','Istanbul','Economy',270)," +
                    "('Sharm El Sheikh','Doha','Economy',250)," +
                    "('Hurghada','Riyadh','Business',310)," +
                    "('London','New York','Business',800)," +
                    "('Paris','Berlin','Economy',220)," +
                    "('Dubai','Riyadh','Economy',180)," +
                    "('Rome','Cairo','Economy',330);");

            //favorites
            db.execSQL("CREATE TABLE IF NOT EXISTS favorites(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER, " +
                    "type TEXT, " +
                    "ref_id INTEGER);");


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
        db.execSQL("DROP TABLE IF EXISTS bookings");
        db.execSQL("DROP TABLE IF EXISTS favorites");

        onCreate(db);
    }

    // ---------------- Getters ----------------
    public List<City> getAllCities() {
        List<City> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("cities", new String[]{"id", "name"}, null, null, null, null, "name COLLATE NOCASE");
        if (c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndexOrThrow("id"));
                String name = c.getString(c.getColumnIndexOrThrow("name"));
                list.add(new City(id, name));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public List<Hotel> getAllHotels() {
        List<Hotel> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("hotels", new String[]{"id", "city_id", "name", "price"}, null, null, null, null, "name COLLATE NOCASE");
        if (c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndexOrThrow("id"));
                int cityId = c.getInt(c.getColumnIndexOrThrow("city_id"));
                String name = c.getString(c.getColumnIndexOrThrow("name"));
                double price = c.getDouble(c.getColumnIndexOrThrow("price"));
                list.add(new Hotel(id, cityId, name, price));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public List<Flight> getAllFlights() {
        List<Flight> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("flights", new String[]{"id", "from_city", "to_city", "cls", "price"}, null, null, null, null, "id ASC");
        if (c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndexOrThrow("id"));
                String from = c.getString(c.getColumnIndexOrThrow("from_city"));
                String to = c.getString(c.getColumnIndexOrThrow("to_city"));
                String cls = c.getString(c.getColumnIndexOrThrow("cls"));
                double price = c.getDouble(c.getColumnIndexOrThrow("price"));
                list.add(new Flight(id, from, to, cls, price));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    // ---------------- Bookings ----------------
    public void confirmBooking(int userId, String type, int refId, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("type", type);      // "hotel" or "flight"
        values.put("ref_id", refId);   // hotel or flight ID
        values.put("date", date);      // e.g., "2025-08-26"
        db.insert("bookings", null, values);
    }

    public void addToFavorites(int userId, String type, int refId){
        ContentValues cv = new ContentValues();
        cv.put("user_id", userId);
        cv.put("type", type);
        cv.put("ref_id", refId);
        getWritableDatabase().insert("favorites", null, cv);
    }


    public void removeFromFavorites(int userId, String type, int refId){
        getWritableDatabase().delete("favorites",
                "user_id=? AND type=? AND ref_id=?",
                new String[]{String.valueOf(userId), type, String.valueOf(refId)});
    }

    public boolean isFavorite(int userId, int refId, String type){
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id FROM favorites WHERE user_id=? AND type=? AND ref_id=?",
                new String[]{String.valueOf(userId), type, String.valueOf(refId)});
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    public List<String> getAllBookings(int userId) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT b.type, b.ref_id, b.date, f.from_city, f.to_city, h.name, h.price " +
                        "FROM bookings b " +
                        "LEFT JOIN flights f ON b.type='flight' AND b.ref_id=f.id " +
                        "LEFT JOIN hotels h ON b.type='hotel' AND b.ref_id=h.id " +
                        "WHERE b.user_id=? ORDER BY b.id DESC",
                new String[]{String.valueOf(userId)}
        );
        if (c.moveToFirst()) {
            do {
                String type = c.getString(c.getColumnIndexOrThrow("type"));
                String date = c.getString(c.getColumnIndexOrThrow("date"));
                String info = "";
                if (type.equals("flight")) {
                    String from = c.getString(c.getColumnIndexOrThrow("from_city"));
                    String to = c.getString(c.getColumnIndexOrThrow("to_city"));
                    info = type.toUpperCase() + ": " + from + " → " + to + " at " + date;
                } else if (type.equals("hotel")) {
                    String name = c.getString(c.getColumnIndexOrThrow("name"));
                    double price = c.getDouble(c.getColumnIndexOrThrow("price"));
                    info = type.toUpperCase() + ": " + name + " ($" + price + ") at " + date;
                }
                list.add(info);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }
}
