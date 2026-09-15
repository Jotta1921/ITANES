package pe.josueyovera.itanes.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import pe.josueyovera.itanes.data.local.entity.PlaceEntity;

@Dao
public interface PlaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlaces(List<PlaceEntity> places);

    @Query("SELECT * FROM places ORDER BY orderNumber ASC")
    List<PlaceEntity> getAllPlaces();

    @Query("SELECT * FROM places WHERE id = :id")
    PlaceEntity getPlaceById(int id);

    @Query("SELECT places.* FROM places INNER JOIN favorites ON places.id = favorites.placeId ORDER BY favorites.createdAt DESC")
    List<PlaceEntity> getFavoritePlaces();

    @Query("SELECT COUNT(*) FROM places")
    int getCount();

    @Query("DELETE FROM places")
    void deleteAllPlaces();
}
