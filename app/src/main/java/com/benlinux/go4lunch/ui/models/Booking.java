package com.benlinux.go4lunch.ui.models;


public class Booking {

    private String bookingId;
    private String restaurantId;
    private String restaurantName;
    private String restaurantPicture;

    private String fullAddress;
    // user who booked
    private String userId;
    private String bookingDate;

    public Booking(){}


    public Booking(String bookingId, String restaurantId, String restaurantName, String fullAddress, String restaurantPicture, String userId, String bookingDate) {
        this.bookingId = bookingId;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.fullAddress = fullAddress;
        this.restaurantPicture = restaurantPicture;
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

    public String getFullAddress() {
        return fullAddress;
    }
    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getRestaurantPicture() {
        return restaurantPicture;
    }
    public void setRestaurantPicture(String restaurantPicture) {
        this.restaurantPicture = restaurantPicture;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookingDate() {
        return bookingDate;
    }
    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

}
