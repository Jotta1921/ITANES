package pe.josueyovera.itanes.data.remote.retrofit;

import pe.josueyovera.itanes.data.remote.api.ItanesApiService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // La BASE_URL real debe ser reemplazada aquí. Debe terminar en /
    private static final String BASE_URL = "https://run.mocky.io/v3/";
    private static Retrofit retrofit = null;

    public static ItanesApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ItanesApiService.class);
    }
}
