package com.installedapps.com.installedapps.appgroup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AppgroupsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AppgroupsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}