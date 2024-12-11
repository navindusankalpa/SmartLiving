package com.example.smartliving.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartliving.FireBase.Dashboard;
import com.example.smartliving.FireBase.FireBaseHandler;
import com.example.smartliving.HomeControlDashBoard.SettingsFragment;
import com.example.smartliving.Login.LoginActivity;
import com.example.smartliving.R;
import com.google.android.material.textfield.TextInputEditText;

public class AddFamilyUserActivity extends AppCompatActivity {
    private TextInputEditText fName, lName, email, password, rePass;
    private String txtEmail, txtFName, txtLName, txtPass, txtRepass;
    private Button btnRegister;
    private Dialog dialog;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_family_user);

        fName = findViewById(R.id.family_first_name);
        lName = findViewById(R.id.family_last_name);
        email = findViewById(R.id.family_email);
        password = findViewById(R.id.family_password);
        rePass = findViewById(R.id.family_retype_password);
        btnRegister = findViewById(R.id.btn_family_register);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading = new ProgressDialog(AddFamilyUserActivity.this);
                loading.setMessage("Please wait");
                loading.setCancelable(false);
                loading.show();

                txtEmail = email.getText().toString().trim();
                txtFName = fName.getText().toString().trim();
                txtLName = lName.getText().toString().trim();
                txtPass = password.getText().toString().trim();
                txtRepass = rePass.getText().toString().trim();

                if (!txtEmail.isEmpty() && !txtFName.isEmpty() && !txtLName.isEmpty() && !txtPass.isEmpty() && !txtRepass.isEmpty()){
                    String UID = new FireBaseHandler().getUid();
                    FireBaseHandler fireBaseHandler = new FireBaseHandler();

                    new Dashboard().getSerial(UID, new Dashboard.getSerialInterface() {
                        @Override
                        public void onSerialFetched(String serial) {
                            if (serial != null) {
                                fireBaseHandler.createFamilyUser(txtFName, txtLName, txtEmail, txtPass, UID, serial, new FireBaseHandler.createFamilyUserCallback() {
                                    @Override
                                    public void onFamilyUserCreated(boolean isSuccessful, String msg) {
                                        if (isSuccessful) {

                                            fireBaseHandler.restoreUser(getApplicationContext(), new FireBaseHandler.RestoreUser() {
                                                @Override
                                                public void onUserRestored(boolean state) {
                                                    if (state) {
                                                        dialog = new Dialog(AddFamilyUserActivity.this);
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
                                                        img.setImageDrawable(getResources().getDrawable(R.drawable.checked, getApplicationContext().getTheme()));

                                                        title.setText("SUCCESSFUL");
                                                        subtitle.setText(Html.fromHtml("<p> User <b>" + txtEmail + "</b> created successfully. </p>"));

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
                                                        loading.cancel();
                                                        dialog.show();
                                                    } else {
                                                        dialog = new Dialog(AddFamilyUserActivity.this);
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
                                                        subtitle.setText("An error occurred. Please contact support. SmartLiving will be closed.\nError reason: User restore unsuccessful.");

                                                        ok.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                dialog.dismiss();
                                                                finish();
                                                            }
                                                        });

                                                        cancel.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                dialog.dismiss();
                                                                finish();
                                                            }
                                                        });
                                                        loading.cancel();
                                                        dialog.show();
                                                    }
                                                }
                                            });

//                                dialog = new Dialog(AddFamilyUserActivity.this );
//                                dialog.setContentView(R.layout.dialog_box);
//                                dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_background));
//                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                                dialog.setCancelable(false);
//
//                                //dialog.getWindow().getAttributes().windowAnimations = R.style.Animation;
//
//                                ImageView img = dialog.findViewById(R.id.dialog_box_icon);
//                                TextView title = dialog.findViewById(R.id.dialog_box_title);
//                                TextView subtitle = dialog.findViewById(R.id.dialog_box_description);
//                                Button ok = dialog.findViewById(R.id.dialog_box_btn_ok);
//                                Button cancel = dialog.findViewById(R.id.dialog_box_btn_cancel);
//
//                                //img.setImageResource(R.drawable.exclamation);
//                                img.setImageDrawable(getResources().getDrawable(R.drawable.checked, getApplicationContext().getTheme()));
//
//                                title.setText("SUCCESSFUL");
//                                subtitle.setText(Html.fromHtml("<p> User <b>" + txtEmail + "</b> created successfully. </p>"));
//
//                                ok.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        dialog.dismiss();
//                                    }
//                                });
//
//                                cancel.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        dialog.dismiss();
//                                    }
//                                });
//                                loading.cancel();
//                                dialog.show();
                                        } else {
                                            dialog = new Dialog(AddFamilyUserActivity.this);
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
                                            subtitle.setText(msg);

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
                                            loading.cancel();
                                            dialog.show();
                                        }
                                    }

                                    @Override
                                    public void onEmailCrated(boolean isCreated, String errMsg) {
                                        dialog = new Dialog(AddFamilyUserActivity.this);
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
                                        subtitle.setText(errMsg);

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
                                        loading.cancel();
                                        dialog.show();
                                    }
                                });
                            }
                        }
                    });

//                    fireBaseHandler.createFamilyUser(txtFName, txtLName, txtEmail, txtPass, UID, serial, new FireBaseHandler.createFamilyUserCallback() {
//                        @Override
//                        public void onFamilyUserCreated(boolean isSuccessful, String msg) {
//                            if (isSuccessful){
//
//                                fireBaseHandler.restoreUser(getApplicationContext(), new FireBaseHandler.RestoreUser() {
//                                    @Override
//                                    public void onUserRestored(boolean state) {
//                                        if (state){
//                                            dialog = new Dialog(AddFamilyUserActivity.this );
//                                            dialog.setContentView(R.layout.dialog_box);
//                                            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_background));
//                                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                                            dialog.setCancelable(false);
//
//                                            //dialog.getWindow().getAttributes().windowAnimations = R.style.Animation;
//
//                                            ImageView img = dialog.findViewById(R.id.dialog_box_icon);
//                                            TextView title = dialog.findViewById(R.id.dialog_box_title);
//                                            TextView subtitle = dialog.findViewById(R.id.dialog_box_description);
//                                            Button ok = dialog.findViewById(R.id.dialog_box_btn_ok);
//                                            Button cancel = dialog.findViewById(R.id.dialog_box_btn_cancel);
//
//                                            //img.setImageResource(R.drawable.exclamation);
//                                            img.setImageDrawable(getResources().getDrawable(R.drawable.checked, getApplicationContext().getTheme()));
//
//                                            title.setText("SUCCESSFUL");
//                                            subtitle.setText(Html.fromHtml("<p> User <b>" + txtEmail + "</b> created successfully. </p>"));
//
//                                            ok.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View view) {
//                                                    dialog.dismiss();
//                                                }
//                                            });
//
//                                            cancel.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View view) {
//                                                    dialog.dismiss();
//                                                }
//                                            });
//                                            loading.cancel();
//                                            dialog.show();
//                                        } else{
//                                            dialog = new Dialog(AddFamilyUserActivity.this );
//                                            dialog.setContentView(R.layout.dialog_box);
//                                            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_background));
//                                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                                            dialog.setCancelable(false);
//
//                                            //dialog.getWindow().getAttributes().windowAnimations = R.style.Animation;
//
//                                            ImageView img = dialog.findViewById(R.id.dialog_box_icon);
//                                            TextView title = dialog.findViewById(R.id.dialog_box_title);
//                                            TextView subtitle = dialog.findViewById(R.id.dialog_box_description);
//                                            Button ok = dialog.findViewById(R.id.dialog_box_btn_ok);
//                                            Button cancel = dialog.findViewById(R.id.dialog_box_btn_cancel);
//
//                                            //img.setImageResource(R.drawable.exclamation);
//                                            img.setImageDrawable(getResources().getDrawable(R.drawable.exclamation, getApplicationContext().getTheme()));
//
//                                            title.setText("ERROR");
//                                            subtitle.setText("An error occurred. Please contact support. SmartLiving will be closed.\nError reason: User restore unsuccessful.");
//
//                                            ok.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View view) {
//                                                    dialog.dismiss();
//                                                    finish();
//                                                }
//                                            });
//
//                                            cancel.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View view) {
//                                                    dialog.dismiss();
//                                                    finish();
//                                                }
//                                            });
//                                            loading.cancel();
//                                            dialog.show();
//                                        }
//                                    }
//                                });
//
////                                dialog = new Dialog(AddFamilyUserActivity.this );
////                                dialog.setContentView(R.layout.dialog_box);
////                                dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_background));
////                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////                                dialog.setCancelable(false);
////
////                                //dialog.getWindow().getAttributes().windowAnimations = R.style.Animation;
////
////                                ImageView img = dialog.findViewById(R.id.dialog_box_icon);
////                                TextView title = dialog.findViewById(R.id.dialog_box_title);
////                                TextView subtitle = dialog.findViewById(R.id.dialog_box_description);
////                                Button ok = dialog.findViewById(R.id.dialog_box_btn_ok);
////                                Button cancel = dialog.findViewById(R.id.dialog_box_btn_cancel);
////
////                                //img.setImageResource(R.drawable.exclamation);
////                                img.setImageDrawable(getResources().getDrawable(R.drawable.checked, getApplicationContext().getTheme()));
////
////                                title.setText("SUCCESSFUL");
////                                subtitle.setText(Html.fromHtml("<p> User <b>" + txtEmail + "</b> created successfully. </p>"));
////
////                                ok.setOnClickListener(new View.OnClickListener() {
////                                    @Override
////                                    public void onClick(View view) {
////                                        dialog.dismiss();
////                                    }
////                                });
////
////                                cancel.setOnClickListener(new View.OnClickListener() {
////                                    @Override
////                                    public void onClick(View view) {
////                                        dialog.dismiss();
////                                    }
////                                });
////                                loading.cancel();
////                                dialog.show();
//                            } else {
//                                dialog = new Dialog(AddFamilyUserActivity.this );
//                                dialog.setContentView(R.layout.dialog_box);
//                                dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_background));
//                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                                dialog.setCancelable(false);
//
//                                //dialog.getWindow().getAttributes().windowAnimations = R.style.Animation;
//
//                                ImageView img = dialog.findViewById(R.id.dialog_box_icon);
//                                TextView title = dialog.findViewById(R.id.dialog_box_title);
//                                TextView subtitle = dialog.findViewById(R.id.dialog_box_description);
//                                Button ok = dialog.findViewById(R.id.dialog_box_btn_ok);
//                                Button cancel = dialog.findViewById(R.id.dialog_box_btn_cancel);
//
//                                //img.setImageResource(R.drawable.exclamation);
//                                img.setImageDrawable(getResources().getDrawable(R.drawable.exclamation, getApplicationContext().getTheme()));
//
//                                title.setText("ERROR");
//                                subtitle.setText(msg);
//
//                                ok.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        dialog.dismiss();
//                                    }
//                                });
//
//                                cancel.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        dialog.dismiss();
//                                    }
//                                });
//                                loading.cancel();
//                                dialog.show();
//                            }
//                        }
//
//                        @Override
//                        public void onEmailCrated(boolean isCreated, String errMsg) {
//                            dialog = new Dialog(AddFamilyUserActivity.this );
//                            dialog.setContentView(R.layout.dialog_box);
//                            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_background));
//                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                            dialog.setCancelable(false);
//
//                            //dialog.getWindow().getAttributes().windowAnimations = R.style.Animation;
//
//                            ImageView img = dialog.findViewById(R.id.dialog_box_icon);
//                            TextView title = dialog.findViewById(R.id.dialog_box_title);
//                            TextView subtitle = dialog.findViewById(R.id.dialog_box_description);
//                            Button ok = dialog.findViewById(R.id.dialog_box_btn_ok);
//                            Button cancel = dialog.findViewById(R.id.dialog_box_btn_cancel);
//
//                            //img.setImageResource(R.drawable.exclamation);
//                            img.setImageDrawable(getResources().getDrawable(R.drawable.exclamation, getApplicationContext().getTheme()));
//
//                            title.setText("ERROR");
//                            subtitle.setText(errMsg);
//
//                            ok.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    dialog.dismiss();
//                                }
//                            });
//
//                            cancel.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    dialog.dismiss();
//                                }
//                            });
//                            loading.cancel();
//                            dialog.show();
//                        }
//                    });
                } else {
                    loading.cancel();
                    Toast.makeText(AddFamilyUserActivity.this, "Please fill required fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}