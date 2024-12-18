package com.dnk.virtualattendance.ui.attendancesummary;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AttendanceSummaryViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AttendanceSummaryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Attendance Summary");
    }

    public LiveData<String> getText() {
        return mText;
    }
}