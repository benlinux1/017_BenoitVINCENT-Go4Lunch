package com.benlinux.go4lunch.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WorkmatesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public WorkmatesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Workmates fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}