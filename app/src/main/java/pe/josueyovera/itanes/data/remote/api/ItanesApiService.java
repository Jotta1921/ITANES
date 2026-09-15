package pe.josueyovera.itanes.data.remote.api;

import java.util.List;

import pe.josueyovera.itanes.data.remote.dto.PlaceRemoteDto;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ItanesApiService {

    // El endpoint real debe ser reemplazado aquí
    @GET("places.json")
    Call<List<PlaceRemoteDto>> getPlaces();
}
