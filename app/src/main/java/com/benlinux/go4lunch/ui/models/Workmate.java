package com.benlinux.go4lunch.ui.models;

import android.media.Image;


public class Workmate {

    private Long id;
    private String name;
    private Image avatar;
    private String restaurant;


    public Workmate(Long id, String name, Image avatar, String restaurant) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.restaurant = restaurant;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getAvatar() {
        return avatar;
    }

    public void setAvatar(Image avatar) {
        this.avatar = avatar;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }
}
