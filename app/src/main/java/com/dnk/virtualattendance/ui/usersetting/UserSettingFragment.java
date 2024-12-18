package com.dnk.virtualattendance.ui.usersetting;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;

import com.dnk.virtualattendance.HomeActivity;
import com.dnk.virtualattendance.R;
import com.dnk.virtualattendance.database.RoleDBManager;
import com.dnk.virtualattendance.database.UserDBManager;
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
    private UserDBManager userDBManager;
    private List<UserModel> userList;
    private RoleDBManager roleDBManager;
    private List<RoleModel> roleList;
    private String newUserUid;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        UserSettingViewModel userSettingViewModel =
                new ViewModelProvider(this).get(UserSettingViewModel.class);

        binding = FragmentUserSettingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        userDBManager = new UserDBManager(this.getContext());
        userDBManager.open();
        userList = userDBManager.getAllUsers();
        Log.d("UserSettingFragment", "User list size: " + userList.size());
        userDBManager.close();


        roleDBManager = new RoleDBManager(this.getContext());
        roleDBManager.open();
        roleList = roleDBManager.getAllRoles();
        roleDBManager.close();

        final TextView textView = binding.userSettingTitleTV;
        userSettingViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        final Spinner userSettingUserSp = binding.userSettingUserSp;
        getUserSpinnerAdapter().observe(getViewLifecycleOwner(), userSettingUserSp::setAdapter);

        final Spinner userSettingRoleSp = binding.userSettingRoleSp;
        getRoleSpinnerAdapter().observe(getViewLifecycleOwner(), userSettingRoleSp::setAdapter);

        Button userSettingSubmitBtn = binding.userSettingSubmitBtn;
        userSettingSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userDBManager = new UserDBManager(view.getContext());
                userDBManager.open();

                UserModel selectedUser = (UserModel) userSettingUserSp.getSelectedItem();
                RoleModel selectedRole = (RoleModel) userSettingRoleSp.getSelectedItem();
                EditText userSettingNameET = binding.userSettingNameET;
                EditText userSettingEmailET = binding.userSettingEmailET;
                EditText userSettingPasswordET = binding.userSettingPasswordET;

                UserModel newUser = new UserModel();

                if (!selectedUser.getId().equals("-1")) {
                    newUser.setId(selectedUser.getId());
                    newUser.setName(userSettingNameET.getText().toString());
                    newUser.setRole(selectedRole.getId());
                    userDBManager.updateUser(newUser);
                } else {
                    newUser.setName(userSettingNameET.getText().toString());
                    newUser.setEmail(userSettingEmailET.getText().toString());
                    newUser.setRole(selectedRole.getId());
                    userDBManager.addUser(newUser);
                    mAuth.createUserWithEmailAndPassword(userSettingEmailET.getText().toString(), userSettingPasswordET.getText().toString())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // New user created successfully
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Log.d("CurrentUserReg", "Success creating user: " + user.getUid());
                                } else {
                                    // Error creating user
                                    Log.d("CurrentUserReg", "Error creating user: " + task.getException().getMessage());
                                }
                            });
                }

                userDBManager.close();

                // Reload Fragment
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment_content_home, new UserSettingFragment());
                transaction.commit();
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