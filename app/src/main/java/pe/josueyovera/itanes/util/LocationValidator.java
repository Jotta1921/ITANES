package pe.josueyovera.itanes.util;

public class LocationValidator {
    public static boolean isValid(double latitude, double longitude) {
        return latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180;
    }
}
