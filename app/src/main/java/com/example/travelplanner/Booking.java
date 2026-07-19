package com.example.travelplanner;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Booking implements Parcelable {
    public String id;             // bookings doc id
    public String details;        // display text
    public long dateMs;           // travel date in millis
    public boolean isConfirmed;   // status
    public boolean isCancelled;   // status

    public Booking() { }

    public Booking(String id, String details, long dateMs, boolean confirmed, boolean cancelled) {
        this.id = id;
        this.details = details;
        this.dateMs = dateMs;
        this.isConfirmed = confirmed;
        this.isCancelled = cancelled;
    }

    public Date getBookingDate() { return new Date(dateMs); }

    /* ====== Parcelable ====== */
    protected Booking(Parcel in) {
        id = in.readString();
        details = in.readString();
        dateMs = in.readLong();
        isConfirmed = in.readByte() != 0;
        isCancelled = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(details);
        dest.writeLong(dateMs);
        dest.writeByte((byte) (isConfirmed ? 1 : 0));
        dest.writeByte((byte) (isCancelled ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Booking> CREATOR = new Creator<Booking>() {
        @Override
        public Booking createFromParcel(Parcel in) {
            return new Booking(in);
        }

        @Override
        public Booking[] newArray(int size) {
            return new Booking[size];
        }
    };
}
