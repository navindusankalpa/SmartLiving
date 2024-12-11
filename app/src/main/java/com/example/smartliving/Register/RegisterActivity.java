package com.example.smartliving.Register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartliving.FireBase.Dashboard;
import com.example.smartliving.FireBase.FireBaseHandler;
import com.example.smartliving.Login.LoginActivity;
import com.example.smartliving.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    TextView clickable;
    TextInputEditText serial, firstName, lastName, birthday, email, phone, address, password, rePass;
    Button btnRegister;
    String txtSerial, txtFirstName, txtLastName, txtBirthday, txtEmail, txtPhone, txtAddress, txtPassword, txtRePass;
    ProgressDialog loading;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference dbRef;
    Dialog dialog2;
    boolean isBirthdayVal, isPhoneVal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();

        clickable = findViewById(R.id.clickable2);
        serial = findViewById(R.id.register_serial_id);
        firstName = findViewById(R.id.register_first_name);
        lastName = findViewById(R.id.register_last_name);
        birthday = findViewById(R.id.register_birthday);
        email = findViewById(R.id.register_email);
        phone = findViewById(R.id.register_phone);
        address = findViewById(R.id.register_address);
        password = findViewById(R.id.register_password);
        rePass = findViewById(R.id.register_retype_password);
        btnRegister = findViewById(R.id.btn_register);

        SpannableString ss = new SpannableString("Already registered? Click here to login.");
        ForegroundColorSpan blueColorSpan = new ForegroundColorSpan(Color.WHITE);
        ss.setSpan(blueColorSpan, 18,22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ClickableSpan cs = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
                RegisterActivity.this.finish();
            }
        };

        ss.setSpan(cs, 26, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        clickable.setText(ss);
        clickable.setMovementMethod(LinkMovementMethod.getInstance());

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String txt = phone.getText().toString().trim();
                if (!txt.isEmpty() && txt.length() == 10){
                    isPhoneVal = true;
                    phone.setError(null);
                } else {
                    isPhoneVal = false;
                    phone.setError("Not a valid number");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        phone.addTextChangedListener(textWatcher);

        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String txt = birthday.getText().toString().trim();
                boolean isValidBD = new Dashboard().isValidBirthday(txt);
                if (isValidBD){
                    isBirthdayVal = true;
                    birthday.setError(null);
                } else {
                    isBirthdayVal = false;
                    birthday.setError("Not a valid date");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        birthday.addTextChangedListener(tw);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                txtSerial = serial.getText().toString().trim();
                txtFirstName = firstName.getText().toString().trim();
                txtLastName = lastName.getText().toString().trim();
                txtBirthday = birthday.getText().toString().trim();
                txtPhone = phone.getText().toString().trim();
                txtAddress = address.getText().toString().trim();
                txtEmail = email.getText().toString().trim();
                txtPassword = password.getText().toString().trim();
                txtRePass = rePass.getText().toString().trim();

                if (!txtSerial.isEmpty() && !txtFirstName.isEmpty() && !txtLastName.isEmpty() && !txtBirthday.isEmpty() && !txtPhone.isEmpty() && !txtAddress.isEmpty() && !txtEmail.isEmpty() && !txtPassword.isEmpty() && !txtRePass.isEmpty()){
                    if (isPhoneVal && isBirthdayVal) {
                        if (txtPassword.equals(txtRePass)) {
                            loading = new ProgressDialog(RegisterActivity.this);
                            loading.setCancelable(false);
                            loading.setMessage("Please wait");
                            loading.show();

                            new FireBaseHandler().checkSerial(txtSerial, new FireBaseHandler.checkSerialCallback() {
                                @Override
                                public void onSerialFound(boolean isAvailable, boolean isRegistered) {
                                    if (isAvailable && !isRegistered) {
                                        firebaseDatabase = FirebaseDatabase.getInstance();
                                        mAuth.createUserWithEmailAndPassword(txtEmail, txtPassword)
                                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                    @Override
                                                    public void onSuccess(AuthResult authResult) {

                                                        FireBaseHandler fireBaseHandler = new FireBaseHandler();
                                                        fireBaseHandler.writerUserData(txtSerial, txtFirstName, txtLastName, txtBirthday, txtAddress, txtEmail, txtPhone, RegisterActivity.this, new FireBaseHandler.writeUserCallback() {
                                                            @Override
                                                            public void onUserCreated(boolean result, String msg) {
                                                                if (result) {
                                                                    String UID = fireBaseHandler.getUid();
                                                                    firebaseDatabase = FirebaseDatabase.getInstance();
                                                                    dbRef = firebaseDatabase.getReference("Users").child(UID);
                                                                    dbRef.child("user_type").setValue("customer");
                                                                    dbRef.child("isRevoked").setValue(false);
                                                                    dbRef.child("serial").setValue(txtSerial);
                                                                    dbRef.child("profile_img").setValue("https://firebasestorage.googleapis.com/v0/b/smartliving-aaa3a.appspot.com/o/user.png?alt=media&token=10de0b9f-1fb7-46dd-bfca-ae1546388f92");


                                                                    dbRef = firebaseDatabase.getReference("Serials").child(txtSerial).child("isRegistered");
                                                                    dbRef.setValue(true);


                                                                    dialog2 = new Dialog(RegisterActivity.this);
                                                                    dialog2.setContentView(R.layout.dialog_box);
                                                                    dialog2.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_background));
                                                                    dialog2.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                                    dialog2.setCancelable(false);

                                                                    ImageView img = dialog2.findViewById(R.id.dialog_box_icon);
                                                                    TextView title = dialog2.findViewById(R.id.dialog_box_title);
                                                                    TextView subtitle = dialog2.findViewById(R.id.dialog_box_description);
                                                                    Button ok = dialog2.findViewById(R.id.dialog_box_btn_ok);
                                                                    Button cancel = dialog2.findViewById(R.id.dialog_box_btn_cancel);

                                                                    //img.setImageResource(R.drawable.exclamation);
                                                                    img.setImageDrawable(getResources().getDrawable(R.drawable.checked, getApplicationContext().getTheme()));

                                                                    title.setText("SUCCESSFUL");

                                                                    subtitle.setText(Html.fromHtml("<p> User <b>" + txtEmail + "</b> created successfully. </p>"));

                                                                    ok.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            serial.setText("");
                                                                            serial.setText("");
                                                                            firstName.setText("");
                                                                            lastName.setText("");
                                                                            birthday.setText("");
                                                                            birthday.setError(null);
                                                                            email.setText("");
                                                                            phone.setText("");
                                                                            phone.setError(null);
                                                                            address.setText("");
                                                                            password.setText("");
                                                                            rePass.setText("");
                                                                            dialog2.dismiss();
                                                                        }
                                                                    });

                                                                    cancel.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            dialog2.dismiss();
                                                                        }
                                                                    });
                                                                    loading.cancel();
                                                                    dialog2.show();
                                                                } else {
                                                                    String[] errorMsg = msg.split(": ");

                                                                    dialog2 = new Dialog(RegisterActivity.this);
                                                                    dialog2.setContentView(R.layout.dialog_box);
                                                                    dialog2.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_background));
                                                                    dialog2.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                                    dialog2.setCancelable(false);

                                                                    //dialog2.getWindow().getAttributes().windowAnimations = R.style.Animation;

                                                                    ImageView img = dialog2.findViewById(R.id.dialog_box_icon);
                                                                    TextView title = dialog2.findViewById(R.id.dialog_box_title);
                                                                    TextView subtitle = dialog2.findViewById(R.id.dialog_box_description);
                                                                    Button ok = dialog2.findViewById(R.id.dialog_box_btn_ok);
                                                                    Button cancel = dialog2.findViewById(R.id.dialog_box_btn_cancel);

                                                                    //img.setImageResource(R.drawable.exclamation);
                                                                    img.setImageDrawable(getResources().getDrawable(R.drawable.exclamation, getApplicationContext().getTheme()));

                                                                    title.setText("ERROR");

                                                                    subtitle.setText(errorMsg[1]);

                                                                    ok.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            dialog2.dismiss();
                                                                        }
                                                                    });

                                                                    cancel.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            dialog2.dismiss();
                                                                        }
                                                                    });
                                                                    loading.cancel();
                                                                    dialog2.show();
                                                                }

                                                            }
                                                        });
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        //String[] errorMsg = e.getMessage().split(": ");
                                                        loading.cancel();

                                                        dialog2 = new Dialog(RegisterActivity.this);
                                                        dialog2.setContentView(R.layout.dialog_box);
                                                        dialog2.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_background));
                                                        dialog2.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                        dialog2.setCancelable(false);

                                                        //dialog2.getWindow().getAttributes().windowAnimations = R.style.Animation;

                                                        ImageView img = dialog2.findViewById(R.id.dialog_box_icon);
                                                        TextView title = dialog2.findViewById(R.id.dialog_box_title);
                                                        TextView subtitle = dialog2.findViewById(R.id.dialog_box_description);
                                                        Button ok = dialog2.findViewById(R.id.dialog_box_btn_ok);
                                                        Button cancel = dialog2.findViewById(R.id.dialog_box_btn_cancel);

                                                        //img.setImageResource(R.drawable.exclamation);
                                                        img.setImageDrawable(getResources().getDrawable(R.drawable.multiply, getApplicationContext().getTheme()));

                                                        title.setText("ERROR");

                                                        subtitle.setText(e.getMessage());

                                                        ok.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                dialog2.dismiss();
                                                            }
                                                        });

                                                        cancel.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                dialog2.dismiss();
                                                            }
                                                        });
                                                        dialog2.show();
                                                    }
                                                });
                                    } else if (!isAvailable && isRegistered) {

                                        dialog2 = new Dialog(RegisterActivity.this);
                                        dialog2.setContentView(R.layout.dialog_box);
                                        dialog2.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_background));
                                        dialog2.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        dialog2.setCancelable(false);

                                        //dialog2.getWindow().getAttributes().windowAnimations = R.style.Animation;

                                        ImageView img = dialog2.findViewById(R.id.dialog_box_icon);
                                        TextView title = dialog2.findViewById(R.id.dialog_box_title);
                                        TextView subtitle = dialog2.findViewById(R.id.dialog_box_description);
                                        Button ok = dialog2.findViewById(R.id.dialog_box_btn_ok);
                                        Button cancel = dialog2.findViewById(R.id.dialog_box_btn_cancel);

                                        //img.setImageResource(R.drawable.exclamation);
                                        img.setImageDrawable(getResources().getDrawable(R.drawable.exclamation, getApplicationContext().getTheme()));

                                        title.setText("ERROR");

                                        subtitle.setText("The provided serial ID is invalid. Please try with a different one.");

                                        ok.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog2.dismiss();
                                            }
                                        });

                                        cancel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog2.dismiss();
                                            }
                                        });
                                        loading.cancel();
                                        dialog2.show();
                                    } else if (isAvailable && isRegistered) {

                                        dialog2 = new Dialog(RegisterActivity.this);
                                        dialog2.setContentView(R.layout.dialog_box);
                                        dialog2.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_background));
                                        dialog2.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        dialog2.setCancelable(false);

                                        //dialog2.getWindow().getAttributes().windowAnimations = R.style.Animation;

                                        ImageView img = dialog2.findViewById(R.id.dialog_box_icon);
                                        TextView title = dialog2.findViewById(R.id.dialog_box_title);
                                        TextView subtitle = dialog2.findViewById(R.id.dialog_box_description);
                                        Button ok = dialog2.findViewById(R.id.dialog_box_btn_ok);
                                        Button cancel = dialog2.findViewById(R.id.dialog_box_btn_cancel);

                                        //img.setImageResource(R.drawable.exclamation);
                                        img.setImageDrawable(getResources().getDrawable(R.drawable.exclamation, getApplicationContext().getTheme()));

                                        title.setText("ERROR");

                                        subtitle.setText("The provided serial ID is already used by another user. Please try with a different one.");

                                        ok.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog2.dismiss();
                                            }
                                        });

                                        cancel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog2.dismiss();
                                            }
                                        });
                                        loading.cancel();
                                        dialog2.show();
                                    }
                                }
                            });

//                        firebaseDatabase = FirebaseDatabase.getInstance();

//                        mAuth.createUserWithEmailAndPassword(txtEmail, txtPassword)
//                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                                    @Override
//                                    public void onSuccess(AuthResult authResult) {
//
//                                        FireBaseHandler fireBaseHandler = new FireBaseHandler();
//                                        fireBaseHandler.writerUserData(txtSerial, txtFirstName, txtLastName, txtBirthday, txtAddress, txtEmail, RegisterActivity.this, new FireBaseHandler.writeUserCallback() {
//                                            @Override
//                                            public void onUserCreated(boolean result, String msg) {
//                                                if (result){
//                                                    String UID = fireBaseHandler.getUid();
//                                                    firebaseDatabase = FirebaseDatabase.getInstance();
//                                                    dbRef = firebaseDatabase.getReference("Users").child(UID);
//                                                    dbRef.child("user_type").setValue("customer");
//                                                    dbRef.child("profile_img").setValue("https://firebasestorage.googleapis.com/v0/b/smartliving-aaa3a.appspot.com/o/user.png?alt=media&token=10de0b9f-1fb7-46dd-bfca-ae1546388f92");
//                                                    dialog2 = new Dialog(RegisterActivity.this);
//                                                    dialog2.setContentView(R.layout.dialog_box);
//                                                    dialog2.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_background));
//                                                    dialog2.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                                                    dialog2.setCancelable(false);
//
//                                                    ImageView img = dialog2.findViewById(R.id.dialog_box_icon);
//                                                    TextView title = dialog2.findViewById(R.id.dialog_box_title);
//                                                    TextView subtitle = dialog2.findViewById(R.id.dialog_box_description);
//                                                    Button ok = dialog2.findViewById(R.id.dialog_box_btn_ok);
//                                                    Button cancel = dialog2.findViewById(R.id.dialog_box_btn_cancel);
//
//                                                    //img.setImageResource(R.drawable.exclamation);
//                                                    img.setImageDrawable(getResources().getDrawable(R.drawable.checked, getApplicationContext().getTheme()));
//
//                                                    title.setText("SUCCESSFUL");
//
//                                                    subtitle.setText(Html.fromHtml("<p> User <b>" + txtEmail + "</b> created successfully. </p>"));
//
//                                                    ok.setOnClickListener(new View.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(View view) {
//                                                            dialog2.dismiss();
//                                                        }
//                                                    });
//
//                                                    cancel.setOnClickListener(new View.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(View view) {
//                                                            dialog2.dismiss();
//                                                        }
//                                                    });
//                                                    loading.cancel();
//                                                    dialog2.show();
//                                                } else{
//                                                    String[] errorMsg = msg.split(": ");
//
//                                                    dialog2 = new Dialog(RegisterActivity.this);
//                                                    dialog2.setContentView(R.layout.dialog_box);
//                                                    dialog2.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_background));
//                                                    dialog2.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                                                    dialog2.setCancelable(false);
//
//                                                    //dialog2.getWindow().getAttributes().windowAnimations = R.style.Animation;
//
//                                                    ImageView img = dialog2.findViewById(R.id.dialog_box_icon);
//                                                    TextView title = dialog2.findViewById(R.id.dialog_box_title);
//                                                    TextView subtitle = dialog2.findViewById(R.id.dialog_box_description);
//                                                    Button ok = dialog2.findViewById(R.id.dialog_box_btn_ok);
//                                                    Button cancel = dialog2.findViewById(R.id.dialog_box_btn_cancel);
//
//                                                    //img.setImageResource(R.drawable.exclamation);
//                                                    img.setImageDrawable(getResources().getDrawable(R.drawable.exclamation, getApplicationContext().getTheme()));
//
//                                                    title.setText("ERROR");
//
//                                                    subtitle.setText(errorMsg[1]);
//
//                                                    ok.setOnClickListener(new View.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(View view) {
//                                                            dialog2.dismiss();
//                                                        }
//                                                    });
//
//                                                    cancel.setOnClickListener(new View.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(View view) {
//                                                            dialog2.dismiss();
//                                                        }
//                                                    });
//                                                    loading.cancel();
//                                                    dialog2.show();
//                                                }
//                                            }
//                                        });
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        //String[] errorMsg = e.getMessage().split(": ");
//                                        loading.cancel();
//
//                                        dialog2 = new Dialog(RegisterActivity.this);
//                                        dialog2.setContentView(R.layout.dialog_box);
//                                        dialog2.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_background));
//                                        dialog2.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                                        dialog2.setCancelable(false);
//
//                                        //dialog2.getWindow().getAttributes().windowAnimations = R.style.Animation;
//
//                                        ImageView img = dialog2.findViewById(R.id.dialog_box_icon);
//                                        TextView title = dialog2.findViewById(R.id.dialog_box_title);
//                                        TextView subtitle = dialog2.findViewById(R.id.dialog_box_description);
//                                        Button ok = dialog2.findViewById(R.id.dialog_box_btn_ok);
//                                        Button cancel = dialog2.findViewById(R.id.dialog_box_btn_cancel);
//
//                                        //img.setImageResource(R.drawable.exclamation);
//                                        img.setImageDrawable(getResources().getDrawable(R.drawable.checked, getApplicationContext().getTheme()));
//
//                                        title.setText("ERROR");
//
//                                        subtitle.setText(e.getMessage());
//
//                                        ok.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View view) {
//                                                dialog2.dismiss();
//                                            }
//                                        });
//
//                                        cancel.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View view) {
//                                                dialog2.dismiss();
//                                            }
//                                        });
//                                        dialog2.show();
//                                    }
//                                });
                        } else {
                            Toast.makeText(RegisterActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Invalid phone or birthday", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Please fill required fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        RegisterActivity.this.finish();
    }
}