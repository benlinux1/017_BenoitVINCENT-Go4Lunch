package com.benlinux.go4lunch.modules;

public class FormatRatingModule {

    // Format number of rating stars (between 0.5 and 3) as asked from client
    public static Double formatRating(Double rating) {
        Double formattedRating = null;
        if (rating >= 0 && rating <= 0.8) {
            formattedRating = 0.5;
        } else if (rating > 0.8 && rating <= 1.6) {
            formattedRating = 1.0;
        } else if (rating > 1.6 && rating <= 2.5) {
            formattedRating = 1.5;
        } else if (rating > 2.5 && rating <= 3.4) {
            formattedRating = 2.0;
        } else if (rating > 3.4 && rating <= 4.3) {
            formattedRating = 2.5;
        } else if (rating > 4.3 && rating <= 5.0) {
            formattedRating = 3.0;
        }
        return formattedRating;
    }
}
