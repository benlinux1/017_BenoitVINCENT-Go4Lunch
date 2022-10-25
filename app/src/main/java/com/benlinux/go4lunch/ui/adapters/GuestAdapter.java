package com.benlinux.go4lunch.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.benlinux.go4lunch.data.userManager.UserManager;
import com.benlinux.go4lunch.ui.models.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Objects;


public class GuestAdapter extends RecyclerView.Adapter<GuestAdapter.ViewHolder> {

    private List<String> mGuests;

    // FOR DATA
    private final UserManager userManager = UserManager.getInstance();

    /**
     * Instantiates a new ListAdapter.
     * @param guests the list of restaurants the adapter deals with to set
     */
    public GuestAdapter(List<String> guests) {
        mGuests = guests;
    }


    @NonNull
    @Override
    public GuestAdapter.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workmates_list, parent, false);
        return new GuestAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final GuestAdapter.ViewHolder holder, int position) {
        // bind restaurant according to position in the list
        holder.bind(mGuests.get(position));

        // Launch Restaurant Details according to the Restaurant Id
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View restaurantItem) {
                // On workmate item click, display restaurant details

            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    public void initList(List<String> guests) {
        this.mGuests = guests;
        notifyDataSetChanged();
    }


    public String getItem(int i) {
        return mGuests.get(i);
    }


    @Override
    public int getItemCount() {
        return mGuests.size();
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
            restaurantId = itemView.findViewById(R.id.restaurant_id);
        }


        /**
         * Binds data to the item view.
         * @param userId the restaurant to bind in the item view
         */
        void bind(String userId) {
            userManager.getAllUsersData().addOnCompleteListener(new OnCompleteListener<List<User>>() {
                @Override
                public void onComplete(@NonNull Task<List<User>> task) {
                    for (User user : task.getResult()) {
                        StringBuilder sb = new StringBuilder();
                        if (user.getId().equals(userId) && !Objects.equals(user.getId(), userManager.getCurrentUser().getUid())) {
                            sb.append(user.getName()).append(" is joining !");
                            name.setText(sb.toString());
                            if (user.getAvatar() != null) {
                                Glide.with(avatar.getContext())
                                    .load(user.getAvatar())
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
            });
        }
    }
}
