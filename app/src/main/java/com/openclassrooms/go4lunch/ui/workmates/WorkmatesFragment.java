package com.openclassrooms.go4lunch.ui.workmates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.openclassrooms.go4lunch.databinding.FragmentWorkmatesBinding;

public class WorkmatesFragment extends Fragment {

    private FragmentWorkmatesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        WorkmatesViewModel workmatesViewModel =
                new ViewModelProvider(this).get(WorkmatesViewModel.class);

        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textWorkmates;
        workmatesViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}