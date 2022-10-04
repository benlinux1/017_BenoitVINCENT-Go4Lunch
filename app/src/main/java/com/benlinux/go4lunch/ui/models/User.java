package com.benlinux.go4lunch.ui.models;

import androidx.annotation.Nullable;


public class User {

    private String uid;
    private String name;

    private String email;

    private String avatar;
    private String restaurantOfTheDay;
    private Boolean notified;

    public User(){}


    public User(String uid, String name, String email, String avatar,
                String restaurantOfTheDay, Boolean notified) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.restaurantOfTheDay = restaurantOfTheDay;
        this.notified = notified;
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

    @Nullable
    public Boolean isNotified() {
        return notified;
    }
    public void setIsNotified(Boolean isNotified) {
        this.notified = isNotified;
    }
}
