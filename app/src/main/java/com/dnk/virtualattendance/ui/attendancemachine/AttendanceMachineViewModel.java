package com.dnk.virtualattendance.ui.attendancemachine;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AttendanceMachineViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AttendanceMachineViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Attendance Machine");
    }

    public LiveData<String> getText() {
        return mText;
    }
}