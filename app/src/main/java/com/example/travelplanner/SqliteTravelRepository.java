package com.example.travelplanner;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Local-only implementation backed by {@link DBHelper}. Callbacks are
 * invoked synchronously (local disk reads are fast) — kept for reference /
 * offline mode; {@link Repo} points at {@link FirestoreTravelRepository} by
 * default. Only ever paired with {@link SqliteAuthRepository}, whose user
 * ids are always the string form of a SQLite rowid, so parsing back to int
 * here is safe.
 */
public class SqliteTravelRepository implements TravelRepository {

    private final DBHelper db;

    public SqliteTravelRepository(Context ctx) {
        this.db = new DBHelper(ctx.getApplicationContext());
    }

    private static int uid(String userId) {
        try { return Integer.parseInt(userId); } catch (Exception e) { return -1; }
    }

    @Override
    public void getCities(Callback<List<City>> cb) {
        try { cb.onSuccess(db.getAllCities()); } catch (Exception e) { cb.onError(e); }
    }

    @Override
    public void getHotels(Callback<List<Hotel>> cb) {
        try { cb.onSuccess(db.getAllHotels()); } catch (Exception e) { cb.onError(e); }
    }

    @Override
    public void getHotelsByCity(int cityId, Callback<List<Hotel>> cb) {
        try {
            List<Hotel> out = new ArrayList<>();
            for (Hotel h : db.getAllHotels()) if (h.cityId == cityId) out.add(h);
            cb.onSuccess(out);
        } catch (Exception e) { cb.onError(e); }
    }

    @Override
    public void getHotel(int id, Callback<Hotel> cb) {
        try {
            Hotel found = null;
            for (Hotel h : db.getAllHotels()) if (h.id == id) { found = h; break; }
            cb.onSuccess(found);
        } catch (Exception e) { cb.onError(e); }
    }

    @Override
    public void getFlights(Callback<List<Flight>> cb) {
        try { cb.onSuccess(db.getAllFlights()); } catch (Exception e) { cb.onError(e); }
    }

    @Override
    public void getFlight(int id, Callback<Flight> cb) {
        try {
            Flight found = null;
            for (Flight f : db.getAllFlights()) if (f.id == id) { found = f; break; }
            cb.onSuccess(found);
        } catch (Exception e) { cb.onError(e); }
    }

    @Override
    public void insertBooking(String userId, String type, int refId, String details, long dateMs, Callback<Void> cb) {
        try { db.insertBooking(uid(userId), type, refId, details, dateMs); cb.onSuccess(null); } catch (Exception e) { cb.onError(e); }
    }

    @Override
    public void updateBookingStatus(String userId, String bookingId, boolean confirmed, boolean cancelled, Callback<Void> cb) {
        try { db.updateBookingStatus(uid(userId), Integer.parseInt(bookingId), confirmed, cancelled); cb.onSuccess(null); } catch (Exception e) { cb.onError(e); }
    }

    @Override
    public void deleteBooking(String userId, String bookingId, Callback<Void> cb) {
        try { db.deleteBooking(uid(userId), Integer.parseInt(bookingId)); cb.onSuccess(null); } catch (Exception e) { cb.onError(e); }
    }

    @Override
    public void getBookings(String userId, Callback<List<Booking>> cb) {
        try { cb.onSuccess(db.getAllBookings(uid(userId))); } catch (Exception e) { cb.onError(e); }
    }

    @Override
    public void addFavorite(String userId, String type, int refId, Callback<Void> cb) {
        try { db.addToFavorites(uid(userId), type, refId); cb.onSuccess(null); } catch (Exception e) { cb.onError(e); }
    }

    @Override
    public void removeFavorite(String userId, String type, int refId, Callback<Void> cb) {
        try { db.removeFromFavorites(uid(userId), type, refId); cb.onSuccess(null); } catch (Exception e) { cb.onError(e); }
    }

    @Override
    public void getFavorites(String userId, Callback<List<FavoriteItem>> cb) {
        try {
            List<FavoriteItem> out = new ArrayList<>();
            SQLiteDatabase rdb = db.getReadableDatabase();
            Cursor c = rdb.rawQuery(
                    "SELECT id, type, ref_id FROM favorites WHERE user_id=? ORDER BY id DESC",
                    new String[]{String.valueOf(uid(userId))});
            while (c.moveToNext()) {
                int favId = c.getInt(0);
                String type = c.getString(1);
                int refId = c.getInt(2);
                out.add(new FavoriteItem(String.valueOf(favId), type, refId, resolveTitle(rdb, type, refId)));
            }
            c.close();
            cb.onSuccess(out);
        } catch (Exception e) { cb.onError(e); }
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
