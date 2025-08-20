
package com.example.travelplanner;
import android.database.Cursor; import android.database.sqlite.SQLiteDatabase; import android.os.Bundle; import android.widget.ArrayAdapter; import android.widget.ListView; import androidx.appcompat.app.AppCompatActivity; import java.util.ArrayList;
public class BookingsActivity extends AppCompatActivity {
    DBHelper db; ListView lv;
    @Override protected void onCreate(Bundle s){ super.onCreate(s); setContentView(R.layout.activity_bookings); db = new DBHelper(this); lv = findViewById(R.id.bookingsListView); loadBookings(); }
    private void loadBookings(){ ArrayList<String> data = new ArrayList<>(); SQLiteDatabase r = db.getReadableDatabase(); Cursor c = r.rawQuery("SELECT type,ref_id,date FROM bookings WHERE user_id=?", new String[]{String.valueOf(SessionManager.getUserId(this))}); while (c.moveToNext()){ String type=c.getString(0); int ref=c.getInt(1); String date=c.getString(2); String name=""; if (type.equals("hotel")){ Cursor cc=r.rawQuery("SELECT name FROM hotels WHERE id=?", new String[]{String.valueOf(ref)}); if (cc.moveToFirst()) name=cc.getString(0); cc.close(); } else if (type.equals("flight")){ Cursor cc=r.rawQuery("SELECT from_city,to_city FROM flights WHERE id=?", new String[]{String.valueOf(ref)}); if (cc.moveToFirst()) name=cc.getString(0)+" → "+cc.getString(1); cc.close(); } data.add(type.toUpperCase()+": "+name+" at "+date); } c.close(); lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data)); }
}
