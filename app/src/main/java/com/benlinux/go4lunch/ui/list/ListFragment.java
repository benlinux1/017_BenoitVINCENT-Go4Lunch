package com.benlinux.go4lunch.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.benlinux.go4lunch.databinding.FragmentListViewBinding;
import com.benlinux.go4lunch.ui.models.Restaurant;

import java.util.List;

public class ListFragment extends Fragment {

    private FragmentListViewBinding binding;
    private ListAdapter adapter;

    // TODO : private NearByApiService mNearByService;
    private List<Restaurant> mRestaurants;
    private RecyclerView mRecyclerView;

    /**
     * Create and return a new instance
     * @return @{@link ListFragment}
     */
    public static ListFragment newInstance() {
        ListFragment fragment = new ListFragment();
        return fragment;
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentListViewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();



        return view;
    }


    /**
     * Init the recyclerView
     */
    private void configRecyclerView() {
        mRecyclerView = binding.listRestaurants;
        adapter = new ListAdapter(this.mRestaurants);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(adapter);
    }


    /**
     * Init the List of restaurants
     */
    private void initList() {
        // TODO : mRestaurants = mNearByService.getRestaurants();
        // mRecyclerView.setAdapter(new ListAdapter(mRestaurants));
    }

    @Override
    public void onResume() {
        super.onResume();
        initList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}