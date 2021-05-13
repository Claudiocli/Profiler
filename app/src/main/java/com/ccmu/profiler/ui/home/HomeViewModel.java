package com.ccmu.profiler.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ccmu.profiler.R;

public class HomeViewModel extends AndroidViewModel {

    private MutableLiveData<String> user_name;
    private MutableLiveData<String> user_surname;
    private MutableLiveData<String> user_bio;

    public HomeViewModel(Application application) {
        super(application);
        user_name = new MutableLiveData<>();
        user_surname = new MutableLiveData<>();
        user_bio = new MutableLiveData<>();

        user_name.setValue((getApplication().getString(R.string.name_placeholder)));
        user_surname.setValue((getApplication().getString(R.string.surname_placeholder)));
        user_bio.setValue((getApplication().getString(R.string.bio_placeholder)));

    }

    public LiveData<String> getUserName()   {return user_name;}
    public LiveData<String> getUserSurname()   {return user_surname;}
    public LiveData<String> getUserBio()   {return user_bio;}

    public LiveData<String>[] getUserInfo() {
        LiveData<String>[] returnValue=new LiveData[3];
        returnValue[0]=user_name;
        returnValue[1]=user_surname;
        returnValue[2]=user_bio;

        return returnValue;
    }

}