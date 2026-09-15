package pe.josueyovera.itanes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pe.josueyovera.itanes.data.local.entity.PlaceEntity;
import pe.josueyovera.itanes.data.repository.PlaceRepository;
import pe.josueyovera.itanes.ui.detail.PlaceDetailActivity;
import pe.josueyovera.itanes.ui.favorites.FavoritesActivity;

public class PlacesActivity extends AppCompatActivity {

    private static final String TAG = "ITANES_UI";
    private RecyclerView recyclerView;
    private PlaceAdapter adapter;
    private PlaceRepository repository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private View progressBar;
    private View layoutEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        setupBottomNavigation();
        setupThemeToggle();

        Toolbar toolbar = findViewById(R.id.toolbarPlaces);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationContentDescription(R.string.content_description_back);
        }

        recyclerView = findViewById(R.id.recyclerPlaces);
        progressBar = findViewById(R.id.progressBar);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);

        // Responsive Layout: Grid for tablets (sw600dp), List for smartphones
        int smallestWidth = getResources().getConfiguration().smallestScreenWidthDp;
        if (smallestWidth >= 600) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        
        adapter = new PlaceAdapter();
        
        adapter.setOnPlaceClickListener(placeId -> {
            Intent intent = new Intent(PlacesActivity.this, PlaceDetailActivity.class);
            intent.putExtra(PlaceDetailActivity.EXTRA_PLACE_ID, placeId);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        repository = new PlaceRepository(getApplication());
    }

    private void setupThemeToggle() {
        ImageButton btnToggle = findViewById(R.id.buttonThemeToggle);
        if (btnToggle == null) return;

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            btnToggle.setImageResource(R.drawable.ic_light_mode);
        } else {
            btnToggle.setImageResource(R.drawable.ic_dark_mode);
        }

        btnToggle.setOnClickListener(v -> {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            recreate();
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_places);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_places) {
                return true;
            } else if (itemId == R.id.nav_favorites) {
                Intent intent = new Intent(this, FavoritesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPlaces();
    }

    private void loadPlaces() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.GONE);

        executorService.execute(() -> {
            try {
                List<PlaceEntity> places = repository.getAllPlaces();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    if (places == null || places.isEmpty()) {
                        layoutEmptyState.setVisibility(View.VISIBLE);
                        Log.d(TAG, "No places found in Room");
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.setPlaces(places);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error loading places: " + e.getMessage());
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    layoutEmptyState.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
