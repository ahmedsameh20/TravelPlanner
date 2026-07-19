package com.example.travelplanner;

import java.util.List;

/**
 * Contract for cities/hotels/flights/bookings/favorites access.
 * {@link SqliteTravelRepository} is the only implementation today; a future
 * Firestore-backed implementation can drop in behind {@link Repo#travel}
 * without touching any fragment/adapter.
 */
public interface TravelRepository {
    List<City> getCities();

    List<Hotel> getHotels();
    List<Hotel> getHotelsByCity(int cityId);
    Hotel getHotel(int id);

    List<Flight> getFlights();
    Flight getFlight(int id);

    long insertBooking(int userId, String type, int refId, String details, long dateMs);
    void updateBookingStatus(int userId, int bookingId, boolean confirmed, boolean cancelled);
    void deleteBooking(int userId, int bookingId);
    List<Booking> getBookings(int userId);

    void addFavorite(int userId, String type, int refId);
    void removeFavorite(int userId, String type, int refId);
    boolean isFavorite(int userId, int refId, String type);
    List<FavoriteItem> getFavorites(int userId);
}
