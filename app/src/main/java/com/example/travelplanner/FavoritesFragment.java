package com.example.travelplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoritesFragment extends Fragment {

    private TravelRepository repo;
    private RecyclerView rv;
    private TextView tvEmpty;
    private FavoritesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favorites, container, false);

        repo = Repo.travel(requireContext());
        rv = v.findViewById(R.id.rvFavorites);
        tvEmpty = v.findViewById(R.id.tvEmpty);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new FavoritesAdapter(requireContext(), null, new FavoritesAdapter.Listener() {
            @Override
            public void onRemove(FavoriteItem item) {
                repo.removeFavorite(SessionManager.getUserId(requireContext()), item.type, item.refId);
                Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                reload();
            }

            @Override
            public void onClick(FavoriteItem item) {
                if ("hotel".equals(item.type)) {
                    Intent intent = new Intent(requireContext(), HotelDetailActivity.class);
                    intent.putExtra(HotelDetailActivity.EXTRA_HOTEL_ID, item.refId);
                    startActivity(intent);
                } else if ("flight".equals(item.type)) {
                    Intent intent = new Intent(requireContext(), FlightDetailActivity.class);
                    intent.putExtra(FlightDetailActivity.EXTRA_FLIGHT_ID, item.refId);
                    startActivity(intent);
                } else if ("city".equals(item.type)) {
                    NavState.requestCityFilter(item.refId);
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).selectTab(R.id.nav_hotels);
                    }
                }
            }
        });
        rv.setAdapter(adapter);

        reload();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    private void reload() {
        List<FavoriteItem> favs = repo.getFavorites(SessionManager.getUserId(requireContext()));
        adapter.updateData(favs);
        tvEmpty.setVisibility(favs.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
