package com.dnk.virtualattendance.ui.usersetting;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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

import com.dnk.virtualattendance.HomeActivity;
import com.dnk.virtualattendance.R;
import com.dnk.virtualattendance.database.DBManager;
import com.dnk.virtualattendance.databinding.FragmentUserSettingBinding;
import com.dnk.virtualattendance.model.RoleModel;
import com.dnk.virtualattendance.model.UserModel;
import com.dnk.virtualattendance.ui.rolesetting.RoleSettingFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UserSettingFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FragmentUserSettingBinding binding;
    private List<UserModel> userList;
    private DBManager dbManager;
    private List<RoleModel> roleList;
    private String newUserUid;

    private UserModel selectedUser;
    private RoleModel selectedRole;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        UserSettingViewModel userSettingViewModel =
                new ViewModelProvider(this).get(UserSettingViewModel.class);

        binding = FragmentUserSettingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
      
        dbManager = new DBManager(this.getContext());
        dbManager.open();
        userList = dbManager.getAllUsers();
        roleList = dbManager.getAllRoles();
        dbManager.close();

        final TextView textView = binding.userSettingTitleTV;
        userSettingViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        final Spinner userSettingUserSp = binding.userSettingUserSp;
        getUserSpinnerAdapter().observe(getViewLifecycleOwner(), userSettingUserSp::setAdapter);

        final Spinner userSettingRoleSp = binding.userSettingRoleSp;
        getRoleSpinnerAdapter().observe(getViewLifecycleOwner(), userSettingRoleSp::setAdapter);

        Button userSettingDeleteBtn = binding.userSettingDeleteBtn;


        userSettingUserSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedUser = (UserModel) adapterView.getItemAtPosition(i);

                EditText userSettingNameET = binding.userSettingNameET;
                EditText userSettingEmailET = binding.userSettingEmailET;
                EditText userSettingPasswordET = binding.userSettingPasswordET;

                if (!selectedUser.getId().equals("-1")) {
                    // Autofill the Name and Role
                    userSettingNameET.setText(selectedUser.getName());

                    RoleModel selectedRole = null;
                    for (RoleModel role: roleList) {
                        if (role.getId() == (selectedUser.getRole())) {
                            Log.d("SelectedUserRole", "Role: " + selectedUser.getRole());
                            Log.d("RoleList", "Role: " + role.getId());
                            selectedRole = role;
                            break;
                        }
                    }

                    if (selectedRole != null) {
                        // Set the role in the role spinner
                        ArrayAdapter<RoleModel> roleAdapter = (ArrayAdapter<RoleModel>) userSettingRoleSp.getAdapter();
                        int rolePosition = roleAdapter.getPosition(selectedRole);
                        userSettingRoleSp.setSelection(rolePosition);
                    }

                    // Empty email and password for security
                    userSettingEmailET.setText("");
                    userSettingPasswordET.setText("");
                } else {
                    // Clear fields for new user creation
                    userSettingNameET.setText("");
                    userSettingEmailET.setText("");
                    userSettingPasswordET.setText("");
                }

                // Display delete button
                if (i != 0) {  // Assuming position 0 is the default or empty state
                    userSettingDeleteBtn.setVisibility(View.VISIBLE);  // Show the delete button
                } else {
                    userSettingDeleteBtn.setVisibility(View.GONE);  // Hide the delete button
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedUser = null;
            }
        });

        userSettingRoleSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                RoleModel selectedRole = (RoleModel) adapterView.getItemAtPosition(i);

                // Optionally, attach the role if it has been selected
                if (selectedRole != null && selectedUser != null) {
                    selectedUser.setRole(selectedRole.getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedRole = null;
            }
        });

        Button userSettingSubmitBtn = binding.userSettingSubmitBtn;
        userSettingSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DetachAndAttachSameFragment")
            @Override
            public void onClick(View view) {

                UserModel selectedUser = (UserModel) userSettingUserSp.getSelectedItem();
                RoleModel selectedRole = (RoleModel) userSettingRoleSp.getSelectedItem();
                EditText userSettingNameET = binding.userSettingNameET;
                EditText userSettingEmailET = binding.userSettingEmailET;
                EditText userSettingPasswordET = binding.userSettingPasswordET;

                String name = userSettingNameET.getText().toString();
                String email = userSettingEmailET.getText().toString();
                String password = userSettingPasswordET.getText().toString();

                // Validation
                if (name.isEmpty()) {
                    Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedRole == null) {
                    Toast.makeText(getContext(), "Cannot create user if there is no role", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (email.isEmpty() || !isValidEmail(email)) {
                    Toast.makeText(getContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.isEmpty() && password.length() < 6) {
                    Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserModel newUser = new UserModel();

                dbManager = new DBManager(view.getContext());
                dbManager.open();

                if (!selectedUser.getId().equals("-1")) {
                    newUser.setId(selectedUser.getId());
                    newUser.setName(userSettingNameET.getText().toString());
                    newUser.setRole(selectedRole.getId());
                    dbManager.updateUser(newUser);
                } else {
                    newUser.setName(userSettingNameET.getText().toString());
                    newUser.setEmail(userSettingEmailET.getText().toString());
                    newUser.setRole(selectedRole.getId());
                    dbManager.addUser(newUser);
                  
                    mAuth.createUserWithEmailAndPassword(userSettingEmailET.getText().toString(), userSettingPasswordET.getText().toString())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // New user created successfully
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    assert user != null;
                                    Log.d("CurrentUserReg", "Success creating user: " + user.getUid());
                                } else {
                                    // Error creating user
                                    Log.d("CurrentUserReg", "Error creating user: " + task.getException().getMessage());
                                }
                            });
                }

                dbManager.close();
                // Reload the fragment
                reloadFragment();

            }
        });

        // Handle Delete button click
        userSettingDeleteBtn.setOnClickListener(v -> {
            showDeleteConfirmationDialog();
        });

        return root;
    }

    // Method to validate email format
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog to confirm the deletion
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Role")
                .setMessage("Are you sure you want to delete this role?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteSelectedUser();
                    dialog.dismiss();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteSelectedUser() {

        // Locally
        dbManager.open();
        dbManager.deleteUser(selectedUser);
        dbManager.close();

        Toast.makeText(getContext(), "User " +  selectedUser.getName() + " deleted", Toast.LENGTH_SHORT).show();
        reloadFragment();
    }



    private void reloadFragment() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.popBackStack(R.id.nav_user_setting, true);
        navController.navigate(R.id.nav_user_setting);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public LiveData<SpinnerAdapter> getUserSpinnerAdapter(){
        UserModel initUser = new UserModel();
        initUser.setName("Choose this to add new user");
        initUser.setId("-1");

        List<UserModel> userSpinnerList = new ArrayList<>();
        userSpinnerList.add(initUser);
        userSpinnerList.addAll(userList);

        LiveData<List<UserModel>> liveUserList = new MutableLiveData<>(userSpinnerList);

        return Transformations.map(liveUserList, users -> {
            ArrayAdapter<UserModel> adapter = new ArrayAdapter<>(
                    this.getContext(),
                    android.R.layout.simple_spinner_item,
                    users
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            return adapter;
        });
    }
    public LiveData<SpinnerAdapter> getRoleSpinnerAdapter(){
        LiveData<List<RoleModel>> liveRoleList = new MutableLiveData<>(roleList);

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