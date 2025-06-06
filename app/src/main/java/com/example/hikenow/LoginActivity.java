package com.example.hikenow;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

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
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText emailET;
    EditText passwordET;

    MaterialButton loginBT;
    MaterialButton regBT;
    MaterialButton guestBT;
    ProgressBar progressBar;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    CheckBox rememberMe;
    boolean isRemembered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rememberMe = findViewById(R.id.rememberMe);
        emailET = findViewById(R.id.editTextEmailAddress);
        passwordET = findViewById(R.id.editTextTextPassword);
        loginBT = findViewById(R.id.loginButton);
        regBT = findViewById(R.id.gotoregButton);
        guestBT = findViewById(R.id.guestButton);
        progressBar = findViewById(R.id.loginProgressBar);
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isRemembered = sharedPreferences.getBoolean("remember", false);

        if (isRemembered){
            emailET.setText(sharedPreferences.getString("email", ""));
            passwordET.setText(sharedPreferences.getString("password", ""));
            rememberMe.setChecked(true);
        }

        loginBT.setAlpha(0f);
        loginBT.animate().alpha(1f).setDuration(1500);
        regBT.setAlpha(0f);
        regBT.animate().alpha(1f).setDuration(1500);
        guestBT.setAlpha(0f);
        guestBT.animate().alpha(1f).setDuration(1500);

        loginBT.setOnClickListener(v -> {
            progressBar.setVisibility(VISIBLE);
            String email = emailET.getText().toString().trim();
            String pwd = passwordET.getText().toString().trim();
            if (rememberMe.isChecked()){
                editor.putString("email", email);
                editor.putString("password", pwd);
                editor.putBoolean("remember", true);
                editor.apply();
            } else {
                editor.clear();
                editor.apply();
            }
            if (TextUtils.isEmpty(email)||TextUtils.isEmpty(pwd)){
                progressBar.setVisibility(INVISIBLE);
                StatusDialogFragment dialog = StatusDialogFragment.newInstance("Error", "Missing email or password");
                dialog.show(getSupportFragmentManager(), "missingCredentialsDialog");
                return;
            }
            try {
                mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(INVISIBLE);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            progressBar.setVisibility(INVISIBLE);
                            Exception e = task.getException();
                            String errorMsg = (e != null) ? e.getMessage() : "Unknown error";
                            Log.e("FirebaseLogin", "Login failed", e);
                            if (errorMsg.equals("The supplied auth credential is incorrect, malformed or has expired.")){
                                errorMsg = "Email or password is incorrect, or the user has not yet registered";
                            } else if (errorMsg.equals("The email address is badly formatted.")) {
                                errorMsg = "Invalid email format, email has to look like this: xxx@yyy.zzz";
                            }
                            StatusDialogFragment dialog = StatusDialogFragment.newInstance("Login Failed", errorMsg);
                            dialog.show(getSupportFragmentManager(), "errorDialog");
                        }
                    }
                });
            } catch (Exception e) {
                Log.e("FirebaseLogin", "Exception during login", e);
            }
        });

        regBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        guestBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(VISIBLE);
                mAuth.signInAnonymously()
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            finish();
                                        } else {
                                            progressBar.setVisibility(INVISIBLE);
                                            Exception e = task.getException();
                                            String errorMsg = (e != null) ? e.getMessage() : "Unknown error";
                                            Log.e("FirebaseLogin", "Login failed", e);
                                            StatusDialogFragment dialog = StatusDialogFragment.newInstance("Login Failed", errorMsg);
                                            dialog.show(getSupportFragmentManager(), "errorDialog");
                                        }
                                    }
                                }
                        );
            }
        });
    }

}