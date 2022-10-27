package com.benlinux.go4lunch.ui.adapters;

import static com.benlinux.go4lunch.activities.MainActivity.userLocation;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.benlinux.go4lunch.R;
import com.benlinux.go4lunch.activities.RestaurantDetailsActivity;
import com.benlinux.go4lunch.ui.models.User;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Objects;

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

        // Set holder text color & clickable or not according to booking
        if (holder.restaurantId.getText().equals("")) {
            holder.itemView.setClickable(false);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View restaurantItem) {
                    Toast.makeText(localContext, R.string.no_restaurant, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Launch Restaurant Details according to the Restaurant Id
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View restaurantItem) {
                    // On workmate item click, display restaurant details
                    Intent restaurantDetailsActivityIntent = new Intent(restaurantItem.getContext(), RestaurantDetailsActivity.class);
                    restaurantDetailsActivityIntent.putExtra("PLACE_ID", holder.restaurantId.getText());
                    restaurantDetailsActivityIntent.putExtra("USER_LOCATION", userLocation);
                    restaurantItem.getContext().startActivity(restaurantDetailsActivityIntent);
                }
            });
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void initList(List<User> workmates) {
        this.mWorkmates = workmates;
        notifyDataSetChanged();
    }


    public User getItem(int i) {
        return mWorkmates.get(i);
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
         * The square picture of the workmate
         */
        private final ImageView avatar;

        /**
         * The TextView displaying the id of the workmate
         */
        private final TextView restaurantId;

        /**
         * The TextView displaying the name of the restaurant of the day
         */
        private final TextView name;



        /**
         * Instantiates a new Restaurant ViewHolder.
         * @param itemView the view of the restaurant item
         */
        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.workmate_avatar);
            name = itemView.findViewById(R.id.workmate_name);
            restaurantId = itemView.findViewById(R.id.item_id);
        }


        /**
         * Binds data to the item view.
         * @param workmate the restaurant to bind in the item view
         */
        void bind(User workmate) {
            StringBuilder booking = new StringBuilder();
            if (!Objects.equals(workmate.getRestaurantName(), "")) {
                booking.append(workmate.getName()).append(localContext.getString(R.string.user_is_eating_to)).append(workmate.getRestaurantName());
            } else {
                booking.append(workmate.getName()).append(localContext.getString(R.string.not_decided));
                name.setTextColor(Color.parseColor("#808080"));
            }

            // Set name & booking of the day
            name.setText(booking.toString());
            // Set restaurant id
            restaurantId.setText(workmate.getRestaurantId());
            // Set avatar
            if (workmate.getAvatar() != null) {
                Glide.with(avatar.getContext())
                    .load(workmate.getAvatar())
                    .circleCrop()
                    .into(avatar);
            } else {
                Glide.with(avatar.getContext())
                    .load(R.mipmap.no_photo)
                    .circleCrop()
                    .into(avatar);
            }
        }
    }

}