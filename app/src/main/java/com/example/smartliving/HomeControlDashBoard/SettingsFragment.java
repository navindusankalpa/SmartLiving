package com.example.smartliving.HomeControlDashBoard;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartliving.FireBase.FireBaseHandler;
import com.example.smartliving.Profile.AddFamilyUserActivity;
import com.example.smartliving.Profile.EditProfileActivity;
import com.example.smartliving.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.core.content.ContextCompat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    private Button addFamilyUser, removeFamilyUser, resetPassword, editProfile, help;
    Dialog revokeFamilyUser;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_settings, container, false);

        addFamilyUser = v.findViewById(R.id.settings_add_family_user);
        removeFamilyUser = v.findViewById(R.id.settings_delete_family_user);
        resetPassword = v.findViewById(R.id.settings_reset_password);
        editProfile = v.findViewById(R.id.settings_edit_profile);
        help = v.findViewById(R.id.settings_get_help);

        FireBaseHandler fireBaseHandler = new FireBaseHandler();
        String uid = fireBaseHandler.getUid();

        fireBaseHandler.getUserTypeOnSettings(uid, new FireBaseHandler.getUsrtype() {
            @Override
            public void onUserTypeFetched(boolean isSuccess, String usertype) {
                if (isSuccess){
                    if (usertype.equals("customer")){
                        addFamilyUser.setVisibility(View.VISIBLE);
                        removeFamilyUser.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog dialog2 = new Dialog(getContext());
                dialog2.setContentView(R.layout.dialog_box);
                dialog2.getWindow().setBackgroundDrawable(getDrawable(getContext(), R.drawable.dialog_box_background));
                dialog2.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog2.setCancelable(true);

                ImageView img = dialog2.findViewById(R.id.dialog_box_icon);
                TextView title = dialog2.findViewById(R.id.dialog_box_title);
                TextView subtitle = dialog2.findViewById(R.id.dialog_box_description);
                Button ok = dialog2.findViewById(R.id.dialog_box_btn_ok);
                Button cancel = dialog2.findViewById(R.id.dialog_box_btn_cancel);

                ok.setText("Call");
                cancel.setText("Email");
                cancel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFA500")));

                img.setImageDrawable(getResources().getDrawable(R.drawable.helloo, getContext().getTheme()));

                title.setText("HELP");
                subtitle.setText("What's your favourite way to reach us?");

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + "+94775012808"));
                        dialog2.dismiss();
                        startActivity(intent);
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), GetHelpActivity.class);
                        dialog2.dismiss();
                        startActivity(intent);
                    }
                });

                dialog2.show();

            }
        });

        removeFamilyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uID = new FireBaseHandler().getUid();
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Customers");
                dbRef.child(uID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        DataSnapshot snapshot = task.getResult();
                        System.out.println(task.getResult().toString());
                        if (snapshot.child("family_users").getValue().toString().equals("")){
                            Dialog dialog2 = new Dialog(getContext());
                            dialog2.setContentView(R.layout.dialog_box);
                            dialog2.getWindow().setBackgroundDrawable(getDrawable(getContext(), R.drawable.dialog_box_background));
                            dialog2.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            dialog2.setCancelable(false);

                            ImageView img = dialog2.findViewById(R.id.dialog_box_icon);
                            TextView title = dialog2.findViewById(R.id.dialog_box_title);
                            TextView subtitle = dialog2.findViewById(R.id.dialog_box_description);
                            Button ok = dialog2.findViewById(R.id.dialog_box_btn_ok);
                            Button cancel = dialog2.findViewById(R.id.dialog_box_btn_cancel);

                            //img.setImageResource(R.drawable.exclamation);
                            img.setImageDrawable(getResources().getDrawable(R.drawable.exclamation, getContext().getTheme()));

                            title.setText("EMPTY");

                            subtitle.setText("There are no family users registered under you.");

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
                        } else{
                            revokeFamilyUser = new Dialog(getContext());
                            revokeFamilyUser.setContentView(R.layout.activity_remove_family_user);
                            revokeFamilyUser.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.dialog_box_background));
                            revokeFamilyUser.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            revokeFamilyUser.setCancelable(false);

                            Button cancel = revokeFamilyUser.findViewById(R.id.family_user_cancel);

                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    revokeFamilyUser.cancel();
                                }
                            });

                            revokeFamilyUser.show();
                        }
                    }
                });
            }
        });

        addFamilyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddFamilyUserActivity.class);
                startActivity(intent);
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog2 = new Dialog(getContext());
                dialog2.setContentView(R.layout.dialog_box);
                dialog2.getWindow().setBackgroundDrawable(getDrawable(getContext(), R.drawable.dialog_box_background));
                dialog2.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog2.setCancelable(false);

                ImageView img = dialog2.findViewById(R.id.dialog_box_icon);
                TextView title = dialog2.findViewById(R.id.dialog_box_title);
                TextView subtitle = dialog2.findViewById(R.id.dialog_box_description);
                Button ok = dialog2.findViewById(R.id.dialog_box_btn_ok);
                Button cancel = dialog2.findViewById(R.id.dialog_box_btn_cancel);

                //img.setImageResource(R.drawable.exclamation);
                img.setImageDrawable(getResources().getDrawable(R.drawable.question_mark, getContext().getTheme()));

                title.setText("Reset Password");

                subtitle.setText("Are you sure to reset your password?");

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog2.dismiss();
                        ProgressDialog loading = new ProgressDialog(getContext());
                        loading.setCancelable(false);
                        loading.setMessage("Please wait");
                        loading.show();

                        String uid = new FireBaseHandler().getUid();
                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");
                        dbRef.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                DataSnapshot dataSnapshot = task.getResult();
                                if (dataSnapshot.child("user_type").getValue(String.class).equals("customer")){
                                    DatabaseReference dbRF = FirebaseDatabase.getInstance().getReference("Customers");
                                    dbRF.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            DataSnapshot snapshot = task.getResult();
                                            String email = snapshot.child("email").getValue(String.class);

                                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                            mAuth.sendPasswordResetEmail(email)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            loading.cancel();
                                                            Toast.makeText(getContext(), "Password reset email is sent to your inbox", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            loading.cancel();
                                                            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    });
                                } else {
                                    DatabaseReference dbRF = FirebaseDatabase.getInstance().getReference("FamilyUsers");
                                    dbRF.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            DataSnapshot snapshot = task.getResult();
                                            String email = snapshot.child("email").getValue(String.class);

                                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                            mAuth.sendPasswordResetEmail(email)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            loading.cancel();
                                                            Toast.makeText(getContext(), "Password reset email is sent to your inbox, please check", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            loading.cancel();
                                                            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    });
                                }
                            }
                        });
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

        return v;
    }
}