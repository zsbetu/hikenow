package com.example.hikenow;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText nameET;
    EditText emailET;
    EditText passwordET;
    EditText repeatPasswordET;
    ProgressBar progressBar;
    MaterialButton regBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("en");
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        progressBar = findViewById(R.id.progressBar);
        nameET = findViewById(R.id.editTextName);
        emailET = findViewById(R.id.editTextEmailAddress);
        passwordET = findViewById(R.id.editTextTextPassword);
        repeatPasswordET = findViewById(R.id.editTextTextRepeatPassword);
        regBT = findViewById(R.id.regButton);

        regBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUser();
            }
        });
    }

    private void newUser() {
        progressBar.setVisibility(View.VISIBLE);
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        String r_password = repeatPasswordET.getText().toString().trim();
        String name = nameET.getText().toString().trim();
        if (!password.equals(r_password)){
            progressBar.setVisibility(View.INVISIBLE);
            StatusDialogFragment dialogFragment = StatusDialogFragment.newInstance("Passwords do not match", "The two passwords you've entered do not match. Please check and ensure that the two passwords are identical.");
            dialogFragment.show(getSupportFragmentManager(), "pwdDialog");
            return;
        }

        if (TextUtils.isEmpty(email)){
            progressBar.setVisibility(View.INVISIBLE);
            StatusDialogFragment dialogFragment = StatusDialogFragment.newInstance("Email missing", "Please enter an email address.");
            dialogFragment.show(getSupportFragmentManager(), "noEmailDialog");
            return;
        }

        if (TextUtils.isEmpty(password)){
            progressBar.setVisibility(View.INVISIBLE);
            StatusDialogFragment dialogFragment = StatusDialogFragment.newInstance("Password missing", "Please enter a password, then type it again in the \"Repeat password\" field.");
            dialogFragment.show(getSupportFragmentManager(), "noPwdDialog");
            return;
        }
        if (password.length() < 8){
            progressBar.setVisibility(View.INVISIBLE);
            StatusDialogFragment dialogFragment = StatusDialogFragment.newInstance("Password too short", "Password must be at least 8 characters long.");
            dialogFragment.show(getSupportFragmentManager(), "shortPwdDialog");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null){
                            regBT.setEnabled(true);
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            user.updateProfile(profileChangeRequest)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()){
                                            mAuth.signOut();
                                            StatusDialogFragment dialogFragment = StatusDialogFragment.newInstance("Welcome to hike.now!", "Successfully registered, now you can login with your credentials.");
                                            dialogFragment.setDialogListener(() ->{
                                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            });
                                            dialogFragment.show(getSupportFragmentManager(), "successDialog");
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    });
                        }
                    } else {
                        regBT.setEnabled(true);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegisterActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}