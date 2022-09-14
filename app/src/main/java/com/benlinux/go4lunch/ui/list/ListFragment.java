package com.benlinux.go4lunch.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.benlinux.go4lunch.databinding.FragmentListViewBinding;
import com.benlinux.go4lunch.ui.models.Restaurant;

import org.json.JSONArray;

import java.util.ArrayList;

public class ListFragment extends Fragment {

    private FragmentListViewBinding binding;
    private ListAdapter adapter;

    // TODO : private NearByApiService mNearByService;
    private JSONArray mRestaurants;
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

        mRestaurants = new ListViewModel().getRestaurants();
        configRecyclerView();
        initList();

        return view;
    }


    /**
     * Init the recyclerView
     */
    private void configRecyclerView() {
        mRecyclerView = binding.listRestaurants;
        adapter = new ListAdapter(mRestaurants);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(adapter);
    }


    /**
     * Init the List of restaurants
     */
    private void initList() {
        adapter.initList(mRestaurants);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}