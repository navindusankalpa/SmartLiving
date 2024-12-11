package com.example.smartliving;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.smartliving.FireBase.Dashboard;
import com.example.smartliving.HomeControlDashBoard.ControlActivity;
import com.example.smartliving.Login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();

        SharedPreferences shared = getApplicationContext().getSharedPreferences("checkbox", MainActivity.this.MODE_PRIVATE);
        boolean isShared = shared.getAll() != null && !shared.getAll().isEmpty();

        int SPLASH = 5000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
//                MainActivity.this.finish();

                if (isShared) {
                    Log.w("LoginPrefs", "if");
                    SharedPreferences sharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    String checkbox = sharedPreferences.getString("remember", "");
                    if (Objects.equals(checkbox, "true")) {
                        String email = sharedPreferences.getString("email", "");
                        String password = sharedPreferences.getString("password", "");

                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    SharedPreferences sharedPref = getSharedPreferences("current_user", MODE_PRIVATE);
                                    SharedPreferences.Editor prefEditor = sharedPref.edit();
                                    prefEditor.putString("email", email);
                                    prefEditor.putString("pass", password);
                                    prefEditor.apply();

                                    Intent intent = new Intent(MainActivity.this, ControlActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                                    MainActivity.this.finish();
                                }
                            }
                        });

                    } else {
                        Log.w("LoginPrefs", "checkbox");
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                        MainActivity.this.finish();
                    }
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                    MainActivity.this.finish();
                }
            }
        }, SPLASH);
    }
}