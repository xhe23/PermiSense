package com.installedapps.com.installedapps.home;

import com.installedapps.com.installedapps.R;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerVie
    }

    public LiveData<String> getText() {
        return mText;
    }
}