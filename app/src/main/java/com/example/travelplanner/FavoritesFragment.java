
package com.example.travelplanner; import android.os.Bundle; import android.view.*; import androidx.annotation.Nullable; import androidx.fragment.app.Fragment; import android.widget.TextView;
public class FavoritesFragment extends Fragment { @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){ View v=inflater.inflate(R.layout.fragment_favorites,container,false); TextView tv=v.findViewById(R.id.tvFav); tv.setText("Favorites (tap items to toggle)"); return v; } }
