package com.dnk.virtualattendance.ui.attendancemachine;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;

import com.dnk.virtualattendance.BuildConfig;
import com.dnk.virtualattendance.R;
import com.dnk.virtualattendance.databinding.FragmentAttendanceMachineBinding;
import com.dnk.virtualattendance.databinding.FragmentRoleSettingBinding;
import com.dnk.virtualattendance.model.RoleModel;
import com.dnk.virtualattendance.ui.rolesetting.RoleSettingViewModel;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.List;

public class AttendanceMachineFragment extends Fragment {
    private FragmentAttendanceMachineBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AttendanceMachineViewModel attendanceMachineViewModel =
                new ViewModelProvider(this).get(AttendanceMachineViewModel.class);

        binding = FragmentAttendanceMachineBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FragmentChange", "Attendance Machine onCreate() called");
    }
}