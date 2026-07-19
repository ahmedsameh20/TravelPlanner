package com.example.travelplanner;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Firestore-backed implementation. Collections: cities/hotels/flights are
 * seeded once (self-seeding, see {@link #ensureSeeded()}) using the same
 * catalog {@link DBHelper#onCreate} seeds locally, keyed by the string form
 * of the same int ids so City/Hotel/Flight model code stays untouched.
 * bookings/favorites use Firestore auto-generated document ids.
 */
public class FirestoreTravelRepository implements TravelRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Task<Void> seedTask;

    // ---------------- Seed data (mirrors DBHelper.onCreate) ----------------

    private static final List<City> SEED_CITIES = Arrays.asList(
            new City(1, "Cairo"), new City(2, "Alexandria"), new City(3, "Giza"),
            new City(4, "Luxor"), new City(5, "Aswan"), new City(6, "Sharm El Sheikh"),
            new City(7, "Hurghada"), new City(8, "Port Said"), new City(9, "Mansoura"),
            new City(10, "Tanta"), new City(11, "Suez"), new City(12, "Ismailia"),
            new City(13, "Fayoum"), new City(14, "Minya"), new City(15, "Sohag"),
            new City(16, "Qena"), new City(17, "Beni Suef"), new City(18, "Damietta"),
            new City(19, "Assiut"), new City(20, "Matruh"), new City(21, "Paris"),
            new City(22, "London"), new City(23, "Rome"), new City(24, "Berlin"),
            new City(25, "Istanbul"), new City(26, "New York"), new City(27, "Los Angeles"),
            new City(28, "Dubai"), new City(29, "Doha"), new City(30, "Riyadh")
    );

    private static final List<Hotel> SEED_HOTELS = Arrays.asList(
            new Hotel(1, 1, "Nile View Hotel", 120), new Hotel(2, 1, "Cairo Pyramids Inn", 90),
            new Hotel(3, 2, "Sea Breeze Alexandria", 150), new Hotel(4, 2, "Mediterranean Hotel", 110),
            new Hotel(5, 4, "Luxor Palace", 200), new Hotel(6, 5, "Aswan Nubian Lodge", 140),
            new Hotel(7, 6, "Sharm El Sheikh Resort", 250), new Hotel(8, 7, "Hurghada Paradise", 230),
            new Hotel(9, 21, "Eiffel Tower Hotel", 300), new Hotel(10, 22, "London Bridge Suites", 350),
            new Hotel(11, 23, "Rome Colosseum Hotel", 280), new Hotel(12, 26, "NYC Grand Hotel", 400),
            new Hotel(13, 28, "Dubai Marina Resort", 370), new Hotel(14, 30, "Riyadh Desert Pearl", 200)
    );

    private static final List<Flight> SEED_FLIGHTS = Arrays.asList(
            new Flight(1, "Cairo", "Alexandria", "Economy", 60),
            new Flight(2, "Cairo", "Luxor", "Business", 150),
            new Flight(3, "Cairo", "Aswan", "Economy", 90),
            new Flight(4, "Cairo", "Sharm El Sheikh", "Economy", 100),
            new Flight(5, "Cairo", "Hurghada", "Business", 180),
            new Flight(6, "Cairo", "Paris", "Economy", 350),
            new Flight(7, "Cairo", "London", "Economy", 400),
            new Flight(8, "Cairo", "Dubai", "Economy", 300),
            new Flight(9, "Cairo", "New York", "Business", 750),
            new Flight(10, "Alexandria", "Rome", "Economy", 320),
            new Flight(11, "Luxor", "Berlin", "Economy", 380),
            new Flight(12, "Aswan", "Istanbul", "Economy", 270),
            new Flight(13, "Sharm El Sheikh", "Doha", "Economy", 250),
            new Flight(14, "Hurghada", "Riyadh", "Business", 310),
            new Flight(15, "London", "New York", "Business", 800),
            new Flight(16, "Paris", "Berlin", "Economy", 220),
            new Flight(17, "Dubai", "Riyadh", "Economy", 180),
            new Flight(18, "Rome", "Cairo", "Economy", 330)
    );

    private synchronized Task<Void> ensureSeeded() {
        if (seedTask == null) {
            seedTask = db.collection("cities").limit(1).get().continueWithTask(task -> {
                if (!task.isSuccessful()) throw requireException(task);
                if (!task.getResult().isEmpty()) return Tasks.forResult(null);

                WriteBatch batch = db.batch();
                for (City c : SEED_CITIES) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("id", c.id);
                    data.put("name", c.name);
                    batch.set(db.collection("cities").document(String.valueOf(c.id)), data);
                }
                for (Hotel h : SEED_HOTELS) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("id", h.id);
                    data.put("cityId", h.cityId);
                    data.put("name", h.name);
                    data.put("price", h.price);
                    batch.set(db.collection("hotels").document(String.valueOf(h.id)), data);
                }
                for (Flight f : SEED_FLIGHTS) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("id", f.id);
                    data.put("from", f.from);
                    data.put("to", f.to);
                    data.put("cls", f.cls);
                    data.put("price", f.price);
                    batch.set(db.collection("flights").document(String.valueOf(f.id)), data);
                }
                return batch.commit();
            });
        }
        return seedTask;
    }

    private static Exception requireException(Task<?> task) {
        Exception e = task.getException();
        return e != null ? e : new Exception("Unknown Firestore error");
    }

    // ---------------- Cities / Hotels / Flights ----------------

    @Override
    public void getCities(Callback<List<City>> cb) {
        ensureSeeded()
                .addOnSuccessListener(v -> db.collection("cities").get()
                        .addOnSuccessListener(snap -> cb.onSuccess(toCities(snap)))
                        .addOnFailureListener(cb::onError))
                .addOnFailureListener(cb::onError);
    }

    @Override
    public void getHotels(Callback<List<Hotel>> cb) {
        ensureSeeded()
                .addOnSuccessListener(v -> db.collection("hotels").get()
                        .addOnSuccessListener(snap -> cb.onSuccess(toHotels(snap)))
                        .addOnFailureListener(cb::onError))
                .addOnFailureListener(cb::onError);
    }

    @Override
    public void getHotelsByCity(int cityId, Callback<List<Hotel>> cb) {
        ensureSeeded()
                .addOnSuccessListener(v -> db.collection("hotels").whereEqualTo("cityId", cityId).get()
                        .addOnSuccessListener(snap -> cb.onSuccess(toHotels(snap)))
                        .addOnFailureListener(cb::onError))
                .addOnFailureListener(cb::onError);
    }

    @Override
    public void getHotel(int id, Callback<Hotel> cb) {
        ensureSeeded()
                .addOnSuccessListener(v -> db.collection("hotels").document(String.valueOf(id)).get()
                        .addOnSuccessListener(doc -> cb.onSuccess(doc.exists() ? toHotel(doc) : null))
                        .addOnFailureListener(cb::onError))
                .addOnFailureListener(cb::onError);
    }

    @Override
    public void getFlights(Callback<List<Flight>> cb) {
        ensureSeeded()
                .addOnSuccessListener(v -> db.collection("flights").get()
                        .addOnSuccessListener(snap -> cb.onSuccess(toFlights(snap)))
                        .addOnFailureListener(cb::onError))
                .addOnFailureListener(cb::onError);
    }

    @Override
    public void getFlight(int id, Callback<Flight> cb) {
        ensureSeeded()
                .addOnSuccessListener(v -> db.collection("flights").document(String.valueOf(id)).get()
                        .addOnSuccessListener(doc -> cb.onSuccess(doc.exists() ? toFlight(doc) : null))
                        .addOnFailureListener(cb::onError))
                .addOnFailureListener(cb::onError);
    }

    // ---------------- Bookings ----------------

    @Override
    public void insertBooking(String userId, String type, int refId, String details, long dateMs, Callback<Void> cb) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("type", type);
        data.put("refId", refId);
        data.put("details", details);
        data.put("date", dateMs);
        data.put("confirmed", true);
        data.put("cancelled", false);
        data.put("createdAt", System.currentTimeMillis());
        db.collection("bookings").add(data)
                .addOnSuccessListener(ref -> cb.onSuccess(null))
                .addOnFailureListener(cb::onError);
    }

    @Override
    public void updateBookingStatus(String userId, String bookingId, boolean confirmed, boolean cancelled, Callback<Void> cb) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("confirmed", confirmed);
        updates.put("cancelled", cancelled);
        db.collection("bookings").document(bookingId).update(updates)
                .addOnSuccessListener(v -> cb.onSuccess(null))
                .addOnFailureListener(cb::onError);
    }

    @Override
    public void deleteBooking(String userId, String bookingId, Callback<Void> cb) {
        db.collection("bookings").document(bookingId).delete()
                .addOnSuccessListener(v -> cb.onSuccess(null))
                .addOnFailureListener(cb::onError);
    }

    @Override
    public void getBookings(String userId, Callback<List<Booking>> cb) {
        db.collection("bookings").whereEqualTo("userId", userId).get()
                .addOnSuccessListener(snap -> {
                    List<DocumentSnapshot> docs = new ArrayList<>(snap.getDocuments());
                    docs.sort((a, b) -> Long.compare(longOrZero(b, "createdAt"), longOrZero(a, "createdAt")));
                    List<Booking> out = new ArrayList<>();
                    for (DocumentSnapshot d : docs) out.add(toBooking(d));
                    cb.onSuccess(out);
                })
                .addOnFailureListener(cb::onError);
    }

    // ---------------- Favorites ----------------

    @Override
    public void addFavorite(String userId, String type, int refId, Callback<Void> cb) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("type", type);
        data.put("refId", refId);
        data.put("createdAt", System.currentTimeMillis());
        db.collection("favorites").add(data)
                .addOnSuccessListener(ref -> cb.onSuccess(null))
                .addOnFailureListener(cb::onError);
    }

    @Override
    public void removeFavorite(String userId, String type, int refId, Callback<Void> cb) {
        db.collection("favorites")
                .whereEqualTo("userId", userId)
                .whereEqualTo("type", type)
                .whereEqualTo("refId", refId)
                .get()
                .addOnSuccessListener(snap -> {
                    WriteBatch batch = db.batch();
                    for (DocumentSnapshot d : snap.getDocuments()) batch.delete(d.getReference());
                    batch.commit()
                            .addOnSuccessListener(v -> cb.onSuccess(null))
                            .addOnFailureListener(cb::onError);
                })
                .addOnFailureListener(cb::onError);
    }

    @Override
    public void getFavorites(String userId, Callback<List<FavoriteItem>> cb) {
        db.collection("favorites").whereEqualTo("userId", userId).get()
                .addOnSuccessListener(favSnap -> {
                    List<DocumentSnapshot> favDocs = new ArrayList<>(favSnap.getDocuments());
                    if (favDocs.isEmpty()) { cb.onSuccess(new ArrayList<>()); return; }

                    Task<QuerySnapshot> citiesTask = db.collection("cities").get();
                    Task<QuerySnapshot> hotelsTask = db.collection("hotels").get();
                    Task<QuerySnapshot> flightsTask = db.collection("flights").get();

                    Tasks.whenAllSuccess(citiesTask, hotelsTask, flightsTask)
                            .addOnSuccessListener(results -> {
                                Map<Integer, String> cityNames = nameLookup((QuerySnapshot) results.get(0));
                                Map<Integer, String> hotelNames = nameLookup((QuerySnapshot) results.get(1));
                                Map<Integer, String> flightRoutes = routeLookup((QuerySnapshot) results.get(2));

                                favDocs.sort((a, b) -> Long.compare(longOrZero(b, "createdAt"), longOrZero(a, "createdAt")));

                                List<FavoriteItem> out = new ArrayList<>();
                                for (DocumentSnapshot d : favDocs) {
                                    String type = d.getString("type");
                                    Long refIdL = d.getLong("refId");
                                    int refId = refIdL == null ? 0 : refIdL.intValue();
                                    String title = "";
                                    if ("city".equals(type)) title = cityNames.getOrDefault(refId, "");
                                    else if ("hotel".equals(type)) title = hotelNames.getOrDefault(refId, "");
                                    else if ("flight".equals(type)) title = flightRoutes.getOrDefault(refId, "");
                                    out.add(new FavoriteItem(d.getId(), type, refId, title));
                                }
                                cb.onSuccess(out);
                            })
                            .addOnFailureListener(cb::onError);
                })
                .addOnFailureListener(cb::onError);
    }

    // ---------------- Mapping helpers ----------------

    private List<City> toCities(QuerySnapshot snap) {
        List<City> out = new ArrayList<>();
        for (DocumentSnapshot d : snap.getDocuments()) out.add(toCity(d));
        return out;
    }

    private City toCity(DocumentSnapshot d) {
        Long id = d.getLong("id");
        String name = d.getString("name");
        return new City(id == null ? 0 : id.intValue(), name == null ? "" : name);
    }

    private List<Hotel> toHotels(QuerySnapshot snap) {
        List<Hotel> out = new ArrayList<>();
        for (DocumentSnapshot d : snap.getDocuments()) out.add(toHotel(d));
        return out;
    }

    private Hotel toHotel(DocumentSnapshot d) {
        Long id = d.getLong("id");
        Long cityId = d.getLong("cityId");
        String name = d.getString("name");
        Double price = d.getDouble("price");
        return new Hotel(id == null ? 0 : id.intValue(), cityId == null ? 0 : cityId.intValue(),
                name == null ? "" : name, price == null ? 0 : price);
    }

    private List<Flight> toFlights(QuerySnapshot snap) {
        List<Flight> out = new ArrayList<>();
        for (DocumentSnapshot d : snap.getDocuments()) out.add(toFlight(d));
        return out;
    }

    private Flight toFlight(DocumentSnapshot d) {
        Long id = d.getLong("id");
        String from = d.getString("from");
        String to = d.getString("to");
        String cls = d.getString("cls");
        Double price = d.getDouble("price");
        return new Flight(id == null ? 0 : id.intValue(), from == null ? "" : from, to == null ? "" : to,
                cls == null ? "" : cls, price == null ? 0 : price);
    }

    private Booking toBooking(DocumentSnapshot d) {
        String details = d.getString("details");
        Long date = d.getLong("date");
        Boolean confirmed = d.getBoolean("confirmed");
        Boolean cancelled = d.getBoolean("cancelled");
        return new Booking(d.getId(), details == null ? "" : details, date == null ? 0 : date,
                confirmed != null && confirmed, cancelled != null && cancelled);
    }

    private Map<Integer, String> nameLookup(QuerySnapshot snap) {
        Map<Integer, String> out = new HashMap<>();
        for (DocumentSnapshot d : snap.getDocuments()) {
            Long id = d.getLong("id");
            String name = d.getString("name");
            if (id != null) out.put(id.intValue(), name == null ? "" : name);
        }
        return out;
    }

    private Map<Integer, String> routeLookup(QuerySnapshot snap) {
        Map<Integer, String> out = new HashMap<>();
        for (DocumentSnapshot d : snap.getDocuments()) {
            Long id = d.getLong("id");
            String from = d.getString("from");
            String to = d.getString("to");
            if (id != null) out.put(id.intValue(), (from == null ? "" : from) + " → " + (to == null ? "" : to));
        }
        return out;
    }

    private long longOrZero(DocumentSnapshot d, String field) {
        Long v = d.getLong(field);
        return v == null ? 0 : v;
    }
}
