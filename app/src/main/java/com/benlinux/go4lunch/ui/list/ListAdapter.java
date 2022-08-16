package com.benlinux.go4lunch.ui.list;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;


import com.benlinux.go4lunch.R;
import com.benlinux.go4lunch.activities.RestaurantDetailsActivity;
import com.benlinux.go4lunch.ui.models.Restaurant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<Restaurant> mRestaurants;


    /**
     * Instantiates a new ListAdapter.
     *
     * @param restaurants the list of restaurants the adapter deals with to set
     */
    public ListAdapter(List<Restaurant> restaurants) {
        mRestaurants = restaurants;
    }


    /**
     * Updates the list of restaurants the adapter deals with.
     *
     * @param restaurants the list of tasks the adapter deals with to set
     */
    void updateRestaurants(@NonNull final List<Restaurant> restaurants) {
        this.mRestaurants = restaurants;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restaurant_list, parent, false);
        return new ListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListAdapter.ViewHolder holder, int position) {
        Restaurant restaurant = mRestaurants.get(position);

        // Launch Restaurant Details according to the Restaurant Id
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View restaurantItem) {
               Intent restaurantDetailsActivityIntent = new Intent(restaurantItem.getContext(), RestaurantDetailsActivity.class);
               restaurantDetailsActivityIntent.putExtra("RESTAURANT_ID", restaurant.getId());
               restaurantItem.getContext().startActivity(restaurantDetailsActivityIntent);
            }
        });
    }

    public void initList(List<Restaurant> mRestaurants) {
        this.mRestaurants = mRestaurants;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }

    /**
     * <p>ViewHolder for restaurants items in the restaurants list</p>
     *
     * @author BenLinux1
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * The square picture of the restaurant
         */
        private final ImageView picture;

        /**
         * The TextView displaying the name of the restaurant
         */
        private final TextView name;

        /**
         * The TextView displaying the style and the address of the restaurant
         */
        private final TextView styleAndAddress;

        /**
         * The TextView displaying the opening hours of the restaurant
         */
        private final TextView hours;

        /**
         * The TextView displaying the distance between the user and the restaurant
         */
        private final TextView distance;

        /**
         * The TextView displaying the number of workmates who booked a lunch in the restaurant
         */
        private final TextView numberOfBookings;


        /**
         * Instantiates a new Restaurant ViewHolder.
         *
         * @param itemView the view of the restaurant item
         */
        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            picture = itemView.findViewById(R.id.item_restaurant_picture);
            name = itemView.findViewById(R.id.item_restaurant_name);
            styleAndAddress = itemView.findViewById(R.id.item_restaurant_style_and_address);
            hours = itemView.findViewById(R.id.item_restaurant_hours);
            distance = itemView.findViewById(R.id.item_restaurant_distance);
            numberOfBookings = itemView.findViewById(R.id.item_restaurant_user);


            name.setText(name.getText());
            styleAndAddress.setText(styleAndAddress.getText());
            distance.setText(distance.getText());
            hours.setText(hours.getText());
            numberOfBookings.setText(numberOfBookings.getText());

        }


        /**
         * Binds data to the item view.
         *
         * @param restaurant the restaurant to bind in the item view
         */
        void bind(Restaurant restaurant) {
            // String styleAndAddressString = restaurant.getStyle() + R.string.restaurant_style_and_address_separator + restaurant.getAddress();
            name.setText(name.getText());
            styleAndAddress.setText(styleAndAddress.getText());
            distance.setText(distance.getText());
            /*
            Glide.with(itemView.getContext())
                    .load(restaurant.getPictureUrl())
                    .into(picture); */
            hours.setText(hours.getText());
            numberOfBookings.setText(numberOfBookings.getText());
        }

    }
}