package com.dnk.virtualattendance;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Check if user is authenticated
        if (isUserAuthenticated()) {
            // Redirect to HomeActivity
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Prevent going back to the auth page
            return;
        }
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();

        EditText mainEmailET = findViewById(R.id.mainEmailET);
        EditText mainPasswordET = findViewById(R.id.mainPasswordET);
        Button mainLoginBtn = findViewById(R.id.mainLoginBtn);

        mainLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mainEmailET.getText().toString();
                String password = mainPasswordET.getText().toString();
                // Validate email and password
                if (email.isEmpty()) {
                    mainEmailET.setError("Email must be filled");
                    mainEmailET.requestFocus();
                    return;
                }

                if (!isValidEmail(email)) {
                    mainEmailET.setError("Invalid email format");
                    mainEmailET.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    mainPasswordET.setError("Password cannot be empty");
                    mainPasswordET.requestFocus();
                    return;
                }

                if (password.length() < 6) {
                    mainPasswordET.setError("Password must be at least 6 characters");
                    mainPasswordET.requestFocus();
                    return;
                }


                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    Intent loginIntent = new Intent(MainActivity.this, HomeActivity.class);
                                    startActivity(loginIntent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(MainActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivityCalling", "onResume called");

        // Check if user is authenticated
        if (isUserAuthenticated()) {
            // Redirect to HomeActivity
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Prevent returning to this activity
        }
    }


    private boolean isUserAuthenticated() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentUser != null; // User is authenticated if currentUser is not null
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}