package com.example.travelplanner;

import java.util.List;

/**
 * Contract for cities/hotels/flights/bookings/favorites access.
 * {@link SqliteTravelRepository} and {@link FirestoreTravelRepository} are
 * the two implementations behind {@link Repo#travel}. All methods are async:
 * SQLite invokes the callback immediately (synchronously), Firestore attaches
 * it to the underlying Task's listeners.
 */
public interface TravelRepository {
    void getCities(Callback<List<City>> cb);

    void getHotels(Callback<List<Hotel>> cb);
    void getHotelsByCity(int cityId, Callback<List<Hotel>> cb);
    void getHotel(int id, Callback<Hotel> cb);

    void getFlights(Callback<List<Flight>> cb);
    void getFlight(int id, Callback<Flight> cb);

    void insertBooking(String userId, String type, int refId, String details, long dateMs, Callback<Void> cb);
    void updateBookingStatus(String userId, String bookingId, boolean confirmed, boolean cancelled, Callback<Void> cb);
    void deleteBooking(String userId, String bookingId, Callback<Void> cb);
    void getBookings(String userId, Callback<List<Booking>> cb);

    void addFavorite(String userId, String type, int refId, Callback<Void> cb);
    void removeFavorite(String userId, String type, int refId, Callback<Void> cb);
    void getFavorites(String userId, Callback<List<FavoriteItem>> cb);
}
