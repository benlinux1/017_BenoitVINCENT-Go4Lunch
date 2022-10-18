package com.benlinux.go4lunch.ui.models;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Restaurant {

    /**
     * Model used for Restaurant objects
     */

    private String id;
    private String name;
    private String address;
    private String style;
    private Double rating;
    private Boolean open;
    private String distance;
    private LatLng latLng;
    private String pictureUrl;
    private String hours;
    @Nullable
    private List<Booking> bookings;

    public Restaurant(String id, String name, String address, Double rating, String hours, String distance, LatLng latLng, @Nullable List<Booking> bookings) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.hours = hours;
        this.distance = distance;
        this.latLng = latLng;
        this.bookings = bookings;
    }

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

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    @Nullable
    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
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
