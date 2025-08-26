
package com.example.travelplanner;
import android.os.Bundle; import androidx.appcompat.app.AppCompatActivity; import androidx.fragment.app.Fragment; import com.google.android.material.bottomnavigation.BottomNavigationView;
public class MainActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle s){
        super.onCreate(s);
        setContentView(R.layout.activity_main);
        BottomNavigationView nav=findViewById(R.id.bottom_navigation);
        nav.setOnItemSelectedListener(item->{ Fragment f=null;int id=item.getItemId();if(id==R.id.nav_cities) f=new CitiesFragment();else if(id==R.id.nav_hotels) f=new HotelsFragment();else if(id==R.id.nav_flights) f=new FlightsFragment();else if(id==R.id.nav_favorites) f=new FavoritesFragment();else if(id==R.id.nav_bookings) f=new BookingsFragment();if(f!=null) getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,f).commit();return true; }); if(s==null) getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CitiesFragment()).commit(); }
}