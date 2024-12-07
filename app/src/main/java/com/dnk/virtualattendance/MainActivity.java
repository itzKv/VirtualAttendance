package com.dnk.virtualattendance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText mainEmailET = findViewById(R.id.mainEmailET);
        EditText mainPasswordET = findViewById(R.id.mainPasswordET);
        Button mainLoginBtn = findViewById(R.id.mainLoginBtn);

        // Check dan Ambil Role Usernya dari input Email dan Password
        // Role = ?

        mainLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Kalau Role nya Admin Intent ke UserSetting
                // Kalau Role nya Staff Intent ke AttendanceMachine
                Intent loginIntent = new Intent(MainActivity.this, UserSetting.class);
                startActivity(loginIntent);
            }
        });


    }
}