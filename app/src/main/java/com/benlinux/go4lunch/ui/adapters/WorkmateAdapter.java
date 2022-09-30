package com.benlinux.go4lunch.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.benlinux.go4lunch.R;
import com.benlinux.go4lunch.activities.RestaurantDetailsActivity;
import com.benlinux.go4lunch.ui.models.Booking;
import com.benlinux.go4lunch.ui.models.User;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Random;

public class WorkmateAdapter extends RecyclerView.Adapter<WorkmateAdapter.ViewHolder> {

    private List<User> mWorkmates;
    private final Context localContext;

    /**
     * Instantiates a new ListAdapter.
     * @param workmates the list of restaurants the adapter deals with to set
     */
    public WorkmateAdapter(List<User> workmates, Context context) {
        mWorkmates = workmates;
        localContext = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workmates_list, parent, false);
        return new WorkmateAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WorkmateAdapter.ViewHolder holder, int position) {
        // bind restaurant according to position in the list
        holder.bind(mWorkmates.get(position));

        // Launch Restaurant Details according to the Restaurant Id
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View restaurantItem) {
                Intent restaurantDetailsActivityIntent = new Intent(restaurantItem.getContext(), RestaurantDetailsActivity.class);
                restaurantDetailsActivityIntent.putExtra("PLACE_ID", holder.id.getText());
                restaurantItem.getContext().startActivity(restaurantDetailsActivityIntent);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void initList(List<User> workmates) {
        this.mWorkmates = workmates;
        notifyItemRangeChanged(- 1, mWorkmates.size());
    }


    @Override
    public int getItemCount() {
        return mWorkmates.size();
    }

    /**
     * <p>ViewHolder for restaurants items in the restaurants list</p>
     * @author BenLinux1
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * The square picture of the restaurant
         */
        private final ImageView avatar;

        /**
         * The TextView displaying the id of the restaurant
         */
        private final TextView id;

        /**
         * The TextView displaying the name of the restaurant
         */
        private final TextView name;



        /**
         * Instantiates a new Restaurant ViewHolder.
         * @param itemView the view of the restaurant item
         */
        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.workmate_avatar);
            id = itemView.findViewById(R.id.workmate_id);
            name = itemView.findViewById(R.id.workmate_name);
        }


        /**
         * Binds data to the item view.
         * @param workmate the restaurant to bind in the item view
         */
        void bind(User workmate) {
            StringBuilder booking = new StringBuilder();
            booking.append(workmate.getName()).append(" is eating to ").append(workmate.getRestaurantOfTheDay());
            // Set name & booking of the day
            name.setText(booking.toString());
            // Set id
            id.setText(workmate.getId());
            // Set avatar
            if (workmate.getAvatar() != null) {
                Glide.with(avatar.getContext())
                        .load(workmate.getAvatar())
                        .centerCrop()
                        .into(avatar);
            } else {
                String mAvatarColor = generateRandomColor();
                avatar.setColorFilter(Color.parseColor(mAvatarColor));
            }

        }

        public String generateRandomColor() {
            // create object of Random class
            Random obj = new Random();
            int rand_num = obj.nextInt(0xffffff + 1);
            // format it as hexadecimal string and print
            String colorCode = String.format("#%06x", rand_num);
            return colorCode;
        }
    }






}