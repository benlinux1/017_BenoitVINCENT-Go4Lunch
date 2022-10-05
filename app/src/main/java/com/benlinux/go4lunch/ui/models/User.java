package com.benlinux.go4lunch.ui.models;

import androidx.annotation.Nullable;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;


public class User {

    private String uid;
    private String name;
    private String email;
    private String avatar;
    private String restaurantOfTheDay;
    private Boolean notified;
    private List<String> favoriteRestaurants;

    public User(){}


    public User(String uid, String name, String email, String avatar,
                String restaurantOfTheDay, Boolean notified, List<String> favoriteRestaurants) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.restaurantOfTheDay = restaurantOfTheDay;
        this.notified = notified;
        this.favoriteRestaurants = favoriteRestaurants;
    }

    public String getId() {
        return uid;
    }
    public void setId(String id) {
        this.uid = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRestaurantOfTheDay() {
        return restaurantOfTheDay;
    }
    public void setRestaurantOfTheDay(String restaurant) {
        this.restaurantOfTheDay = restaurantOfTheDay;
    }

    public List<String> getFavoriteRestaurants() {
        return favoriteRestaurants;
    }
    public void setInFavoriteRestaurants(String restaurantId) {
        this.favoriteRestaurants.add(restaurantId);
    }

    @Nullable
    public Boolean isNotified() {
        return notified;
    }
    public void setIsNotified(Boolean isNotified) {
        this.notified = isNotified;
    }
}
