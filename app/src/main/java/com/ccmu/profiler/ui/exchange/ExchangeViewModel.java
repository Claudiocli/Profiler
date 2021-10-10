package com.ccmu.profiler.ui.exchange;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ExchangeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ExchangeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is organization fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}