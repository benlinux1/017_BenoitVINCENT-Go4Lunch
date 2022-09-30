package com.benlinux.go4lunch.ui.models;

import androidx.annotation.Nullable;


public class User {

    private String uid;
    private String name;
    private String email;
    @Nullable
    private  String urlAvatar;
    private String restaurantOfTheDay;
    private Boolean isNotified;


    public User(String uid, String name, String email, @Nullable String urlAvatar,
                String restaurantOfTheDay, Boolean isNotified) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.urlAvatar = urlAvatar;
        this.restaurantOfTheDay = restaurantOfTheDay;
        this.isNotified = isNotified;
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

    @Nullable
    public String getAvatar() {
        return urlAvatar;
    }
    public void setAvatar(String avatar) {
        this.urlAvatar = urlAvatar;
    }

    public String getRestaurantOfTheDay() {
        return restaurantOfTheDay;
    }
    public void setRestaurantOfTheDay(String restaurant) {
        this.restaurantOfTheDay = restaurantOfTheDay;
    }

    public Boolean isNotified() {
        return isNotified;
    }
    public void setIsNotified(Boolean isNotified) {
        this.isNotified = isNotified;
    }
}
