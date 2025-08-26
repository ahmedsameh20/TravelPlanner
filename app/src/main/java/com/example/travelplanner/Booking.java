package com.example.travelplanner;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;

public class Booking implements Parcelable {
    private String bookingId;
    private String details;
    private Date bookingDate;
    private boolean isConfirmed;
    private boolean isCancelled;

    public Booking(String bookingId, String details, Date bookingDate, boolean isConfirmed) {
        this.bookingId = bookingId;
        this.details = details;
        this.bookingDate = bookingDate;
        this.isConfirmed = isConfirmed;
        this.isCancelled = false;
    }

    public String getBookingId() { return bookingId; }
    public String getDetails() { return details; }
    public Date getBookingDate() { return bookingDate; }
    public boolean isConfirmed() { return isConfirmed; }
    public boolean isCancelled() { return isCancelled; }
    public void setCancelled(boolean cancelled) { isCancelled = cancelled; }

    protected Booking(Parcel in) {
        bookingId = in.readString();
        details = in.readString();
        long tmpDate = in.readLong();
        bookingDate = tmpDate == -1 ? null : new Date(tmpDate);
        isConfirmed = in.readByte() != 0;
        isCancelled = in.readByte() != 0;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookingId);
        dest.writeString(details);
        dest.writeLong(bookingDate != null ? bookingDate.getTime() : -1);
        dest.writeByte((byte) (isConfirmed ? 1 : 0));
        dest.writeByte((byte) (isCancelled ? 1 : 0));
    }

    @Override
    public String toString() {
        return "Booking: " + details + " (" + bookingDate + ")";
    }
}
