package com.example.smartliving.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartliving.FireBase.Dashboard;
import com.example.smartliving.FireBase.FireBaseHandler;
import com.example.smartliving.HomeControlDashBoard.ControlActivity;
import com.example.smartliving.Profile.AddFamilyUserActivity;
import com.example.smartliving.R;
import com.example.smartliving.Register.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextView clickable;
    TextInputEditText email, password;
    CheckBox chkBox;
    Button btnForgot, btnLogin;
    String txtEmail, txtPassword;
    ProgressDialog loading, loadingBar2;
    Dialog dialog, dialog2;
    private FirebaseAuth mAuth;

    void showProgressBar(){
        loadingBar2 = new ProgressDialog(LoginActivity.this);
        loadingBar2.setMessage("Sending email...");
        loadingBar2.setCancelable(false);
        loadingBar2.show();
    }

    void cancelProgressBar(){
        loadingBar2.cancel();
    }

    @Override
    public void onBackPressed() {
        dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.dialog_box);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        dialog.getWindow().getAttributes().windowAnimations = R.style.Animation;

        ImageView img = dialog.findViewById(R.id.dialog_box_icon);
        TextView title = dialog.findViewById(R.id.dialog_box_title);
        TextView subtitle = dialog.findViewById(R.id.dialog_box_description);
        Button ok = dialog.findViewById(R.id.dialog_box_btn_ok);
        Button cancel = dialog.findViewById(R.id.dialog_box_btn_cancel);

        //img.setImageResource(R.drawable.exclamation);
        img.setImageDrawable(getResources().getDrawable(R.drawable.question_mark, getApplicationContext().getTheme()));

        title.setText("EXIT");
        subtitle.setText("Are you sure to exit from the app?");

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                LoginActivity.this.finish();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();

        clickable = findViewById(R.id.clickable);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        chkBox = findViewById(R.id.chk_remember);
        btnForgot = findViewById(R.id.forgot_psw);
        btnLogin = findViewById(R.id.btn_login);

        SpannableString ss = new SpannableString("No account? Click here to register.");
        ForegroundColorSpan blueColorSpan = new ForegroundColorSpan(Color.WHITE);
        ss.setSpan(blueColorSpan, 18,22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ClickableSpan cs = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
                LoginActivity.this.finish();
            }
        };

        ss.setSpan(cs, 18, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        clickable.setText(ss);
        clickable.setMovementMethod(LinkMovementMethod.getInstance());

        btnForgot.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                dialog = new Dialog(LoginActivity.this);
                dialog.setContentView(R.layout.activity_recover_password);
                dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_background));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);

                //dialog.getWindow().getAttributes().windowAnimations = R.style.Animation;

                TextInputEditText email = dialog.findViewById(R.id.recover_email_address);
                Button ok = dialog.findViewById(R.id.btn_recover);
                Button cancel = dialog.findViewById(R.id.button_cancel_recover);

                email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ok.setHint("");
                    }
                });

                ok.setEnabled(false);

                TextWatcher textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String txt = email.getText().toString().trim();
                        if (!txt.isEmpty() && txt.contains("@") && txt.contains(".com")){
                            int green = getResources().getColor(R.color.lime_green);
                            ok.setBackgroundColor(green);
                            ok.setEnabled(true);
                            email.setError(null);
                        } else {
                            int grayColor = getResources().getColor(R.color.gray);
                            int whiteColor = getResources().getColor(R.color.white);
                            ok.setBackgroundColor(grayColor);
                            ok.setTextColor(whiteColor);
                            ok.setEnabled(false);
                            email.setError("Not a valid mail");
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                };

                email.addTextChangedListener(textWatcher);

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showProgressBar();
                        String txtRecoverMail = email.getText().toString();
                        mAuth.sendPasswordResetEmail(txtRecoverMail)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {

                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            cancelProgressBar();
                                            Toast.makeText(LoginActivity.this, "Verification email sent, check your inbox", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                        } else {
                                            String[] err = Objects.requireNonNull(task.getException()).toString().split(": ");
                                            cancelProgressBar();
                                            dialog.dismiss();

                                            dialog = new Dialog(LoginActivity.this );
                                            dialog.setContentView(R.layout.dialog_box);
                                            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_background));
                                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                            dialog.setCancelable(false);

                                            //dialog.getWindow().getAttributes().windowAnimations = R.style.Animation;

                                            ImageView img = dialog.findViewById(R.id.dialog_box_icon);
                                            TextView title = dialog.findViewById(R.id.dialog_box_title);
                                            TextView subtitle = dialog.findViewById(R.id.dialog_box_description);
                                            Button ok = dialog.findViewById(R.id.dialog_box_btn_ok);
                                            Button cancel = dialog.findViewById(R.id.dialog_box_btn_cancel);

                                            //img.setImageResource(R.drawable.exclamation);
                                            img.setImageDrawable(getResources().getDrawable(R.drawable.exclamation, getApplicationContext().getTheme()));

                                            title.setText("ERROR");
                                            subtitle.setText(err[1]);

                                            ok.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    dialog.dismiss();
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
                                });

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
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtEmail = Objects.requireNonNull(email.getText()).toString();
                txtPassword = Objects.requireNonNull(password.getText()).toString();

                if(!txtEmail.isEmpty() && !txtPassword.isEmpty()) {
                    loading = new ProgressDialog(LoginActivity.this);
                    loading.setCancelable(false);
                    loading.setMessage("Please Wait");
                    loading.show();

                    mAuth.signInWithEmailAndPassword(txtEmail, txtPassword)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    String UID = new FireBaseHandler().getUid();

                                    new FireBaseHandler().chkUserRevoked(UID, new FireBaseHandler.userRevoked() {
                                        @Override
                                        public void onUserRevoked(boolean isRev) {
                                            if (isRev){
                                                loading.cancel();
                                                mAuth.signOut();
                                                dialog2 = new Dialog(LoginActivity.this);
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
                                                img.setImageDrawable(getResources().getDrawable(R.drawable.exclamation, getApplicationContext().getTheme()));

                                                title.setText("Account Revoked");

                                                subtitle.setText("This account is revoked by your admin.");

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
                                            else{
                                                String gEmail = new FireBaseHandler().healthyEmail(txtEmail);
                                                new FireBaseHandler().getUserTypeByUID(UID, gEmail, (isSuccess, usertype) -> {
                                                    if (isSuccess) {
                                                        if (usertype.equals("family_user")) {
                                                            String e = new FireBaseHandler().healthyEmail(txtEmail);
                                                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");
                                                            dbRef.child(e).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (Boolean.FALSE.equals(snapshot.child("is_db_updated").getValue(Boolean.class))) {
                                                                        dbRef.child(e).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                Object userData = snapshot.getValue();
                                                                                snapshot.getRef().removeValue();
                                                                                dbRef.child(UID).setValue(userData);

                                                                                DatabaseReference dbRef1 = FirebaseDatabase.getInstance().getReference("FamilyUsers");
                                                                                dbRef1.child(e).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                                                        Object userData1 = snapshot1.getValue();
                                                                                        snapshot.getRef().removeValue();
                                                                                        dbRef1.child(UID).setValue(userData1);
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                                    }
                                                                                });

                                                                                dbRef.child(UID).child("is_db_updated").setValue(true);

                                                                                if (chkBox.isChecked()){
                                                                                    new Dashboard().handleRememberMe(true, txtEmail, txtPassword, LoginActivity.this);
                                                                                }

                                                                                loading.cancel();
                                                                                Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                                                                                Intent intent = new Intent(LoginActivity.this, ControlActivity.class);
                                                                                startActivity(intent);
                                                                                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                                                                                LoginActivity.this.finish();
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });
                                                                    } else {
                                                                        if (chkBox.isChecked()){
                                                                            new Dashboard().handleRememberMe(true, txtEmail, txtPassword, LoginActivity.this);
                                                                        }

                                                                        loading.cancel();
                                                                        Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                                                                        Intent intent = new Intent(LoginActivity.this, ControlActivity.class);
                                                                        startActivity(intent);
                                                                        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                                                                        LoginActivity.this.finish();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });
                                                        } else if (usertype.equals("customer")) {
                                                            SharedPreferences sharedPref = getSharedPreferences("current_user", MODE_PRIVATE);
                                                            SharedPreferences.Editor prefEditor = sharedPref.edit();
                                                            prefEditor.putString("email", txtEmail);
                                                            prefEditor.putString("pass", txtPassword);
                                                            prefEditor.apply();

                                                            if (chkBox.isChecked()){
                                                                new Dashboard().handleRememberMe(true, txtEmail, txtPassword, LoginActivity.this);
                                                            }

                                                            loading.cancel();
                                                            Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(LoginActivity.this, ControlActivity.class);
                                                            startActivity(intent);
                                                            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                                                            LoginActivity.this.finish();
                                                        }
                                                    }
                                                });
//                                                if(chkBox.isChecked()){
//
//                                                    SharedPreferences sharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE);
//                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
//
//                                                    editor.putString("remember", "true");
//                                                    editor.putString("email", txtEmail);
//                                                    editor.putString("password", txtPassword);
//                                                    editor.apply();
//                                                } else{
//                                                    SharedPreferences sharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE);
//                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                                                    editor.putString("remember", "false");
//                                                    editor.apply();
//                                                }


//                                                loading.cancel();
//                                                Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
//                                                Intent intent = new Intent(LoginActivity.this, ControlActivity.class);
//                                                startActivity(intent);
//                                                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
//                                                LoginActivity.this.finish();
                                            }
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loading.cancel();
                                    dialog2 = new Dialog(LoginActivity.this);
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
                                    img.setImageDrawable(getResources().getDrawable(R.drawable.exclamation, getApplicationContext().getTheme()));

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
                } else {
                    Toast.makeText(LoginActivity.this, "Please fill required fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}