package com.benlinux.go4lunch.ui.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

public class Restaurant {

    /**
     * Model used for Restaurant objects
     */

    private String id;
    private String name;
    private String address;
    private String style;
    private Float rating;
    private Boolean open;
    private Float distance;
    private LatLng latLng;
    private String pictureUrl;

    public Restaurant(String id) {
        this.id = id;
    }

    public Restaurant() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return  false;
        Restaurant restaurant = (Restaurant) o;
        return id.equals(restaurant.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
