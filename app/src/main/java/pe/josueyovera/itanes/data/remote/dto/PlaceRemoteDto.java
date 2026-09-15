package pe.josueyovera.itanes.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class PlaceRemoteDto {

    private int id;
    private String name;
    private String shortDescription;
    private String description;
    private String address;
    private double latitude;
    private double longitude;
    private String imageUrl;
    private int orderNumber;
    private String updatedAt;

    public PlaceRemoteDto() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
