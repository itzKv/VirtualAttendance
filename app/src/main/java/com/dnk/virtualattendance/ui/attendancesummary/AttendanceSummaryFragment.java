package com.dnk.virtualattendance.ui.attendancesummary;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dnk.virtualattendance.HomeActivity;
import com.dnk.virtualattendance.databinding.FragmentAttendanceSummaryBinding;
import com.dnk.virtualattendance.model.RoleModel;

public class AttendanceSummaryFragment extends Fragment {
    private FragmentAttendanceSummaryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AttendanceSummaryViewModel attendanceSummaryViewModel =
                new ViewModelProvider(this).get(AttendanceSummaryViewModel.class);

        binding = FragmentAttendanceSummaryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        HomeActivity activity = (HomeActivity) getActivity();
        if (activity != null) {
            RoleModel userRole = activity.getUserRole();
            Log.d("UserRoleFragment", "User Role: " + userRole.getRoleName());
        }

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
    }
}