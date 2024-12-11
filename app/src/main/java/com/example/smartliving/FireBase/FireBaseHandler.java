package com.example.smartliving.FireBase;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FireBaseHandler {
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference dbRef;

    public String getUid(){
        return (Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
    }

    public void createFamilyUser(String firstName, String lastName, String email, String password, String UID, String serial, final createFamilyUserCallback callback){
        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    String firebaseUser = getUid();

                    @Override
                    public void onSuccess(AuthResult authResult) {
                        firebaseDatabase = FirebaseDatabase.getInstance();
                        dbRef = firebaseDatabase.getReference("FamilyUsers").child(healthyEmail(email));

                        Map<String, Object> updates = new HashMap<>();

                        updates.put("firstName", firstName);
                        updates.put("lastName", lastName);
                        updates.put("email", email);
                        updates.put("root_user", UID);

                        dbRef.updateChildren(updates)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        String dbEmail = healthyEmail(email);
                                        dbRef = firebaseDatabase.getReference("Users").child(dbEmail);
                                        dbRef.child("is_db_updated").setValue(false);
                                        dbRef.child("serial").setValue(serial);
                                        dbRef.child("isRevoked").setValue(false);
                                        dbRef.child("profile_img").setValue("https://firebasestorage.googleapis.com/v0/b/smartliving-aaa3a.appspot.com/o/user.png?alt=media&token=10de0b9f-1fb7-46dd-bfca-ae1546388f92");
                                        dbRef.child("user_type").setValue("family_user");

                                        firebaseDatabase.getReference("Customers").child(UID).child("family_users").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                long count = snapshot.getChildrenCount();
                                                long newKey = count + 1;

                                                firebaseDatabase.getReference("Customers").child(UID).child("family_users").child(String.valueOf(newKey)).setValue(healthyEmail(email))
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                callback.onFamilyUserCreated(true, null);
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                callback.onFamilyUserCreated(false, e.getMessage());
                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        callback.onFamilyUserCreated(false, e.getMessage());
                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onEmailCrated(false, e.getMessage());
                    }
                });
    }

    public void checkSerial(String serial, final checkSerialCallback callback){
        firebaseDatabase = FirebaseDatabase.getInstance();
        dbRef = firebaseDatabase.getReference("Serials");


        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isFound = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if (dataSnapshot.getKey().equals(serial)){
                        isFound = true;
                        break;
                    }
                }
                if (isFound){
                    dbRef = firebaseDatabase.getReference("Serials").child(serial).child("isRegistered");
                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean isRegistered = true;
                            if (Boolean.TRUE.equals(snapshot.getValue(Boolean.class))){
                                isRegistered = true;
                            } else if (Boolean.FALSE.equals(snapshot.getValue(Boolean.class))) {
                                isRegistered = false;
                            }
                            callback.onSerialFound(true, isRegistered);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    callback.onSerialFound(false, true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface checkSerialCallback{
        void onSerialFound(boolean isAvailable, boolean isRegistered);
    }

    public void writerUserData(String serial, String firstName, String lastName, String birthday, String address, String email, String phone, Context c, final writeUserCallback callback){
        String UID = getUid();

        firebaseDatabase = FirebaseDatabase.getInstance();
        dbRef = firebaseDatabase.getReference("Customers").child(UID);

        List<String> myList = new ArrayList<String>(Arrays.asList(email.split("")));
        Map<String, Object> updates = new HashMap<>();

        updates.put("firstName", firstName);
        updates.put("lastName", lastName);
        updates.put("email", email);
        updates.put("birthday", birthday);
        updates.put("address", address);
        updates.put("family_users", "");
        updates.put("serial", serial);
        updates.put("phone", phone);

        dbRef.updateChildren(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        callback.onUserCreated(true, "true");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onUserCreated(false, e.getMessage());
                    }
                });
    }

    public void uploadProfilePhoto(String UID){

    }

    public interface writeUserCallback{
       void onUserCreated(boolean result, String msg);
    }

    public interface createFamilyUserCallback{
        void onFamilyUserCreated(boolean isSuccessful, String msg);
        void onEmailCrated(boolean isCreated, String errMsg);
    }

    public void getUserTypeByUID(String UID, String email, final getUsrtype callback){
        firebaseDatabase = FirebaseDatabase.getInstance();
        dbRef = firebaseDatabase.getReference("Users");

        dbRef.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists() && snapshot.getValue() != null){
                    callback.onUserTypeFetched(true, snapshot.child("user_type").getValue(String.class));
                } else {
                    DatabaseReference dbRef1 = FirebaseDatabase.getInstance().getReference("Users").child(email);
                    dbRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.w("State", "Inside of onDataChange");
                            if(snapshot.exists()){
                                callback.onUserTypeFetched(true, snapshot.child("user_type").getValue(String.class));
                            } else{
                                callback.onUserTypeFetched(false, null);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            callback.onUserTypeFetched(false, null);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onUserTypeFetched(false, null);
            }
        });
    }

    public void getUserTypeOnSettings(String uid, final getUsrtype callback){
        dbRef = FirebaseDatabase.getInstance().getReference("Users");
        dbRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onUserTypeFetched(true, snapshot.child("user_type").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onUserTypeFetched(false, null);
            }
        });
    }

    public interface getUsrtype{
        void onUserTypeFetched(boolean isSuccess, String usertype);
    }

    public String getSerialFamilyUser(String UID){
        dbRef = firebaseDatabase.getReference("FamilyUsers").child("UID").child("serial");
        Log.w("Family Serial", dbRef.getKey());
        return dbRef.getKey();
    }

    public void chkUserRevoked(String uid, final userRevoked callback){
        DatabaseReference dbREF = FirebaseDatabase.getInstance().getReference("Users");
        dbREF.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                boolean rev;
                if (task.isSuccessful()){
                    DataSnapshot dataSnapshot = task.getResult();
                    rev = Boolean.TRUE.equals(dataSnapshot.child("isRevoked").getValue(Boolean.class));
                    callback.onUserRevoked(rev);
                } else {
                    Log.e("UID", Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        });
    }

    public interface userRevoked{
        void onUserRevoked(boolean isRev);
    }

    public String healthyEmail(String email){
        if (email.contains(".")){
            email = email.replace(".", "+");
        }
        if (email.contains("[")) {
            email = email.replace("[", "{");
        }
        if (email.contains("]")){
            email = email.replace("]", "}");
        }
        if (email.contains("$")){
            email = email.replace("$", "^");
        }
        if (email.contains("#")){
            email = email.replace("#", "!");
        }
        if (email.contains("/")){
            email = email.replace("/", "?");
        }
        return email;
    }

    public String reverseHealthyEmail(String email){
        if (email.contains("+")){
            email = email.replace("+", ".");
        }
        if (email.contains("{")) {
            email = email.replace("{", "[");
        }
        if (email.contains("}")){
            email = email.replace("}", "]");
        }
        if (email.contains("^")){
            email = email.replace("^", "$");
        }
        if (email.contains("!")){
            email = email.replace("!", "#");
        }
        if (email.contains("?")){
            email = email.replace("?", "/");
        }
        return email;
    }

    public void restoreUser(Context context, final RestoreUser callback){
        SharedPreferences sharedPreferences = context.getSharedPreferences("current_user", MODE_PRIVATE);
        String SPEmail = sharedPreferences.getString("email", null);
        String SPPass = sharedPreferences.getString("pass", null);

        mAuth.signInWithEmailAndPassword(SPEmail, SPPass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        callback.onUserRestored(task.isSuccessful());
                    }
                });
    }

    public interface RestoreUser{
        void onUserRestored(boolean state);
    }

    public void changeSerialStatus(String serial, final changeSerialStatusCallback callback){
        dbRef =  FirebaseDatabase.getInstance().getReference("Serials").child(serial);
        dbRef.child("isRegistered").setValue(true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        callback.onStatusChanged(true);
                    } else{
                        callback.onStatusChanged(false);
                    }
                }
        });
    }

    public interface changeSerialStatusCallback{
        void onStatusChanged(boolean serialStatus);
    }

}
