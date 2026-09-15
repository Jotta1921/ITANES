package pe.josueyovera.itanes.ui.detail;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pe.josueyovera.itanes.R;
import pe.josueyovera.itanes.data.local.entity.FavoriteEntity;
import pe.josueyovera.itanes.data.local.entity.PlaceEntity;
import pe.josueyovera.itanes.data.repository.FavoriteRepository;
import pe.josueyovera.itanes.data.repository.PlaceRepository;
import pe.josueyovera.itanes.ui.map.MapActivity;
import pe.josueyovera.itanes.util.LocationValidator;

public class PlaceDetailActivity extends AppCompatActivity {

    private static final String TAG = "ITANES_UI";
    public static final String EXTRA_PLACE_ID = "extra_place_id";
    private static final int DEFAULT_ID = -1;

    private PlaceRepository repository;
    private FavoriteRepository favoriteRepository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private ImageView imageDetail;
    private TextView textName;
    private TextView textShortDescription;
    private TextView textFullDescription;
    private TextView textAddress;
    private TextView textCoordinates;
    private View buttonFavorite;
    private View buttonShare;
    private View buttonMap;
    private View buttonDirections;

    private int currentPlaceId;
    private PlaceEntity currentPlace;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationContentDescription(R.string.content_description_back);
        }

        repository = new PlaceRepository(getApplication());
        favoriteRepository = new FavoriteRepository(getApplication());
        
        initViews();

        currentPlaceId = getIntent().getIntExtra(EXTRA_PLACE_ID, DEFAULT_ID);
        if (currentPlaceId <= 0) {
            showErrorAndFinish();
            return;
        }

        loadPlaceDetails(currentPlaceId);
        checkFavoriteStatus(currentPlaceId);
    }

    private void initViews() {
        imageDetail = findViewById(R.id.imageDetail);
        textName = findViewById(R.id.textDetailName);
        textShortDescription = findViewById(R.id.textDetailShortDescription);
        textFullDescription = findViewById(R.id.textDetailFullDescription);
        textAddress = findViewById(R.id.textDetailAddress);
        textCoordinates = findViewById(R.id.textDetailCoordinates);
        buttonFavorite = findViewById(R.id.buttonFavorite);
        buttonShare = findViewById(R.id.buttonShare);
        buttonMap = findViewById(R.id.buttonMap);
        buttonDirections = findViewById(R.id.buttonDirections);

        buttonFavorite.setOnClickListener(v -> toggleFavorite());
        buttonShare.setOnClickListener(v -> sharePlace());
        buttonMap.setOnClickListener(v -> openMap());
        buttonDirections.setOnClickListener(v -> openDirections());
    }

    private void openMap() {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra(EXTRA_PLACE_ID, currentPlaceId);
        startActivity(intent);
    }

    private void openDirections() {
        if (currentPlace == null) return;

        double lat = currentPlace.getLatitude();
        double lng = currentPlace.getLongitude();

        // Validar coordenadas
        if (!LocationValidator.isValid(lat, lng)) {
            Toast.makeText(this, R.string.error_invalid_location, Toast.LENGTH_SHORT).show();
            return;
        }

        // URL de navegación universal (Google Maps Dir API)
        String uriString = String.format(Locale.US,
                "https://www.google.com/maps/dir/?api=1&destination=%f,%f&travelmode=driving",
                lat, lng);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
        
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.error_no_navigation_app, Toast.LENGTH_SHORT).show();
        }
    }

    private void checkFavoriteStatus(int placeId) {
        executorService.execute(() -> {
            try {
                boolean favorite = favoriteRepository.isFavorite(placeId);
                runOnUiThread(() -> {
                    isFavorite = favorite;
                    updateFavoriteButton();
                });
            } catch (Exception e) {
                Log.e(TAG, "Error checking favorite status: " + e.getMessage());
            }
        });
    }

    private void toggleFavorite() {
        executorService.execute(() -> {
            try {
                if (isFavorite) {
                    favoriteRepository.deleteByPlaceId(currentPlaceId);
                    isFavorite = false;
                } else {
                    FavoriteEntity favorite = new FavoriteEntity(currentPlaceId, String.valueOf(System.currentTimeMillis()));
                    favoriteRepository.insertFavorite(favorite);
                    isFavorite = true;
                }
                runOnUiThread(this::updateFavoriteButton);
            } catch (Exception e) {
                Log.e(TAG, "Error toggling favorite: " + e.getMessage());
            }
        });
    }

    private void updateFavoriteButton() {
        if (buttonFavorite instanceof com.google.android.material.button.MaterialButton) {
            com.google.android.material.button.MaterialButton btn = (com.google.android.material.button.MaterialButton) buttonFavorite;
            if (isFavorite) {
                btn.setText(R.string.button_unfavorite);
                btn.setIconResource(android.R.drawable.btn_star_big_on);
            } else {
                btn.setText(R.string.button_favorite);
                btn.setIconResource(android.R.drawable.btn_star_big_off);
            }
        }
    }

    private void loadPlaceDetails(int placeId) {
        executorService.execute(() -> {
            try {
                PlaceEntity place = repository.getPlaceById(placeId);
                runOnUiThread(() -> {
                    if (place != null) {
                        currentPlace = place;
                        displayPlace(place);
                    } else {
                        showErrorAndFinish();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error loading place details: " + e.getMessage());
                runOnUiThread(this::showErrorAndFinish);
            }
        });
    }

    private void displayPlace(PlaceEntity place) {
        textName.setText(place.getName());
        textShortDescription.setText(place.getShortDescription());
        textFullDescription.setText(place.getDescription());
        textAddress.setText(place.getAddress());
        
        String coordinates = String.format(Locale.getDefault(), "Lat: %.6f, Lon: %.6f", 
                place.getLatitude(), place.getLongitude());
        textCoordinates.setText(coordinates);

        Glide.with(this)
                .load(place.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.stat_notify_error)
                .centerCrop()
                .into(imageDetail);
    }

    private void sharePlace() {
        if (currentPlace == null) return;

        String shareText = String.format("%s\n%s\n\n%s\n%s\n\n%s",
                currentPlace.getName(),
                currentPlace.getShortDescription(),
                getString(R.string.label_address),
                currentPlace.getAddress(),
                getString(R.string.share_footer, getString(R.string.app_name)));

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, currentPlace.getName());
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        Intent chooser = Intent.createChooser(shareIntent, getString(R.string.share_chooser_title));
        
        try {
            startActivity(chooser);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.error_share_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void showErrorAndFinish() {
        Toast.makeText(this, R.string.error_load_place, Toast.LENGTH_SHORT).show();
        finish();
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
