package com.example.smartliving.HomeControlDashBoard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartliving.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ControlActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        replaceFragment(new IndoorFragment());

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                if(item.getItemId() == R.id.nav_home) {
                    selectedFragment = new IndoorFragment();
                } else if (item.getItemId() == R.id.nav_outdoor) {
                    selectedFragment = new OutdoorFragment();
                } else if (item.getItemId() == R.id.nav_profile) {
                    selectedFragment = new ProfileFragment();
                } else if (item.getItemId() == R.id.nav_profile) {
                    selectedFragment = new ProfileFragment();
                } else if (item.getItemId() == R.id.nav_settings) {
                    selectedFragment = new SettingsFragment();
                }

                if (selectedFragment != null){
                    replaceFragment(selectedFragment);
                }

                return true;
            }
        });
    }



    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        dialog = new Dialog(ControlActivity.this);
        dialog.setContentView(R.layout.dialog_box);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        ImageView img = dialog.findViewById(R.id.dialog_box_icon);
        TextView title = dialog.findViewById(R.id.dialog_box_title);
        TextView subtitle = dialog.findViewById(R.id.dialog_box_description);
        Button ok = dialog.findViewById(R.id.dialog_box_btn_ok);
        Button cancel = dialog.findViewById(R.id.dialog_box_btn_cancel);

        img.setImageDrawable(getResources().getDrawable(R.drawable.question_mark, getApplicationContext().getTheme()));

        title.setText("EXIT");
        subtitle.setText("Are you sure to exit from the app?");

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                ControlActivity.this.finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}