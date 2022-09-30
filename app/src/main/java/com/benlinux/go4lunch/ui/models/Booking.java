package com.benlinux.go4lunch.ui.models;

import java.util.Date;

public class Booking {

    private String bookingId;
    private String restaurantId;
    private String restaurantName;
    // user who booked
    private String userId;
    private Date bookingDate;


    public Booking(String bookingId, String restaurantId, String restaurantName, String userId, Date bookingDate) {
        this.bookingId = bookingId;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.userId = userId;
        this.bookingDate = bookingDate;
    }

    public String getBookingId() {
        return bookingId;
    }
    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }
    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getBookingDate() {
        return bookingDate;
    }
    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }


}
