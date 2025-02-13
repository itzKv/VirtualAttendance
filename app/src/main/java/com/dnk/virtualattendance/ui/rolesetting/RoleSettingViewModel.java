package com.dnk.virtualattendance.ui.rolesetting;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RoleSettingViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public RoleSettingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Add/Edit Role");
    }

    public LiveData<String> getText() {
        return mText;
    }
}