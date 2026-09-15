package pe.josueyovera.itanes.data.repository;

import android.app.Application;

import pe.josueyovera.itanes.data.local.dao.FavoriteDao;
import pe.josueyovera.itanes.data.local.database.AppDatabase;
import pe.josueyovera.itanes.data.local.entity.FavoriteEntity;

public class FavoriteRepository {

    private final FavoriteDao favoriteDao;

    public FavoriteRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        favoriteDao = db.favoriteDao();
    }

    public void insertFavorite(FavoriteEntity favorite) {
        favoriteDao.insertFavorite(favorite);
    }

    public void deleteByPlaceId(int placeId) {
        favoriteDao.deleteByPlaceId(placeId);
    }

    public boolean isFavorite(int placeId) {
        return favoriteDao.isFavorite(placeId);
    }
}
