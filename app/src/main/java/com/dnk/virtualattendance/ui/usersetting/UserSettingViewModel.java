package com.dnk.virtualattendance.ui.usersetting;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserSettingViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public UserSettingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Add/Edit User");
    }

    public LiveData<String> getText() {
        return mText;
    }
}