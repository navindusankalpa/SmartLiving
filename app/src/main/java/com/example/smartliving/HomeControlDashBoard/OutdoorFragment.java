
package com.example.smartliving.HomeControlDashBoard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.smartliving.FireBase.Dashboard;
import com.example.smartliving.FireBase.FireBaseHandler;
import com.example.smartliving.Handlers.NotificationHandler;
import com.example.smartliving.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OutdoorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OutdoorFragment extends Fragment {
    Switch door, gardenLights;

    boolean isDoorSecured;
    private boolean doorState, gardenState;
    Button outdoorSecurity;
    String UID;
    DatabaseReference dbRef;
    TextView rainStatus;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OutdoorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OutdoorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OutdoorFragment newInstance(String param1, String param2) {
        OutdoorFragment fragment = new OutdoorFragment();
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

        UID = new FireBaseHandler().getUid();
        new Dashboard().getSerial(UID, new Dashboard.getSerialInterface() {
            @Override
            public void onSerialFetched(String serial) {
                dbRef = FirebaseDatabase.getInstance().getReference("Devices").child(serial).child("door");
                dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (Boolean.TRUE.equals(snapshot.getValue(Boolean.class))){
                            door.setChecked(true);
                        } else{
                            door.setChecked(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                dbRef = FirebaseDatabase.getInstance().getReference("Devices").child(serial).child("garden");
                dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(Boolean.TRUE.equals(snapshot.getValue(Boolean.class))){
                            gardenLights.setChecked(true);
                        } else {
                            gardenLights.setChecked(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                dbRef = FirebaseDatabase.getInstance().getReference("Devices").child(serial).child("is_door_secured");
                dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (Boolean.TRUE.equals(snapshot.getValue(Boolean.class))){
                            isDoorSecured = true;
                            outdoorSecurity.setText("Manual");
                        } else{
                            isDoorSecured = false;
                            outdoorSecurity.setText("Auto");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                dbRef = FirebaseDatabase.getInstance().getReference("Devices").child(serial).child("rain");
                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (Boolean.TRUE.equals(snapshot.getValue(Boolean.class))){
                            rainStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                            rainStatus.setText("Rain Detected");
                        } else {
                            rainStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                            rainStatus.setText("Not Detected");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

//        viewModel.getDoorState().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                doorState = aBoolean;
//            }
//        });
//
//        viewModel.getGardenLightsState().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                gardenState = aBoolean;
//            }
//        });


//        UID = new FireBaseHandler().getUid();
//        new Dashboard().getSerial(UID, new Dashboard.getSerialInterface() {
//            @Override
//            public void onSerialFetched(String serial) {
//                dbRef = FirebaseDatabase.getInstance().getReference("Devices").child(serial).child("door");
//                dbRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (Boolean.TRUE.equals(snapshot.getValue(Boolean.class))){
//                            door.setChecked(true);
//                        } else{
//                            door.setChecked(false);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//                dbRef = FirebaseDatabase.getInstance().getReference("Devices").child(serial).child("garden");
//                dbRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if(Boolean.TRUE.equals(snapshot.getValue(Boolean.class))){
//                            gardenLights.setChecked(true);
//                        } else {
//                            gardenLights.setChecked(false);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//                new Dashboard().getSerial(UID, new Dashboard.getSerialInterface() {
//                    @Override
//                    public void onSerialFetched(String serial) {
//                        dbRef = FirebaseDatabase.getInstance().getReference("Devices").child(serial).child("rain");
//                        dbRef.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                if (Boolean.TRUE.equals(snapshot.getValue(Boolean.class))){
//                                    rainStatus.setText("Rain Detected");
//
//                                    Context context = getContext();
//                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
//                                            .setSmallIcon(R.drawable.checked)
//                                            .setContentTitle("Rain Detected")
//                                            .setContentText("SmartLiving detected rain in your premises.")
//                                            .setPriority(NotificationCompat.PRIORITY_HIGH);
//
//                                    // Intent to open an activity when the notification is clicked
//                                    if (context != null) {
//                                        Intent intent = new Intent(context, OutdoorFragment.class);
//                                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                                        builder.setContentIntent(pendingIntent);
//                                    }
//
//                                    UUID uuid = UUID.randomUUID();
//
//                                    // Convert the UUID to a unique integer
//                                    long mostSignificantBits = uuid.getMostSignificantBits();
//                                    long leastSignificantBits = uuid.getLeastSignificantBits();
//                                    int notificationId = (int) (mostSignificantBits ^ leastSignificantBits);
//
//                                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//                                    Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                                    Ringtone ringtone = RingtoneManager.getRingtone(context, notificationSoundUri);
//
//                                    // Play the notification sound
//                                    ringtone.play();
//                                    Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
////                            vibrator.vibrate(250);
//
//                                    long[] pattern = {0, 100, 200, 300}; // Vibration pattern: off, on, off, on
//                                    vibrator.vibrate(pattern, -1);
//
//                                    notificationManager.notify(notificationId, builder.build());
//
//
//                                    rainStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
//                                } else {
//                                    rainStatus.setText("Not Detected");
//                                    rainStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//                    }
//                });
//
//                dbRef = FirebaseDatabase.getInstance().getReference("Devices").child(serial).child("is_door_secured");
//                dbRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (Boolean.TRUE.equals(snapshot.getValue(Boolean.class))){
//                            isDoorSecured = true;
//                            outdoorSecurity.setText("Manual");
//                        } else{
//                            isDoorSecured = false;
//                            outdoorSecurity.setText("Auto");
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            }
//        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_outdoor, container, false);

        rainStatus = v.findViewById(R.id.outdoor_rain_status);
        gardenLights = v.findViewById(R.id.outdoor_garden_lights);
        door = v.findViewById(R.id.outdoor_main_door);
        outdoorSecurity = v.findViewById(R.id.outdoor_btn_door_security);

//        UID = new FireBaseHandler().getUid();
//        new Dashboard().getSerial(UID, new Dashboard.getSerialInterface() {
//            @Override
//            public void onSerialFetched(String serial) {
//                dbRef = FirebaseDatabase.getInstance().getReference("Devices").child(serial).child("door");
//                dbRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (Boolean.TRUE.equals(snapshot.getValue(Boolean.class))){
//                            viewModel.setDoorState(true);
//                            //door.setChecked(true);
//                        } else{
//                            viewModel.setDoorState(false);
//                            //door.setChecked(false);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//                dbRef = FirebaseDatabase.getInstance().getReference("Devices").child(serial).child("garden");
//                dbRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if(Boolean.TRUE.equals(snapshot.getValue(Boolean.class))){
//                            viewModel.setGardenLightsState(true);
//                            //gardenLights.setChecked(true);
//                        } else {
//                            viewModel.setGardenLightsState(false);
//                            //gardenLights.setChecked(false);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//                dbRef = FirebaseDatabase.getInstance().getReference("Devices").child(serial).child("is_door_secured");
//                dbRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (Boolean.TRUE.equals(snapshot.getValue(Boolean.class))){
//                            isDoorSecured = true;
//                            outdoorSecurity.setText("Manual");
//                        } else{
//                            isDoorSecured = false;
//                            outdoorSecurity.setText("Auto");
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            }
//        });

        outdoorSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Dashboard().getSerial(UID, new Dashboard.getSerialInterface() {
                    @Override
                    public void onSerialFetched(String serial) {
                        dbRef = FirebaseDatabase.getInstance().getReference("Devices").child(serial);
                        if (isDoorSecured){

                            dbRef.child("is_door_secured").setValue(false);
                            outdoorSecurity.setText("Auto");
                        } else{

                            dbRef.child("is_door_secured").setValue(true);
                            outdoorSecurity.setText("Manual");
                        }
                    }
                });
            }
        });

        door.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Dashboard dashboard = new Dashboard();
                if (door.isChecked()){
                    dashboard.getSerial(UID, new Dashboard.getSerialInterface() {
                        @Override
                        public void onSerialFetched(String serial) {
                            dbRef = FirebaseDatabase.getInstance().getReference("Devices").child(serial);
                            dbRef.child("door").setValue(true);
                        }
                    });
                } else{
                    dashboard.getSerial(UID, new Dashboard.getSerialInterface() {
                        @Override
                        public void onSerialFetched(String serial) {
                            dbRef = FirebaseDatabase.getInstance().getReference("Devices").child(serial);
                            dbRef.child("door").setValue(false);
                        }
                    });
                }
            }
        });

        gardenLights.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Dashboard dashboard = new Dashboard();
                if (gardenLights.isChecked()){
                    dashboard.getSerial(UID, new Dashboard.getSerialInterface() {
                        @Override
                        public void onSerialFetched(String serial) {
                            dbRef = FirebaseDatabase.getInstance().getReference("Devices").child(serial);
                            dbRef.child("garden").setValue(true);
                        }
                    });
                } else{
                    dashboard.getSerial(UID, new Dashboard.getSerialInterface() {
                        @Override
                        public void onSerialFetched(String serial) {
                            dbRef = FirebaseDatabase.getInstance().getReference("Devices").child(serial);
                            dbRef.child("garden").setValue(false);
                        }
                    });
                }
            }
        });

        // Rain notification

//        new Dashboard().getSerial(UID, new Dashboard.getSerialInterface() {
//            @Override
//            public void onSerialFetched(String serial) {
//                dbRef = FirebaseDatabase.getInstance().getReference("Devices").child(serial).child("rain");
//                dbRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (Boolean.TRUE.equals(snapshot.getValue(Boolean.class))){
//                            rainStatus.setText("Rain Detected");
//
//                            Context context = getContext();
//                            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
//                                    .setSmallIcon(R.drawable.checked)
//                                    .setContentTitle("Rain Detected")
//                                    .setContentText("SmartLiving detected rain in your premises.")
//                                    .setPriority(NotificationCompat.PRIORITY_HIGH);
//
//                            // Intent to open an activity when the notification is clicked
//                            if (context != null) {
//                                Intent intent = new Intent(context, OutdoorFragment.class);
//                                PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                                builder.setContentIntent(pendingIntent);
//                            }
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
//
//                            rainStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
//                        } else {
//                            rainStatus.setText("Not Detected");
//                            rainStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            }
//        });

        new Dashboard().getSerial(UID, new Dashboard.getSerialInterface() {
            @Override
            public void onSerialFetched(String serial) {
                dbRef = FirebaseDatabase.getInstance().getReference("Devices").child(serial);
                dbRef.child("rain").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String currentRainStatus = rainStatus.getText().toString();
                        if (currentRainStatus.equals("Not Detected") && Objects.equals(snapshot.getValue(Boolean.class), true)){
                            try{
                                rainStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                            } catch (NullPointerException ignored){}
                            rainStatus.setText("Rain Detected");

                            if (getContext() != null) {
                                new NotificationHandler().showNotificationWithIntent(getContext(), "Rain Detected", "SmartLiving detected rain in your premises", OutdoorFragment.class);
                            }

                        } else if (currentRainStatus.equals("Rain Detected") && Objects.equals(snapshot.getValue(Boolean.class), false)) {
                            try {
                                rainStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                            } catch (NullPointerException ignored){}
                            rainStatus.setText("Not Detected");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        return v;
    }
}