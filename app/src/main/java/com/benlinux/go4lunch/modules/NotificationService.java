package com.benlinux.go4lunch.modules;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.benlinux.go4lunch.R;
import com.benlinux.go4lunch.activities.UserLunchActivity;
import com.benlinux.go4lunch.data.bookingManager.BookingManager;
import com.benlinux.go4lunch.data.userManager.UserManager;
import com.benlinux.go4lunch.ui.models.Booking;
import com.benlinux.go4lunch.ui.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NotificationService extends BroadcastReceiver {

    // FOR DATA
    private final UserManager userManager = UserManager.getInstance();
    private final BookingManager bookingManager = BookingManager.getInstance();
    private Context context;

    private List<User> allUsers;
    private List<Booking> bookingsOfToday;
    private String workmatesNotificationText = null;
    private String userID = null;
    private String restaurantName = null;
    private String restaurantAddress = null;
    private String restaurantId = null;

    /**
     * When receive signal, check bookings of day
     * update database in consequence for each user
     * send notification if user booked today
     * @param context is application context
     * @param intent is define in MainActivity to launch this notification service
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        this.userID = userManager.getCurrentUser().getUid();

        // Check booking of today
        getBookingsOfToday().addOnCompleteListener(new OnCompleteListener<List<Booking>>() {
            @Override
            public void onComplete(@NonNull Task<List<Booking>> task) {
                // update database
                setBookingsOfTodayInUserDatabase().addOnCompleteListener(new OnCompleteListener<List<User>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<User>> task) {
                        userManager.getUserData().addOnCompleteListener(new OnCompleteListener<User>() {
                            @Override
                            // update notification variables (restaurant address, name & workmates)
                            public void onComplete(@NonNull Task<User> user) {
                                setRestaurantName(user.getResult().getRestaurantName());
                                setRestaurantAddress(user.getResult().getRestaurantAddress());
                                setRestaurantId(user.getResult().getRestaurantId());
                                setUserJoiningList();
                            }
                        });
                    }
                });
            }
        });
    }

    private void setRestaurantName(String name) {
        this.restaurantName = name;
    }

    private void setRestaurantAddress(String address) {
        this.restaurantAddress = address;
    }

    private void setRestaurantId(String id) {
        this.restaurantId = id;
    }


    // Check all bookings of day
    private Task<List<Booking>> getBookingsOfToday() {
        return bookingManager.getAllBookingsData().addOnCompleteListener(new OnCompleteListener<List<Booking>>() {
            @Override
            public void onComplete(@NonNull Task<List<Booking>> bookingTask) {
                bookingsOfToday = new ArrayList<>();
                final Calendar currentDate = Calendar.getInstance(Locale.FRANCE);
                // Define today formatted date
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String today = dateFormat.format(currentDate.getTime());
                for (Booking booking : bookingTask.getResult()) {
                    if (booking.getBookingDate().equals(today)) {
                        bookingsOfToday.add(booking);
                    }
                }
            }
        });
    }


    // Update users restaurants of day for each of them, according to bookings of day
    private Task<List<User>> setBookingsOfTodayInUserDatabase() {
        return userManager.getAllUsersData().addOnCompleteListener(new OnCompleteListener<List<User>>() {
            @Override
            public void onComplete(@NonNull Task<List<User>> users) {
                allUsers = users.getResult();
                for (User user : allUsers) {
                    for (Booking bookingOfDay : bookingsOfToday) {
                        if (bookingOfDay.getUserId().equals(user.getId())) {
                            userManager.updateUserRestaurantOfTheDay(user.getId(), bookingOfDay.getRestaurantName());
                            userManager.updateUserRestaurantAddressOfTheDay(user.getId(), bookingOfDay.getFullAddress());
                            userManager.updateUserRestaurantIdOfTheDay(user.getId(), bookingOfDay.getRestaurantId());
                            break;
                        } else {
                            userManager.updateUserRestaurantOfTheDay(user.getId(), "");
                            userManager.updateUserRestaurantAddressOfTheDay(user.getId(), "");
                            userManager.updateUserRestaurantIdOfTheDay(user.getId(), "");
                        }
                    }
                    if (bookingsOfToday.isEmpty()) {
                        userManager.updateUserRestaurantOfTheDay(user.getId(), "");
                        userManager.updateUserRestaurantAddressOfTheDay(user.getId(), "");
                        userManager.updateUserRestaurantIdOfTheDay(user.getId(), "");
                    }
                }
            }
        });
    }



    // Define workmates who join user & send notification
    private void setUserJoiningList() {

        List<User> userJoining = new ArrayList<>();
        StringBuilder userWhoJoinStringBuilder = new StringBuilder();

        // Check workmates who booked in the same restaurant, except current user
        for (Booking booking : bookingsOfToday) {
            if (!booking.getUserId().equals(userID) && booking.getRestaurantId().equals(restaurantId)) {
                for (User user : allUsers) {
                    if (booking.getUserId().equals(user.getId())) {
                        userJoining.add(user);
                        break;
                    }
                }
            }
        }


        // Define workmates text according to number of them who join
        if (userJoining.size() == 1) {
            userWhoJoinStringBuilder.append(userJoining.get(0).getName()).append(context.getString(R.string.is_joining));
            workmatesNotificationText = userWhoJoinStringBuilder.toString();
        } else if (userJoining.size() > 1) {
            for (User user : userJoining) {
                userWhoJoinStringBuilder.append(user.getName()).append(", ");
            }
            userWhoJoinStringBuilder.substring(0, userWhoJoinStringBuilder.length()-2);
            userWhoJoinStringBuilder.append(" ").append(context.getString(R.string.are_joining));
            workmatesNotificationText = userWhoJoinStringBuilder.toString();

        } else {
            workmatesNotificationText = context.getString(R.string.notification_you_are_eating_alone);
        }

        // Send notification if current user booked today
        for (Booking booking : bookingsOfToday) {
            if (booking.getUserId().equals(userID)) {
                sendNotification(context);
                break;
            }
        }
    }


    // Define & build notification with all parameters
    @SuppressLint("UnspecifiedImmutableFlag")
    void sendNotification(Context context) {
        String CHANNEL_ID = context.getResources().getString(R.string.app_name);
        CharSequence appName = context.getResources().getString(R.string.app_name);
        CharSequence notificationTitle = context.getString(R.string.notification_title);
        StringBuilder messageBuilder = new StringBuilder();

        String restaurantNotificationText = restaurantName + ", " + restaurantAddress + ".";

        CharSequence finalNotificationMessage = messageBuilder.append(restaurantNotificationText).append(" ").append(workmatesNotificationText);

        // Define notification builder
        NotificationCompat.Builder mBuilder;

        // Define notification intent to redirect user to UserLunchActivity
        Intent notificationIntent = new Intent(context, UserLunchActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Define intent with flag that update current activity if app is open
        PendingIntent contentIntent;
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        } else {
            contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        // Define notification manager
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        // Set notification parameters & add notification channel if build version >= 26
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, appName, NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(mChannel);
            mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLights(context.getResources().getColor(R.color.colorPrimary, context.getTheme()), 2000, 500)
                    .setContentTitle(notificationTitle);
        } else {
            mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentTitle(notificationTitle);
        }

        // Assert that user is notified once
        mBuilder.setOnlyAlertOnce(true);
        // Set notification intent
        mBuilder.setContentIntent(contentIntent);
        // Set notification message
        mBuilder.setContentText(finalNotificationMessage);
        // Auto-cancel notification
        mBuilder.setAutoCancel(true);
        // Build notification
        mNotificationManager.notify(1, mBuilder.build());
    }
}
