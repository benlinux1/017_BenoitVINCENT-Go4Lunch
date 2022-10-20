package com.benlinux.go4lunch.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.benlinux.go4lunch.R;
import com.benlinux.go4lunch.data.bookingManager.BookingManager;
import com.benlinux.go4lunch.data.userManager.UserManager;
import com.benlinux.go4lunch.databinding.ActivityLunchBinding;
import com.benlinux.go4lunch.ui.adapters.BookingAdapter;
import com.benlinux.go4lunch.ui.adapters.GuestAdapter;
import com.benlinux.go4lunch.ui.models.Booking;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class UserLunchActivity extends AppCompatActivity {

    private ActivityLunchBinding binding;
    private RecyclerView bookingRecyclerView;
    @SuppressLint("StaticFieldLeak")
    private static TextView noBookingText;
    private static List<Booking> userBookings;

    // FOR DATA
    private final UserManager userManager = UserManager.getInstance();
    private final BookingManager bookingManager = BookingManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLunchBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);
        setToolbar();
        setViews();
        checkUserBookings();

    }

    private void setToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Your lunch");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Close details page and turn back to main activity if back button is clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            MainActivity.navigate(this);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Define views
    private void setViews() {
        bookingRecyclerView = binding.listBookings;
        noBookingText = binding.textBookings;
    }

    public void configureRecyclerView() {
        BookingAdapter adapter = new BookingAdapter(userBookings, this);
        bookingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookingRecyclerView.setHasFixedSize(true);
        bookingRecyclerView.setAdapter(adapter);
        showTextIfNoBooking();
    }


    public static void showTextIfNoBooking() {
        // If user bookings list is empty, show notification text instead of recyclerview
        if (userBookings.size() == 0) {
            noBookingText.setText(R.string.no_booking_for_user);
        }
    }

    private void checkUserBookings() {

        bookingManager.getAllBookingsData().addOnCompleteListener(new OnCompleteListener<List<Booking>>() {
            @Override
            public void onComplete(@NonNull Task<List<Booking>> task) {

                Calendar currentDate = Calendar.getInstance(Locale.FRANCE);

                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String today = dateFormat.format(currentDate.getTime());

                String userId = userManager.getCurrentUser().getUid();

                boolean userBookedToday = false;

                userBookings = new ArrayList<>();

                for (Booking existingBooking : task.getResult()) {
                    // If current user booked for today
                    if (Objects.equals(existingBooking.getUserId(), userId) &&
                            Objects.equals(existingBooking.getBookingDate(), today)) {
                        userBookedToday = true;
                        break;
                    }
                }
                for (Booking existingBooking : task.getResult()) {
                    // Add user bookings to list
                    if (Objects.equals(existingBooking.getUserId(), userId)) {
                        userBookings.add(existingBooking);
                    }
                }
                setUserBookingsList(userBookings);
                configureRecyclerView();
            }
        });


    }

    private void setUserBookingsList(List<Booking> bookings) {
        this.userBookings = bookings;
    }

    private List<Booking> getUserBookingsList() {
        return this.userBookings;
    }
}
