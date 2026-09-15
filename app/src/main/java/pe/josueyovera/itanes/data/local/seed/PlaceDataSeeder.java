package pe.josueyovera.itanes.data.local.seed;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pe.josueyovera.itanes.data.local.entity.PlaceEntity;
import pe.josueyovera.itanes.data.repository.PlaceRepository;

public class PlaceDataSeeder {

    private static final String TAG = "PlaceDataSeeder";
    private final PlaceRepository repository;
    private final ExecutorService executorService;

    public PlaceDataSeeder(PlaceRepository repository) {
        this.repository = repository;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void seed() {
        executorService.execute(() -> {
            int count = repository.getCount();
            if (count == 0) {
                List<PlaceEntity> places = new ArrayList<>();
                
                places.add(new PlaceEntity(1, 
                    "Plaza Mayor de Lima", 
                    "El corazón histórico y fundacional de la capital peruana.", 
                    "También conocida como la Plaza de Armas, es el sitio donde Francisco Pizarro fundó Lima en 1535. Está rodeada por importantes edificios gubernamentales y religiosos como el Palacio de Gobierno, la Municipalidad de Lima y la Catedral.", 
                    "Jr. de la Unión y Jr. Huallaga, Cercado de Lima, Lima",
                        -12.045932516674503, -77.03055243558224,
                    "https://larepublica.cronosmedia.glr.pe/original/2023/08/13/64d94efa1ed444231a307259.jpg", 
                    1, "2026-08-31"));

                places.add(new PlaceEntity(2, 
                    "Basílica Catedral de Lima", 
                    "Imponente joya arquitectónica del Centro Histórico de Lima.", 
                    "Ubicada en la Plaza Mayor, su construcción se inició en el siglo XVI y combina elementos renacentistas, barrocos y neoclásicos. En su interior alberga las criptas de personajes históricos como Francisco Pizarro y valiosas obras de arte sacro.", 
                    "Plaza Mayor, Cercado de Lima, Lima",
                        -12.046642925429207, -77.02964749669181,
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQdNNdU59p3DsUk7VZDHyTKlsdqexIS8a4G_y2MUWOGzCIWF2hQaeLCttA&s=10", 
                    2, "2026-08-31"));

                places.add(new PlaceEntity(3, 
                    "Plaza San Martín", 
                    "Emblemática plaza inaugurada in 1921 para el centenario de la independencia.", 
                    "Nombrada en honor al libertador Don José de San Martín, destaca por su monumento central y los majestuosos edificios de estilo francés que la rodean, como el histórico Hotel Bolivar y el Club Naútico. Es un punto de gran valor cultural y arquitectónico.", 
                    "Av. Colmena y Jr. de la Unión, Cercado de Lima, Lima", 
                    -12.051651250368002, -77.03459865767091, 
                    "https://cuscoperu.b-cdn.net/wp-content/uploads/2026/06/Plaza-San-Martin.jpg", 
                    3, "2026-08-31"));

                places.add(new PlaceEntity(4, 
                    "Circuito Mágico del Agua", 
                    "El complejo de fuentes de agua más grande del mundo en un parque público.", 
                    "Situado en el Parque de la Reserva, ofrece un espectáculo nocturno inolvidable de fuentes cibernéticas sincronizadas con luces láser, música y proyecciones de imágenes sobre cortinas de agua. Cuenta con trece fuentes ornamentales e interactivas.", 
                    "Parque de la Reserva, Av. Petit Thouars s/n, Cercado de Lima, Lima", 
                    -12.070326905458517, -77.03285531419841, 
                    "https://www.ytuqueplanes.com/imagenes//fotos/turismo-urbano/distrito/atractivo/detalle/Circuito-M%C3%A1gico-del-Agua-en-el-Parque-de-la-Reserva-736-x-446.webp",
                    4, "2026-08-31"));

                places.add(new PlaceEntity(5,
                    "Museo de Sitio Huaca Pucllana", 
                    "Antiguo centro ceremonial preincaico en medio de la ciudad moderna.", 
                    "Corresponde a un impresionante complejo arqueológico de la cultura Lima (construido entre los años 200 y 700 d.C.) hecho a base de adobes organizados en forma de \"libros estibados\". Incluye un museo de sitio, un circuito de visitas guiadas y un restaurante con vista panorámica.", 
                    "Calle General Borgoño cdra. 8, Miraflores, Lima", 
                    -12.110967017200519, -77.03362896126355,
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRiFWJCiC53QfEs8I6Uvoi26x8UCs3AmJLIXS1eUWhcpcs3vERQWKB3LFkZ&s=10",
                    5, "2026-08-31"));

                repository.insertPlaces(places);
                Log.d(TAG, "PlaceDataSeeder: 5 lugares insertados en Room");
            } else {
                Log.d(TAG, "PlaceDataSeeder: " + count + " lugares disponibles en Room");
            }
        });
    }
}
