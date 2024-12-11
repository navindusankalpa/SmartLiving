package com.example.smartliving.Handlers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smartliving.FireBase.FireBaseHandler;
import com.example.smartliving.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DeleteFamilyUserAdapter extends RecyclerView.Adapter<DeleteFamilyUserAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<DeleteFamilyUserHandler> list;
    private DeleteFamilyUserHandler deleteFamilyUserHandler;
    FirebaseDatabase firebaseDatabase;

    public DeleteFamilyUserAdapter(Context c, ArrayList<DeleteFamilyUserHandler> l){
        this.context = c;
        this.list = l;
    }


    @NonNull
    @Override
    public DeleteFamilyUserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.delete_family_user_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeleteFamilyUserAdapter.MyViewHolder holder, int position) {
        String imgUrl, fName, lName, email, uid;
        FireBaseHandler fireBaseHandler = new FireBaseHandler();

        deleteFamilyUserHandler = list.get(position);
        firebaseDatabase = FirebaseDatabase.getInstance();

        fName = deleteFamilyUserHandler.getFirst_name();
        lName = deleteFamilyUserHandler.getLast_name();
        email = deleteFamilyUserHandler.getEmail();
        uid = deleteFamilyUserHandler.getKey();

        getImg(uid, new onEmailReturned() {
            @Override
            public void onEmailReturnedFun(boolean isSuccess, String email) {
                if (isSuccess){
                    Glide.with(holder.itemView).load(email).into(holder.profileImg);
                }
            }
        });

        String fullName = fName.substring(0,1).toUpperCase() + fName.substring(1).toLowerCase() + " " + lName.substring(0,1).toUpperCase() + lName.substring(1).toLowerCase();
        holder.name.setText(fullName);
        holder.email.setText(email);

        fireBaseHandler.chkUserRevoked(uid, new FireBaseHandler.userRevoked() {
            @Override
            public void onUserRevoked(boolean isRev) {
                if (isRev){
                    holder.delete.setVisibility(View.GONE);
                }
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    DatabaseReference dbref = firebaseDatabase.getReference("Users").child(uid).child("isRevoked");
                    dbref.setValue(true)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(context, "User " + email + " revoked successfully.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } catch (Exception e){
                    Log.e("Firebase Error", e.getMessage());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, email;
        ImageView profileImg, delete;
        public MyViewHolder(@NonNull View itemView)  {
            super(itemView);
        }
    }

    private void getImg(String UID, final onEmailReturned callback){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(UID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    String email = snapshot.child("profile_img").getValue().toString();
                    callback.onEmailReturnedFun(true, email);
                }else{
                    callback.onEmailReturnedFun(false, task.getException().toString());
                }
            }
        });
    }
    private interface onEmailReturned{
        void onEmailReturnedFun(boolean isSuccess, String email);
    }
}
