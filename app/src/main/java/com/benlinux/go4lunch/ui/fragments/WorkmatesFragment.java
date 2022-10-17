package com.benlinux.go4lunch.ui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.benlinux.go4lunch.R;
import com.benlinux.go4lunch.data.userManager.UserManager;
import com.benlinux.go4lunch.databinding.FragmentWorkmatesBinding;

import com.benlinux.go4lunch.ui.adapters.WorkmateAdapter;
import com.benlinux.go4lunch.ui.models.User;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.android.material.divider.MaterialDividerItemDecoration;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class WorkmatesFragment extends Fragment {

    private FragmentWorkmatesBinding binding;
    private WorkmateAdapter adapter;
    private List<User> mWorkmates;
    private RecyclerView mRecyclerView;
    MaterialDividerItemDecoration divider;

    String myId;

    // FOR DATA
    private final UserManager userManager = UserManager.getInstance();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myId = userManager.getCurrentUser().getUid();
        getWorkmatesFromFireStore();


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mRecyclerView = binding.listWorkmates;


        return view;
    }


    // Set Workmates in Recycler View
    private void getWorkmatesFromFireStore() {
        // Get workmates from firestore
        this.userManager.getAllUsersData().addOnCompleteListener(new OnCompleteListener<List<User>>() {
            @Override
            public void onComplete(@NonNull Task<List<User>> task) {
                if (task.isSuccessful()) {
                    // if result OK, set workmates list & configure recycler view
                    List<User> workmates = new ArrayList<>();
                    String currentUserId = userManager.getCurrentUser().getUid();
                    for (User user : task.getResult()) {
                        // Add workmates except current user to list
                        if (!Objects.equals(user.getId(), currentUserId)) {
                            workmates.add(user);
                        }
                    }
                    if (workmates != null) {
                        setWorkmatesList(workmates);
                        configRecyclerView();
                    }
                    // Populate workmates List if no user in Database
                    if (workmates == null || workmates.isEmpty()) {
                        mWorkmates = new ArrayList<>();
                        List<String> favorites = Collections.emptyList();
                        mWorkmates.add(new User("1", "Scarlett", "scarlett@test.com", null, "Le Zinc", true, favorites));
                        mWorkmates.add(new User("2", "Hugh", "hugh@test.com", null, "Le Zinc", true, favorites));
                        mWorkmates.add(new User("3", "Nana", "nana@test.com", null, "Le Seoul", true, favorites));
                    }
                }
            }
        });
    }


    private void setWorkmatesList(List<User> workmates) {
        this.mWorkmates = workmates;
    }

    private List<User> getWorkmatesList() {
        return this.mWorkmates;
    }


    /**
     * Init the recyclerView that contains workmates
     */
    private void configRecyclerView() {
        adapter = new WorkmateAdapter(getWorkmatesList(), getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        divider = new MaterialDividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        divider.setDividerInsetStart(200);
        mRecyclerView.addItemDecoration(divider);

        mRecyclerView.setAdapter(adapter);



        /**
        adapter.notifyItemRangeInserted(-1, mWorkmates.size());
        if (mWorkmates.size() == 0) {
           binding.textWorkmates.setText("No available workmates");
        }
         */



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}