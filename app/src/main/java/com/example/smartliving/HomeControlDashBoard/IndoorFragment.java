package com.example.smartliving.HomeControlDashBoard;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartliving.FireBase.Dashboard;
import com.example.smartliving.FireBase.FireBaseHandler;
import com.example.smartliving.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IndoorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IndoorFragment extends Fragment {
    Switch kitchen, livingRoom;
    TextView gasLevel;
    DatabaseReference databaseReference;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public IndoorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IndoorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IndoorFragment newInstance(String param1, String param2) {
        IndoorFragment fragment = new IndoorFragment();
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

        View v = inflater.inflate(R.layout.fragment_indoor, container, false);

        gasLevel = v.findViewById(R.id.indoor_gas);
        kitchen = v.findViewById(R.id.indoor_kitchen_light_switch);
        livingRoom = v.findViewById(R.id.indoor_living_room_light_switch);

        String uid = new FireBaseHandler().getUid();

        new Dashboard().getSerial(uid, new Dashboard.getSerialInterface() {
            @Override
            public void onSerialFetched(String serial) {
                databaseReference = FirebaseDatabase.getInstance().getReference("Devices").child(serial).child("kitchen");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(Boolean.TRUE.equals(snapshot.getValue(Boolean.class))){
                            kitchen.setChecked(true);
                        } else{
                            kitchen.setChecked(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                databaseReference = FirebaseDatabase.getInstance().getReference("Devices").child(serial).child("living_room");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(Boolean.TRUE.equals(snapshot.getValue(Boolean.class))){
                            livingRoom.setChecked(true);
                        } else {
                            livingRoom.setChecked(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        new Dashboard().getSerial(uid, new Dashboard.getSerialInterface() {
            @Override
            public void onSerialFetched(String serial) {
                databaseReference = FirebaseDatabase.getInstance().getReference("Devices").child(serial).child("gas");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        int gas = snapshot.getValue(Integer.class);
//                        if (gas <= 5){
//                            gasLevel.setText("Normal");
//                            gasLevel.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
//                        } else if (gas > 6 && gas <= 10) {
//                            Context context = getContext();
//                            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
//                                    .setSmallIcon(R.drawable.exclamation)
//                                    .setContentTitle("Medium Gas Level Detected")
//                                    .setContentText("SmartLiving detected medium gas level in your home.")
//                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//                            // Intent to open an activity when the notification is clicked
//
//                                Intent intent = new Intent(context, IndoorFragment.class);
//                                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                                builder.setContentIntent(pendingIntent);
//
//
//                            UUID uuid = UUID.randomUUID();
//
//                            // Convert the UUID to a unique integer
//                            long mostSignificantBits = uuid.getMostSignificantBits();
//                            long leastSignificantBits = uuid.getLeastSignificantBits();
//                            int notificationId = (int) (mostSignificantBits ^ leastSignificantBits);
//
//                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//                            Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                            Ringtone ringtone = RingtoneManager.getRingtone(context, notificationSoundUri);
//
//                            // Play the notification sound
//                            ringtone.play();
//                            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
////                            vibrator.vibrate(250);
//
//                            long[] pattern = {0, 100, 200, 300}; // Vibration pattern: off, on, off, on
//                            vibrator.vibrate(pattern, -1);
//
//                            notificationManager.notify(notificationId, builder.build());
//
//                            gasLevel.setText("Medium");
//                            gasLevel.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_orange));
//                        } else{
//                            Context context = getContext();
//                            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
//                                    .setSmallIcon(R.drawable.exclamation)
//                                    .setContentTitle("Dangerous Gas Level Detected")
//                                    .setContentText("SmartLiving detected dangerous gas level in your home.")
//                                    .setPriority(NotificationCompat.PRIORITY_HIGH);
//
//                            // Intent to open an activity when the notification is clicked
//
//                                Intent intent = new Intent(context, IndoorFragment.class);
//                                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                                builder.setContentIntent(pendingIntent);
//
//
//                            UUID uuid = UUID.randomUUID();
//
//                            // Convert the UUID to a unique integer
//                            long mostSignificantBits = uuid.getMostSignificantBits();
//                            long leastSignificantBits = uuid.getLeastSignificantBits();
//                            int notificationId = (int) (mostSignificantBits ^ leastSignificantBits);
//
//                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//                            Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                            Ringtone ringtone = RingtoneManager.getRingtone(context, notificationSoundUri);
//
//                            // Play the notification sound
//                            ringtone.play();
//                            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
////                            vibrator.vibrate(250);
//
//                            long[] pattern = {0, 100, 200, 300}; // Vibration pattern: off, on, off, on
//                            vibrator.vibrate(pattern, -1);
//
//                            notificationManager.notify(notificationId, builder.build());
//                            gasLevel.setText("Dangerous " + String.valueOf(gas));
//                            gasLevel.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
//                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Data fetch error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        kitchen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (kitchen.isChecked()){
                    new Dashboard().getSerial(uid, new Dashboard.getSerialInterface() {
                        @Override
                        public void onSerialFetched(String serial) {
                            databaseReference = FirebaseDatabase.getInstance().getReference("Devices").child(serial);
                            databaseReference.child("kitchen").setValue(true);
                        }
                    });
                } else{
                    new Dashboard().getSerial(uid, new Dashboard.getSerialInterface() {
                        @Override
                        public void onSerialFetched(String serial) {
                            databaseReference = FirebaseDatabase.getInstance().getReference("Devices").child(serial);
                            databaseReference.child("kitchen").setValue(false);
                        }
                    });
                }
            }
        });

        livingRoom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (livingRoom.isChecked()){
                    new Dashboard().getSerial(uid, new Dashboard.getSerialInterface() {
                        @Override
                        public void onSerialFetched(String serial) {
                            databaseReference = FirebaseDatabase.getInstance().getReference("Devices").child(serial);
                            databaseReference.child("living_room").setValue(true);
                        }
                    });
                } else {
                    new Dashboard().getSerial(uid, new Dashboard.getSerialInterface() {
                        @Override
                        public void onSerialFetched(String serial) {
                            databaseReference = FirebaseDatabase.getInstance().getReference("Devices").child(serial);
                            databaseReference.child("living_room").setValue(false);
                        }
                    });
                }
            }
        });


        return v;
    }
}