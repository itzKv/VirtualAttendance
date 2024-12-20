package com.dnk.virtualattendance.ui.usersetting;

import android.annotation.SuppressLint;
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

        userSettingUserSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                UserModel selectedUser = (UserModel) adapterView.getItemAtPosition(i);

                EditText userSettingNameET = binding.userSettingNameET;
                EditText userSettingEmailET = binding.userSettingEmailET;
                EditText userSettingPasswordET = binding.userSettingPasswordET;

                if (selectedUser.getId() != -1) {
                    userSettingNameET.setText(selectedUser.getName());
                    userSettingEmailET.setText(selectedUser.getEmail());
                    userSettingPasswordET.setText("");
                } else {
                    // Clear fields for new user creation
                    userSettingNameET.setText("");
                    userSettingEmailET.setText("");
                    userSettingPasswordET.setText("");
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
                dbManager = new DBManager(view.getContext());
                dbManager.open();

                UserModel selectedUser = (UserModel) userSettingUserSp.getSelectedItem();
                RoleModel selectedRole = (RoleModel) userSettingRoleSp.getSelectedItem();
                EditText userSettingNameET = binding.userSettingNameET;
                EditText userSettingEmailET = binding.userSettingEmailET;
                EditText userSettingPasswordET = binding.userSettingPasswordET;

                UserModel newUser = new UserModel();

                if (selectedUser.getId() != -1) {
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
                NavController navController = NavHostFragment.findNavController(UserSettingFragment.this);
                // Refresh this fragment (remove from back stack and navigate again)
                navController.popBackStack(R.id.nav_user_setting, true);
                navController.navigate(R.id.nav_user_setting);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public LiveData<SpinnerAdapter> getUserSpinnerAdapter(){
        UserModel initUser = new UserModel();
        initUser.setName("Choose this to add new user");
        initUser.setId(-1);

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