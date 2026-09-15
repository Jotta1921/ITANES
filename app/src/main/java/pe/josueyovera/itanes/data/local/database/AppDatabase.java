package pe.josueyovera.itanes.data.local.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import pe.josueyovera.itanes.data.local.dao.FavoriteDao;
import pe.josueyovera.itanes.data.local.dao.PlaceDao;
import pe.josueyovera.itanes.data.local.entity.FavoriteEntity;
import pe.josueyovera.itanes.data.local.entity.PlaceEntity;

@Database(entities = {PlaceEntity.class, FavoriteEntity.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract PlaceDao placeDao();
    public abstract FavoriteDao favoriteDao();

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "itanes_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
