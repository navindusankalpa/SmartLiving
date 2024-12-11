package com.example.smartliving.HomeControlDashBoard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

import com.example.smartliving.FireBase.FireBaseHandler;
import com.example.smartliving.Handlers.DeleteFamilyUserAdapter;
import com.example.smartliving.Handlers.DeleteFamilyUserHandler;
import com.example.smartliving.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RemoveFamilyUserActivity extends AppCompatActivity {

    Activity activity;
    Context context;
    Button cancel;
    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference dbRef;

    DeleteFamilyUserAdapter adapter;
    ArrayList<DeleteFamilyUserHandler> list;

    public RemoveFamilyUserActivity(Activity a, Context c){
        this.activity = a;
        this.context = c;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_family_user);

        recyclerView = findViewById(R.id.family_user_recycler);

        list = new ArrayList<>();
        adapter = new DeleteFamilyUserAdapter(getApplicationContext(), list);
        recyclerView.setAdapter(adapter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        String uid = new FireBaseHandler().getUid();
        dbRef = firebaseDatabase.getReference("Customers");
        dbRef.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot snapshot = task.getResult();
                System.out.println(task.getResult().toString());
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    DeleteFamilyUserHandler deleteFamilyUserHandler = dataSnapshot.getValue(DeleteFamilyUserHandler.class);
                    if (deleteFamilyUserHandler != null){
                        list.add(deleteFamilyUserHandler);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

    }
}