# Travel Planner

An Android app for browsing cities, hotels, and flights, saving favorites, and booking trips — backed by a real Firebase project (Authentication + Cloud Firestore).

## Features

- **Cities** — browse a catalog of cities with a search bar; tap a city to jump straight to its hotels.
- **Hotels** — filter by city, sort by price or rating, view rich detail screens with synthetic ratings/reviews/amenities, and book a stay.
- **Flights** — search by origin/destination/class, sort by price or duration, view flight details, and book a ticket.
- **Favorites** — save cities, hotels, or flights and manage them from a dedicated tab.
- **Bookings** — view booking history, confirm, cancel, or delete bookings.
- **Accounts** — register and log in with email/password; sessions persist across app restarts.

## Architecture

The data layer sits behind two interfaces, `TravelRepository` and `AuthRepository`, so the rest of the app (fragments, adapters, activities) never talks to Firebase or SQLite directly — it only sees an async `Callback<T>`-based API:

- `FirestoreTravelRepository` / `FirebaseAuthRepository` — the active implementation, backed by Cloud Firestore and Firebase Authentication. On first run, `FirestoreTravelRepository` self-seeds the `cities`, `hotels`, and `flights` collections if they're empty.
- `SqliteTravelRepository` / `SqliteAuthRepository` — a local-only fallback backed by `DBHelper` (SQLite), kept for offline/local development. Swap it in via `Repo.java`.

`Repo.java` is the single seam that wires up which implementation is active.

## Tech stack

- Java, Android SDK (minSdk 24, targetSdk/compileSdk 36)
- Firebase Authentication + Cloud Firestore
- Material Components (bottom navigation, cards, chips)
- SQLite (`DBHelper`) as a local-only fallback data source

## Setup

1. Clone the repo.
2. Add your own `google-services.json` from the Firebase console (Project Settings → your Android app), matching applicationId `com.ahmed.travelplanner`, and place it in `app/`.
3. In the Firebase console, enable **Authentication** (Email/Password provider) and create a **Cloud Firestore** database (Native mode).
4. Publish Firestore security rules that restrict access to authenticated users, e.g.:
   ```
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /{document=**} {
         allow read, write: if request.auth != null;
       }
     }
   }
   ```
5. Open the project in Android Studio and run it on an emulator or device. On first launch, the app registers a Firebase user and seeds the Firestore catalog automatically.

## Project structure

- `TravelRepository.java`, `AuthRepository.java` — repository interfaces (the seam).
- `FirestoreTravelRepository.java`, `FirebaseAuthRepository.java` — Firebase-backed implementations.
- `SqliteTravelRepository.java`, `SqliteAuthRepository.java`, `DBHelper.java` — local SQLite fallback.
- `*Fragment.java`, `*Activity.java` — UI screens (Cities, Hotels, Flights, Favorites, Bookings, Login/Register, detail screens).
- `*Adapter.java` — RecyclerView adapters for each list screen.
- `HotelMeta.java`, `FlightMeta.java`, `Review.java`, `ReviewGenerator.java` — deterministic synthetic data (ratings, amenities, reviews, schedules) layered on top of the catalog for realism.
