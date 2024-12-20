package com.dnk.virtualattendance;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.dnk.virtualattendance.database.DBManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.dnk.virtualattendance.databinding.ActivityHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;

    private TextView userNameTV;
    private TextView userEmailTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarHome.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);

        // Access the header view
        View headerView = navigationView.getHeaderView(0);

        Log.d("Binding", "Binding is: " + binding);

        userNameTV = headerView.findViewById(R.id.userNameTV);
        userEmailTV = headerView.findViewById(R.id.userEmailTV);

        userNameTV.setText(getCurrentUserName());
        userEmailTV.setText(getCurrentUserEmail());

        // Retrieve authenticated user's role
        String userRole = getAuthUserRole();

        if ("Admin".equals(userRole)) {
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_role_setting, R.id.nav_user_setting)
                    .setOpenableLayout(drawer)
                    .build();

            navController.navigate(R.id.nav_role_setting);
        } else {
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_attendance_machine, R.id.nav_attendance_summary)
                    .setOpenableLayout(drawer)
                    .build();

            navController.navigate(R.id.nav_attendance_machine);
        }

        updateNavigationMenu(userRole, navigationView);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        drawer.closeDrawer(GravityCompat.START);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_setting, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            logoutUser();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    private String getAuthUserRole() {
        // Get the email of the currently authenticated user
        String email = getCurrentUserEmail();
        Log.d("UserRole", "Email: " + email); // Log the retrieved email
        if (email != null) {
            // Query SQLite database to find the user's role
            DBManager dbManager = new DBManager(this);
            dbManager.open();
            String role = dbManager.getRoleNameByEmail(email);
            dbManager.close();
            Log.d("UserRole", "Role: " + role); // Log the retrieved role)
            return role; // Return the retrieved role
        }

        return null; // Return null if email is not available
    }

    private String getCurrentUserName() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getDisplayName();
        }
        return "User";
    }
    private String getCurrentUserEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getEmail(); // Return the email of the authenticated user
        }
        return null; // Return null if no user is authenticated
    }
    private void updateNavigationMenu(String role, NavigationView navigationView) {
        Menu menu = navigationView.getMenu();

        // Clear the existing menu
        menu.clear();

        if ("Admin".equals(role)) {
            // Menu untuk Admin
            menu.add(Menu.NONE, R.id.nav_role_setting, Menu.NONE, R.string.menu_role_setting)
                    .setIcon(R.drawable.ic_menu_camera);
            menu.add(Menu.NONE, R.id.nav_user_setting, Menu.NONE, R.string.menu_user_setting)
                    .setIcon(R.drawable.ic_menu_gallery);
        } else {
            // Menu untuk non-Admin
            menu.add(Menu.NONE, R.id.nav_attendance_machine, Menu.NONE, R.string.menu_attendance_machine)
                    .setIcon(R.drawable.ic_menu_camera);
            menu.add(Menu.NONE, R.id.nav_attendance_summary, Menu.NONE, R.string.menu_attendance_summary)
                    .setIcon(R.drawable.ic_menu_gallery);
        }

        // Refresh the menu
        navigationView.invalidate();
    }
    private void logoutUser() {
        // Sign out the user from Firebase
        FirebaseAuth.getInstance().signOut();

        // Redirect the user to the login screen or main screen
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();  // Finish the current activity so the user can't go back to it
    }


}