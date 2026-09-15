package pe.josueyovera.itanes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import pe.josueyovera.itanes.data.local.seed.PlaceDataSeeder;
import pe.josueyovera.itanes.data.repository.PlaceRepository;
import pe.josueyovera.itanes.ui.favorites.FavoritesActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        setupBottomNavigation();
        setupThemeToggle();
        setupHeroImage();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0); 
            return insets;
        });

        // Inicializar repositorio y seeder
        PlaceRepository repository = new PlaceRepository(getApplication());
        PlaceDataSeeder seeder = new PlaceDataSeeder(repository);
        seeder.seed();

        // Sincronizar lugares desde la API
        repository.syncPlaces();

        // Navegación a PlacesActivity
        Button buttonExplore = findViewById(R.id.buttonExplore);
        buttonExplore.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PlacesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
    }

    private void setupHeroImage() {
        android.widget.ImageView imageHero = findViewById(R.id.imageHero);
        if (imageHero != null) {
            // URL corregida: evitamos la doble codificación de espacios para que Glide la procese bien
            String imageUrl = "https://media.vogue.mx/photos/5e19fa7ba3810f0008d96d3e/2:3/w_2560,c_limit/Lima-%20La%20Costa%20Verde.jpg";
            
            Log.d("ITANES_UI", "Intentando cargar hero image: " + imageUrl);

            Glide.with(this)
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.drawable.img_lima_portada)
                .error(R.drawable.img_lima_portada)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e("ITANES_UI", "Fallo al cargar imagen de portada: " + (e != null ? e.getMessage() : "Desconocido"));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("ITANES_UI", "Imagen de portada cargada con éxito");
                        return false;
                    }
                })
                .into(imageHero);
        }
    }

    private void setupThemeToggle() {
        ImageButton btnToggle = findViewById(R.id.buttonThemeToggle);
        if (btnToggle == null) return;

        int nightMode = AppCompatDelegate.getDefaultNightMode();
        if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
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
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_places) {
                Intent intent = new Intent(this, PlacesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
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
}
