package pe.josueyovera.itanes.ui.map;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.maplibre.android.MapLibre;
import org.maplibre.android.annotations.MarkerOptions;
import org.maplibre.android.camera.CameraPosition;
import org.maplibre.android.camera.CameraUpdateFactory;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.OnMapReadyCallback;
import org.maplibre.android.maps.Style;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.maplibre.android.maps.MapLibreMapOptions;
import pe.josueyovera.itanes.R;
import pe.josueyovera.itanes.data.local.entity.PlaceEntity;
import pe.josueyovera.itanes.data.repository.PlaceRepository;
import pe.josueyovera.itanes.ui.detail.PlaceDetailActivity;
import pe.josueyovera.itanes.util.LocationValidator;

import android.widget.FrameLayout;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "ITANES_MAP";
    private MapView mapView;
    private MapLibreMap mapLibreMap;
    private PlaceRepository repository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    
    private TextView textPlaceName;
    private TextView textPlaceAddress;
    private PlaceEntity currentPlace;
    private boolean isStyleLoaded = false;

    private static final double MIN_ZOOM = 3.0;
    private static final double MAX_ZOOM = 19.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar MapLibre con Application Context
        MapLibre.getInstance(getApplicationContext());

        setContentView(R.layout.activity_map);

        Toolbar toolbar = findViewById(R.id.toolbarMap);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationContentDescription(R.string.content_description_back);
        }

        // Configuración de mapa optimizada para emulador
        MapLibreMapOptions options = new MapLibreMapOptions()
                .textureMode(true)
                .translucentTextureSurface(true);
        
        mapView = new MapView(this, options);
        
        FrameLayout container = findViewById(R.id.mapContainer);
        if (container != null) {
            container.addView(mapView);
        }

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        textPlaceName = findViewById(R.id.textMapPlaceName);
        textPlaceAddress = findViewById(R.id.textMapPlaceAddress);

        findViewById(R.id.buttonZoomIn).setOnClickListener(v -> zoomIn());
        findViewById(R.id.buttonZoomOut).setOnClickListener(v -> zoomOut());

        repository = new PlaceRepository(getApplication());

        int placeId = getIntent().getIntExtra(PlaceDetailActivity.EXTRA_PLACE_ID, -1);
        Log.d(TAG, "Loading map for placeId: " + placeId);
        loadAllData(placeId);
    }

    private void loadAllData(int placeId) {
        executorService.execute(() -> {
            try {
                currentPlace = repository.getPlaceById(placeId);
                runOnUiThread(() -> {
                    if (currentPlace != null) {
                        textPlaceName.setText(currentPlace.getName());
                        textPlaceAddress.setText(currentPlace.getAddress());
                        if (mapLibreMap != null && isStyleLoaded) {
                            setupMapMarkers();
                        }
                    } else {
                        Toast.makeText(this, R.string.error_load_place, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error loading data: " + e.getMessage());
            }
        });
    }

    @Override
    public void onMapReady(@NonNull MapLibreMap mapLibreMap) {
        Log.d(TAG, "Map is ready");
        this.mapLibreMap = mapLibreMap;
        
        // Estilo detallado de OpenFreeMap (basado en OpenStreetMap)
        mapLibreMap.setStyle(new Style.Builder().fromUri("https://tiles.openfreemap.org/styles/liberty"), style -> {
            Log.d(TAG, "Style loaded successfully");
            isStyleLoaded = true;
            if (currentPlace != null) {
                setupMapMarkers();
            }
        });
    }

    private void setupMapMarkers() {
        if (isFinishing() || isDestroyed()) return;
        if (mapLibreMap == null || !isStyleLoaded || currentPlace == null) {
            Log.w(TAG, "Cannot setup markers yet");
            return;
        }

        try {
            double lat = currentPlace.getLatitude();
            double lng = currentPlace.getLongitude();

            // Validar coordenadas
            if (!LocationValidator.isValid(lat, lng)) {
                Toast.makeText(this, R.string.error_invalid_coordinates, Toast.LENGTH_SHORT).show();
                return;
            }

            // Limpiar marcadores previos
            mapLibreMap.clear();

            // Añadir ÚNICAMENTE el marcador del lugar seleccionado
            LatLng location = new LatLng(lat, lng);
            mapLibreMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(currentPlace.getName()));
            
            Log.d(TAG, "Added marker for: " + currentPlace.getName());

            // Centrar cámara en el lugar seleccionado
            CameraPosition position = new CameraPosition.Builder()
                    .target(location)
                    .zoom(15.0)
                    .build();

            mapLibreMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        } catch (Exception e) {
            Log.e(TAG, "Error in setupMapMarkers: " + e.getMessage());
        }
    }

    private void zoomIn() {
        if (mapLibreMap != null) {
            double currentZoom = mapLibreMap.getCameraPosition().zoom;
            if (currentZoom < MAX_ZOOM) {
                mapLibreMap.animateCamera(CameraUpdateFactory.zoomTo(Math.min(currentZoom + 1, MAX_ZOOM)));
            }
        }
    }

    private void zoomOut() {
        if (mapLibreMap != null) {
            double currentZoom = mapLibreMap.getCameraPosition().zoom;
            if (currentZoom > MIN_ZOOM) {
                mapLibreMap.animateCamera(CameraUpdateFactory.zoomTo(Math.max(currentZoom - 1, MIN_ZOOM)));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Ciclo de vida de MapView
    @Override
    protected void onStart() {
        super.onStart();
        if (mapView != null) mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mapView != null) mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) mapView.onDestroy();
        executorService.shutdown();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) mapView.onSaveInstanceState(outState);
    }
}
