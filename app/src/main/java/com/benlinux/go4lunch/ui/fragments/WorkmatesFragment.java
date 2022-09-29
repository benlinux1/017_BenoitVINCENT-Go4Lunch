package com.benlinux.go4lunch.ui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.benlinux.go4lunch.databinding.FragmentWorkmatesBinding;

import com.benlinux.go4lunch.ui.adapters.WorkmateAdapter;
import com.benlinux.go4lunch.ui.models.Workmate;
import com.google.android.material.divider.MaterialDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesFragment extends Fragment {

    private FragmentWorkmatesBinding binding;
    private WorkmateAdapter adapter;
    private List<Workmate> mWorkmates;
    private RecyclerView mRecyclerView;
    MaterialDividerItemDecoration divider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        mRecyclerView = binding.listWorkmates;
        mWorkmates = new ArrayList<>();
        mWorkmates.add(new Workmate(123L, "Scarlett", null, "Le Zinc"));
        mWorkmates.add(new Workmate(1234L, "Hugh", null, "Le Zinc"));
        mWorkmates.add(new Workmate(12345L, "Nana", null, "Le Seoul"));
        configRecyclerView();

        return view;
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
        adapter.notifyItemRangeInserted(- 1, mWorkmates.size());
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