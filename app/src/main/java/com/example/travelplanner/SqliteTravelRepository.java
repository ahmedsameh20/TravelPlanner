package com.example.travelplanner;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class SqliteTravelRepository implements TravelRepository {

    private final DBHelper db;

    public SqliteTravelRepository(Context ctx) {
        this.db = new DBHelper(ctx.getApplicationContext());
    }

    @Override
    public List<City> getCities() {
        return db.getAllCities();
    }

    @Override
    public List<Hotel> getHotels() {
        return db.getAllHotels();
    }

    @Override
    public List<Hotel> getHotelsByCity(int cityId) {
        List<Hotel> out = new ArrayList<>();
        for (Hotel h : db.getAllHotels()) if (h.cityId == cityId) out.add(h);
        return out;
    }

    @Override
    public Hotel getHotel(int id) {
        for (Hotel h : db.getAllHotels()) if (h.id == id) return h;
        return null;
    }

    @Override
    public List<Flight> getFlights() {
        return db.getAllFlights();
    }

    @Override
    public Flight getFlight(int id) {
        for (Flight f : db.getAllFlights()) if (f.id == id) return f;
        return null;
    }

    @Override
    public long insertBooking(int userId, String type, int refId, String details, long dateMs) {
        return db.insertBooking(userId, type, refId, details, dateMs);
    }

    @Override
    public void updateBookingStatus(int userId, int bookingId, boolean confirmed, boolean cancelled) {
        db.updateBookingStatus(userId, bookingId, confirmed, cancelled);
    }

    @Override
    public void deleteBooking(int userId, int bookingId) {
        db.deleteBooking(userId, bookingId);
    }

    @Override
    public List<Booking> getBookings(int userId) {
        return db.getAllBookings(userId);
    }

    @Override
    public void addFavorite(int userId, String type, int refId) {
        db.addToFavorites(userId, type, refId);
    }

    @Override
    public void removeFavorite(int userId, String type, int refId) {
        db.removeFromFavorites(userId, type, refId);
    }

    @Override
    public boolean isFavorite(int userId, int refId, String type) {
        return db.isFavorite(userId, refId, type);
    }

    @Override
    public List<FavoriteItem> getFavorites(int userId) {
        List<FavoriteItem> out = new ArrayList<>();
        SQLiteDatabase rdb = db.getReadableDatabase();
        Cursor c = rdb.rawQuery(
                "SELECT id, type, ref_id FROM favorites WHERE user_id=? ORDER BY id DESC",
                new String[]{String.valueOf(userId)});
        while (c.moveToNext()) {
            int favId = c.getInt(0);
            String type = c.getString(1);
            int refId = c.getInt(2);
            out.add(new FavoriteItem(favId, type, refId, resolveTitle(rdb, type, refId)));
        }
        c.close();
        return out;
    }

    private String resolveTitle(SQLiteDatabase rdb, String type, int refId) {
        if ("city".equals(type)) {
            Cursor cc = rdb.rawQuery("SELECT name FROM cities WHERE id=?", new String[]{String.valueOf(refId)});
            String name = cc.moveToFirst() ? cc.getString(0) : "";
            cc.close();
            return name;
        } else if ("hotel".equals(type)) {
            Cursor cc = rdb.rawQuery("SELECT name FROM hotels WHERE id=?", new String[]{String.valueOf(refId)});
            String name = cc.moveToFirst() ? cc.getString(0) : "";
            cc.close();
            return name;
        } else if ("flight".equals(type)) {
            Cursor cc = rdb.rawQuery("SELECT from_city, to_city FROM flights WHERE id=?", new String[]{String.valueOf(refId)});
            String name = cc.moveToFirst() ? (cc.getString(0) + " → " + cc.getString(1)) : "";
            cc.close();
            return name;
        }
        return "";
    }
}
