package com.ccmu.profiler.ui.organization;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OrganizationViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public OrganizationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is organization fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}