package com.example.smartliving.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.smartliving.FireBase.FireBaseHandler;
import com.example.smartliving.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {
    TextInputEditText fName, lastName, bDay, phone, address;
    TextView email, serial;
    ImageView profileImage;
    Button edit;
    ProgressDialog loading;
    DatabaseReference dbRef;
    String uid, editedFName, editedLName, editedBirthday, editedPhone, editedAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        loading = new ProgressDialog(EditProfileActivity.this);
        loading.setCancelable(false);
        loading.setMessage("Please wait");
        loading.show();

        serial = findViewById(R.id.edit_profile_serial_id);
        fName = findViewById(R.id.edit_profile_first_name);
        lastName = findViewById(R.id.edit_profile_last_name);
        bDay = findViewById(R.id.edit_profile_birthday);
        phone = findViewById(R.id.edit_profile_phone);
        address = findViewById(R.id.edit_profile_address);
        edit = findViewById(R.id.btn_edit_profile);
        email = findViewById(R.id.edit_profile_email);
        profileImage = findViewById(R.id.edit_profile_image);

        uid = new FireBaseHandler().getUid();

        dbRef = FirebaseDatabase.getInstance().getReference("Customers").child(uid);
        dbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot dataSnapshot = task.getResult();
                serial.setText("Serial ID " + dataSnapshot.child("serial").getValue(String.class));
                fName.setText(dataSnapshot.child("firstName").getValue(String.class));
                lastName.setText(dataSnapshot.child("lastName").getValue(String.class));
                bDay.setText(dataSnapshot.child("birthday").getValue(String.class));
                address.setText(dataSnapshot.child("address").getValue(String.class));
                email.setText(dataSnapshot.child("email").getValue(String.class));
                phone.setText(dataSnapshot.child("phone").getValue(String.class));

                dbRef = FirebaseDatabase.getInstance().getReference("Users");
                dbRef.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        DataSnapshot dataSnapshot1 = task.getResult();
                        String img = dataSnapshot1.child("profile_img").getValue(String.class);
                        try {
                            Glide.with(EditProfileActivity.this).load(img).into(profileImage);
                        } catch (Exception e){
                            Log.e("Error", e.getMessage());
                        } finally {
                            loading.cancel();
                        }
                    }
                });
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editedAddress = address.getText().toString().trim();
                editedFName = fName.getText().toString().trim();
                editedLName = lastName.getText().toString().trim();
                editedPhone = phone.getText().toString().trim();
                editedBirthday = bDay.getText().toString().trim();

                if (!editedBirthday.isEmpty() && !editedAddress.isEmpty() && !editedFName.isEmpty() && !editedLName.isEmpty() && !editedPhone.isEmpty()){
                    String userid = new FireBaseHandler().getUid();

                    loading = new ProgressDialog(EditProfileActivity.this);
                    loading.setCancelable(false);
                    loading.setMessage("Please wait");
                    loading.show();

                    dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                    dbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            DataSnapshot dataSnapshot = task.getResult();
                            String usertype = dataSnapshot.child("user_type").getValue(String.class);

                            if (usertype.equals("customer")){
                                dbRef = FirebaseDatabase.getInstance().getReference("Customers").child(userid);
                                dbRef.child("address").setValue(editedAddress);
                                dbRef.child("firstName").setValue(editedFName);
                                dbRef.child("lastName").setValue(editedLName);
                                dbRef.child("phone").setValue(editedPhone);
                                dbRef.child("birthday").setValue(editedBirthday);
                                loading.cancel();
                                Toast.makeText(EditProfileActivity.this, "Data edited successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                dbRef = FirebaseDatabase.getInstance().getReference("FamilyUsers").child(userid);
                                dbRef.child("firstName").setValue(editedFName);
                                dbRef.child("lastName").setValue(editedLName);
                                loading.cancel();
                                Toast.makeText(EditProfileActivity.this, "Data edited successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(EditProfileActivity.this, "Please fill required fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}