package com.dnk.virtualattendance.ui.rolesetting;

import android.app.Activity;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;

import com.dnk.virtualattendance.BuildConfig;
import com.dnk.virtualattendance.R;
import com.dnk.virtualattendance.database.RoleDBManager;
import com.dnk.virtualattendance.databinding.FragmentRoleSettingBinding;
import com.dnk.virtualattendance.model.RoleModel;
import com.dnk.virtualattendance.ui.usersetting.UserSettingFragment;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoleSettingFragment extends Fragment {
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private FragmentRoleSettingBinding binding;
    private RoleDBManager roleDBManager;
    private List<RoleModel> roleList;
    private ActivityResultLauncher<Intent> autocompleteLauncher;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RoleSettingViewModel roleSettingViewModel =
                new ViewModelProvider(this).get(RoleSettingViewModel.class);

        binding = FragmentRoleSettingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        roleDBManager = new RoleDBManager(this.getContext());
        roleDBManager.open();
        roleList = roleDBManager.getAllRoles();
        roleDBManager.close();

        final TextView titleTV = binding.roleSettingTitleTV;
        roleSettingViewModel.getText().observe(getViewLifecycleOwner(), titleTV::setText);

        final Spinner roleSettingRoleSp = binding.roleSettingRoleSp;
        EditText roleSettingNameSp = binding.roleSettingNameSp;
        EditText roleSettingStartTimeTP = binding.roleSettingStartTimeTP;
        EditText roleSettingEndTimeTP = binding.roleSettingEndTimeTP;
        EditText roleSettingLocationET = binding.roleSettingLocationET;
        EditText roleSettingSalaryNum = binding.roleSettingSalaryNum;
        EditText roleSettingSpareTimeTP = binding.roleSettingSpareTimeTP;

        getRoleSpinnerAdapter().observe(getViewLifecycleOwner(), roleSettingRoleSp::setAdapter);

        Button roleSettingSubmitBtn = binding.roleSettingSubmitBtn;
        roleSettingSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roleDBManager = new RoleDBManager(view.getContext());
                roleDBManager.open();

                RoleModel selectedRole = (RoleModel) roleSettingRoleSp.getSelectedItem();

                RoleModel newRole = new RoleModel();

                if (selectedRole.getId() != -1) {
                    newRole.setId(selectedRole.getId());
                    newRole.setRoleName(roleSettingNameSp.getText().toString());
                    newRole.setWorkingStartTime(roleSettingStartTimeTP.getText().toString());
                    newRole.setWorkingEndTime(roleSettingEndTimeTP.getText().toString());
                    newRole.setWorkingSpareTime(roleSettingSpareTimeTP.getText().toString());
                    newRole.setWorkingLocation(roleSettingLocationET.getText().toString());
                    newRole.setSalary(roleSettingSalaryNum.getText().toString());
                    roleDBManager.updateRole(newRole);
                } else {
                    newRole.setRoleName(roleSettingNameSp.getText().toString());
                    newRole.setWorkingStartTime(roleSettingStartTimeTP.getText().toString());
                    newRole.setWorkingEndTime(roleSettingEndTimeTP.getText().toString());
                    newRole.setWorkingSpareTime(roleSettingSpareTimeTP.getText().toString());
                    newRole.setWorkingLocation(roleSettingLocationET.getText().toString());
                    newRole.setSalary(roleSettingSalaryNum.getText().toString());
                    roleDBManager.addRole(newRole);
                }

                roleDBManager.close();

                // Reload Fragment
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment_content_home, new RoleSettingFragment());
                transaction.commit();
            }
        });

        // Inisialisasi ActivityResultLauncher
        autocompleteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Place place = Autocomplete.getPlaceFromIntent(result.getData());
                        binding.roleSettingLocationET.setText(place.getName()); // Set lokasi ke EditText
                    } else if (result.getResultCode() == AutocompleteActivity.RESULT_ERROR) {
                        Status status = Autocomplete.getStatusFromIntent(result.getData());
                        Log.e("AutocompleteError", status.getStatusMessage()); // Log error
                    }
                }
        );

        roleSettingLocationET.setFocusable(false); // Prevent keyboard popup for autocomplete
        roleSettingLocationET.setOnClickListener(v -> {
            List<Place.Field> fields = List.of(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(requireContext());
            autocompleteLauncher.launch(intent); // Gunakan launcher
        });



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

        // Initialize Places SDK
        if (!Places.isInitialized()) {
            Places.initialize(requireContext().getApplicationContext(), BuildConfig.MAPS_API_KEY);
        }
    }

    public LiveData<SpinnerAdapter> getRoleSpinnerAdapter(){
        RoleModel initRole = new RoleModel();
        initRole.setRoleName("Choose this to add new role");
        initRole.setId(-1);

        List<RoleModel> roleSpinnerList = new ArrayList<>();
        roleSpinnerList.add(initRole);
        roleSpinnerList.addAll(roleList);

        LiveData<List<RoleModel>> liveRoleList = new MutableLiveData<>(roleSpinnerList);

        return Transformations.map(liveRoleList, roles -> {
            ArrayAdapter<RoleModel> adapter = new ArrayAdapter<>(
                    this.getContext(),
                    android.R.layout.simple_spinner_item,
                    roles
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            return adapter;
        });
    }
}