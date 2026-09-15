package pe.josueyovera.itanes.data.repository;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pe.josueyovera.itanes.R;
import pe.josueyovera.itanes.data.local.dao.PlaceDao;
import pe.josueyovera.itanes.data.local.database.AppDatabase;
import pe.josueyovera.itanes.data.local.entity.PlaceEntity;
import pe.josueyovera.itanes.data.mapper.PlaceMapper;
import pe.josueyovera.itanes.data.remote.dto.PlaceRemoteDto;
import pe.josueyovera.itanes.data.remote.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceRepository {

    private static final String TAG_SYNC = "ITANES_SYNC";
    private static final String TAG_ROOM = "ITANES_ROOM";
    private final PlaceDao placeDao;
    private final ExecutorService executorService;
    private final Application application;

    public PlaceRepository(Application application) {
        this.application = application;
        AppDatabase db = AppDatabase.getInstance(application);
        placeDao = db.placeDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void syncPlaces() {
        Log.d(TAG_SYNC, "Iniciando sincronización");
        RetrofitClient.getApiService().getPlaces().enqueue(new Callback<List<PlaceRemoteDto>>() {
            @Override
            public void onResponse(Call<List<PlaceRemoteDto>> call, Response<List<PlaceRemoteDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PlaceRemoteDto> remotePlaces = response.body();
                    if (!remotePlaces.isEmpty()) {
                        Log.d(TAG_SYNC, remotePlaces.size() + " lugares recibidos");
                        savePlacesToRoom(remotePlaces);
                    } else {
                        Log.d(TAG_SYNC, "Respuesta vacía, se conservan datos locales");
                    }
                } else {
                    Log.e(TAG_SYNC, "Error HTTP " + response.code() + ": " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<PlaceRemoteDto>> call, Throwable t) {
                Log.e(TAG_SYNC, "Fallo en la petición de red: " + t.getMessage());
                Toast.makeText(application, R.string.error_offline, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savePlacesToRoom(List<PlaceRemoteDto> remotePlaces) {
        executorService.execute(() -> {
            try {
                for (PlaceRemoteDto dto : remotePlaces) {
                    Log.d(TAG_SYNC, "API DATA -> " + dto.getName() + " Coords: " + dto.getLatitude() + ", " + dto.getLongitude());
                }
                List<PlaceEntity> entities = PlaceMapper.toEntityList(remotePlaces);
                placeDao.insertPlaces(entities);
                Log.d(TAG_SYNC, "Sincronización completada");
            } catch (Exception e) {
                Log.e(TAG_SYNC, "Error al guardar en Room: " + e.getMessage());
            }
        });
    }

    public List<PlaceEntity> getAllPlaces() {
        try {
            return placeDao.getAllPlaces();
        } catch (Exception e) {
            Log.e(TAG_ROOM, "Error al obtener todos los lugares: " + e.getMessage());
            return null;
        }
    }

    public PlaceEntity getPlaceById(int id) {
        try {
            return placeDao.getPlaceById(id);
        } catch (Exception e) {
            Log.e(TAG_ROOM, "Error al obtener lugar por ID: " + e.getMessage());
            return null;
        }
    }

    public List<PlaceEntity> getFavoritePlaces() {
        try {
            return placeDao.getFavoritePlaces();
        } catch (Exception e) {
            Log.e(TAG_ROOM, "Error al obtener lugares favoritos: " + e.getMessage());
            return null;
        }
    }

    public void insertPlaces(List<PlaceEntity> places) {
        placeDao.insertPlaces(places);
    }

    public int getCount() {
        return placeDao.getCount();
    }

    public void deleteAllPlaces() {
        placeDao.deleteAllPlaces();
    }
}
