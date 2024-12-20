package com.dnk.virtualattendance.ui.rolesetting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.dnk.virtualattendance.BuildConfig;
import com.dnk.virtualattendance.R;
import com.dnk.virtualattendance.database.DBManager;
import com.dnk.virtualattendance.databinding.FragmentRoleSettingBinding;
import com.dnk.virtualattendance.model.RoleModel;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;
import java.util.List;

public class RoleSettingFragment extends Fragment {
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private FragmentRoleSettingBinding binding;
    private DBManager dbManager;
    private List<RoleModel> roleList;
    private ActivityResultLauncher<Intent> autocompleteLauncher;
    private RoleModel selectedRole;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RoleSettingViewModel roleSettingViewModel =
                new ViewModelProvider(this).get(RoleSettingViewModel.class);

        binding = FragmentRoleSettingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dbManager = new DBManager(this.getContext());
        dbManager.open();
        roleList = dbManager.getAllRoles();
        dbManager.close();

        final TextView titleTV = binding.roleSettingTitleTV;
        roleSettingViewModel.getText().observe(getViewLifecycleOwner(), titleTV::setText);

        final Spinner roleSettingRoleSp = binding.roleSettingRoleSp;
        EditText roleSettingNameSp = binding.roleSettingNameSp;
        EditText roleSettingStartTimeTP = binding.roleSettingStartTimeTP;
        EditText roleSettingEndTimeTP = binding.roleSettingEndTimeTP;
        EditText roleSettingLocationET = binding.roleSettingLocationET;
        EditText roleSettingSalaryNum = binding.roleSettingSalaryNum;
        EditText roleSettingSpareTimeTP = binding.roleSettingSpareTimeTP;
        Button roleSettingDeleteBtn = binding.roleSettingDeleteBtn;

        getRoleSpinnerAdapter().observe(getViewLifecycleOwner(), roleSettingRoleSp::setAdapter);

        roleSettingRoleSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedRole = (RoleModel) adapterView.getItemAtPosition(i);

                EditText roleSettingNameSp = binding.roleSettingNameSp;
                EditText roleSettingStartTimeTP = binding.roleSettingStartTimeTP;
                EditText roleSettingEndTimeTP = binding.roleSettingEndTimeTP;
                EditText roleSettingLocationET = binding.roleSettingLocationET;
                EditText roleSettingSalaryNum = binding.roleSettingSalaryNum;
                EditText roleSettingSpareTimeTP = binding.roleSettingSpareTimeTP;

                if (selectedRole.getId() != -1) {
                    // Autofill the credentials
                    roleSettingNameSp.setText(selectedRole.getRoleName());
                    roleSettingStartTimeTP.setText(selectedRole.getWorkingStartTime());
                    roleSettingEndTimeTP.setText(selectedRole.getWorkingEndTime());
                    roleSettingLocationET.setText(selectedRole.getWorkingLocation());
                    roleSettingSalaryNum.setText(selectedRole.getSalary());
                    roleSettingSpareTimeTP.setText(selectedRole.getWorkingSpareTime());

                    // Display delete button
                    if (i != 0) {  // Assuming position 0 is the default or empty state
                        roleSettingDeleteBtn.setVisibility(View.VISIBLE);  // Show the delete button
                    } else {
                        roleSettingDeleteBtn.setVisibility(View.GONE);  // Hide the delete button
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedRole = null;
            }
        });

        roleSettingStartTimeTP.setFocusable(false); // Mencegah keyboard popup
        roleSettingStartTimeTP.setOnClickListener(v -> showTimePickerDialog(roleSettingStartTimeTP));

        roleSettingEndTimeTP.setFocusable(false); // Mencegah keyboard popup
        roleSettingEndTimeTP.setOnClickListener(v -> showTimePickerDialog(roleSettingEndTimeTP));

        roleSettingSpareTimeTP.setFocusable(false); // Mencegah keyboard popup
        roleSettingSpareTimeTP.setOnClickListener(v -> showTimePickerDialog(roleSettingSpareTimeTP));

        Button roleSettingSubmitBtn = binding.roleSettingSubmitBtn;
        roleSettingSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbManager = new DBManager(view.getContext());
                dbManager.open();

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
                    dbManager.updateRole(newRole);
                } else {
                    newRole.setRoleName(roleSettingNameSp.getText().toString());
                    newRole.setWorkingStartTime(roleSettingStartTimeTP.getText().toString());
                    newRole.setWorkingEndTime(roleSettingEndTimeTP.getText().toString());
                    newRole.setWorkingSpareTime(roleSettingSpareTimeTP.getText().toString());
                    newRole.setWorkingLocation(roleSettingLocationET.getText().toString());
                    newRole.setSalary(roleSettingSalaryNum.getText().toString());
                    dbManager.addRole(newRole);
                }

                dbManager.close();

                // Reload the fragment
                reloadFragment();
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


        // Handle Delete button click
        roleSettingDeleteBtn.setOnClickListener(v -> {
            if (selectedRole != null && selectedRole.getId() != -1) {
                showDeleteConfirmationDialog();
            }
        });

        // Existing code for other actions (Add/Update roles)


        return root;
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog to confirm the deletion
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Role")
                .setMessage("Are you sure you want to delete this role?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteRole();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteRole() {
        dbManager.open();
        // Check if any users are attached to the role
        if (dbManager.isRoleUsed(selectedRole.getId())) {
            // Notify the user that the role cannot be deleted
            Toast.makeText(requireContext(), "Cannot delete role. Users are still attached to this role.", Toast.LENGTH_SHORT).show();
        } else {
            // Delete locally
            dbManager.deleteRole(selectedRole);

            // Firebase

            Toast.makeText(requireContext(), "Role deleted successfully.", Toast.LENGTH_SHORT).show();
        }

        dbManager.close();

        // Reload the fragment
        reloadFragment();
    }

    private void reloadFragment() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.popBackStack(R.id.nav_role_setting, true);
        navController.navigate(R.id.nav_role_setting);
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

    private void showTimePickerDialog(EditText timeEditText) {
        final int[] currentHour = {12}; // Default hour
        final int[] currentMinute = {0}; // Default minute

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minute) -> {
                    // Format the time and set it to the EditText
                    String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
                    timeEditText.setText(formattedTime);
                },
                currentHour[0],
                currentMinute[0],
                true // Use 24-hour format
        );

        timePickerDialog.show();
    }

}