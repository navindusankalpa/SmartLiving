package com.example.smartliving.HomeControlDashBoard;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.smartliving.FireBase.FireBaseHandler;
import com.example.smartliving.Login.LoginActivity;
import com.example.smartliving.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private Uri uri;
    ImageView profile_image;
    ProgressDialog progressDialog;
    Button btnLogout;
    Dialog dialog2;
    TextView email, profileUserType;
    StorageReference storageReference;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View v =  inflater.inflate(R.layout.fragment_profile, container, false);

        profile_image = v.findViewById(R.id.profile_image);
        btnLogout = v.findViewById(R.id.btn_profile_logout);
        email = v.findViewById(R.id.profile_email);
        profileUserType = v.findViewById(R.id.profile_usertype);

        String UID = new FireBaseHandler().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(UID);
        dbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot dataSnapshot = task.getResult();
                String usertype = dataSnapshot.child("user_type").getValue(String.class);
                if (usertype.equals("customer")){
                    DatabaseReference dbRef1 = FirebaseDatabase.getInstance().getReference("Customers").child(UID);
                    dbRef1.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            DataSnapshot dataSnapshot1 = task.getResult();
                            email.setText(dataSnapshot1.child("email").getValue(String.class));
                            profileUserType.setText("Customer");
                        }
                    });
                } else {
                    DatabaseReference dbRef1 = FirebaseDatabase.getInstance().getReference("FamilyUsers").child(UID);
                    dbRef1.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            DataSnapshot dataSnapshot1 = task.getResult();
                            email.setText(dataSnapshot1.child("email").getValue(String.class));
                            profileUserType.setText("Family User");
                        }
                    });
                }
            }
        });

        getImageUrl(new getImgUrl() {
            @Override
            public void onImgUrlReady(String url) {
                Glide.with(getActivity())
                        .load(url)
                        .listener(new RequestListener<Drawable>() {

                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(profile_image);
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImg();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                dialog2 = new Dialog(getContext());
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

                title.setText("LOGOUT");

                subtitle.setText("Are you sure to logout from the app?");

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog2.dismiss();
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        FirebaseAuth.getInstance().signOut();

                        SharedPreferences sharedPref = getActivity().getSharedPreferences("current_user", Context.MODE_PRIVATE);
                        SharedPreferences.Editor prefEditor = sharedPref.edit();
                        prefEditor.putString("email", "");
                        prefEditor.putString("pass", "");
                        prefEditor.apply();

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("checkbox", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("remember", "false");
                        editor.putString("email", "");
                        editor.putString("password", "");
                        editor.apply();
                        getActivity().finish();
                        startActivity(intent);
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

    private void selectImg(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data!=null && data.getData()!=null){
            uri = data.getData();
            profile_image.setImageURI(uri);

            uploadImg(new onDownloadUrlReady() {

                @Override
                public void onDownloadUrlFun(String url) {
                    String UID = new FireBaseHandler().getUid();
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference rootDatabase = db.getReference("Users").child(UID);

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("profile_img", url);

                    rootDatabase.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    public void uploadImg(final onDownloadUrlReady callback){
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Image is uploading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String imageTitle = productID("PROFILE" );
        storageReference = FirebaseStorage.getInstance().getReference("images/" + imageTitle);
        storageReference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                callback.onDownloadUrlFun(uri.toString());
                                progressDialog.cancel();
                            }
                        });

                        profile_image.setImageURI(uri);
                        progressDialog.cancel();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.cancel();
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public interface onDownloadUrlReady{
        void onDownloadUrlFun(String url);
    }

    public String productID(String category){
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        category = category.toUpperCase();
        return (category + date);

    }

    public void getImageUrl(final getImgUrl callback){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        String UID = new FireBaseHandler().getUid();
        databaseReference.child(UID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    String img = snapshot.child("profile_img").getValue().toString();
                    callback.onImgUrlReady(img);
                }
            }
        });
    }

    public interface getImgUrl{
        void onImgUrlReady(String url);
    }
}