package com.dnk.virtualattendance.ui.rolesetting;

import android.app.Application;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.dnk.virtualattendance.HomeActivity;
import com.dnk.virtualattendance.database.RoleDBManager;
import com.dnk.virtualattendance.model.RoleModel;

import java.util.ArrayList;
import java.util.List;

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