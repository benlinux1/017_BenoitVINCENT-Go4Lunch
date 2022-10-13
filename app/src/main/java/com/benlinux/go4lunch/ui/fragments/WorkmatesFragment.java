package com.benlinux.go4lunch.ui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.benlinux.go4lunch.data.userManager.UserManager;
import com.benlinux.go4lunch.databinding.FragmentWorkmatesBinding;

import com.benlinux.go4lunch.ui.adapters.WorkmateAdapter;
import com.benlinux.go4lunch.ui.models.User;
import com.google.android.gms.tasks.OnFailureListener;

import com.google.android.gms.tasks.Task;
import com.google.android.material.divider.MaterialDividerItemDecoration;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class WorkmatesFragment extends Fragment {

    private FragmentWorkmatesBinding binding;
    private WorkmateAdapter adapter;
    private List<User> mWorkmates;
    private RecyclerView mRecyclerView;
    MaterialDividerItemDecoration divider;

    // FOR DATA
    private final UserManager userManager = UserManager.getInstance();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWorkmates = getWorkmatesList();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mRecyclerView = binding.listWorkmates;

        configRecyclerView();

        return view;
    }

    // TODO : FIX EMPTY mWorkmates ArrayList !!!
    private List<User> getWorkmatesList() {
        Task<List<User>> getUsersData = userManager.getAllUsersData();
        mWorkmates = new ArrayList<User>();
        getUsersData.addOnSuccessListener(users -> {
            mWorkmates.addAll(users);
        });

        getUsersData.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("EXCEPTION", e.toString());
            }
        });

        if (mWorkmates == null || mWorkmates.isEmpty()) {
            List<String> favorites = Collections.emptyList();
            mWorkmates.add(new User("1", "Scarlett", "scarlett@test.com", null, "Le Zinc", true, favorites));
            mWorkmates.add(new User("2", "Hugh","hugh@test.com", null, "Le Zinc", true, favorites));
            mWorkmates.add(new User("3", "Nana", "nana@test.com",  null, "Le Seoul", true, favorites));
        }

        return mWorkmates;
    }


    /**
     * Init the recyclerView that contains workmates
     */
    private void configRecyclerView() {
        adapter = new WorkmateAdapter(mWorkmates, getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        divider = new MaterialDividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        divider.setDividerInsetStart(200);
        mRecyclerView.addItemDecoration(divider);

        mRecyclerView.setAdapter(adapter);

        adapter.initList(mWorkmates);

        adapter.notifyItemRangeInserted(-1, mWorkmates.size());
        if (mWorkmates.size() == 0) {
           binding.textWorkmates.setText("No available workmates");
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}