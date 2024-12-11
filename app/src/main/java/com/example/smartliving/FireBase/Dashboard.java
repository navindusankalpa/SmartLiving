package com.example.smartliving.FireBase;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Dashboard {
    private static final String AES_SECRET_KEY = "_@$4l@audV+F5kTR";
    private static final String AES_IV = "0102030405060708090A0B0C0D0E0F10";
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;

    public void handleRememberMe(boolean isChecked, String txtEmail, String txtPass, Context c){
        if(isChecked){
            SharedPreferences sharedPreferences = c.getSharedPreferences("checkbox", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("remember", "true");
            editor.putString("email", txtEmail);
            editor.putString("password", txtPass);
            editor.apply();
        } else{
            SharedPreferences sharedPreferences = c.getSharedPreferences("checkbox", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("remember", "false");
            editor.apply();
        }
    }

    public void getSerial(String uid, final getSerialInterface callback){
        databaseReference = firebaseDatabase.getReference("Users");
        databaseReference.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot dataSnapshot = task.getResult();
                String serial = dataSnapshot.child("serial").getValue(String.class);
                callback.onSerialFetched(serial);
            }
        });
    }

    public interface getSerialInterface{
        void onSerialFetched(String serial);
    }

    public void getGas(String uid, final getGasInterface callback){
        getSerial(uid, new getSerialInterface() {
            @Override
            public void onSerialFetched(String serial) {
                databaseReference = firebaseDatabase.getReference("Devices");
                databaseReference.child(serial).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        DataSnapshot dataSnapshot = task.getResult();
                        int gasLevel = dataSnapshot.child("gas").getValue(Integer.class);
                        callback.onGasFetched(gasLevel);
                    }
                });
            }
        });
    }

    public interface getGasInterface{
        void onGasFetched(int gas);
    }

    public boolean isValidBirthday(String birthday) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false); // This will make the parsing strict

        try {
            // Attempt to parse the input string as a date
            Date parsedDate = dateFormat.parse(birthday);

            // Check if the parsed date matches the input string
            // If the input string is not a valid date, this check will fail
            if (!birthday.equals(dateFormat.format(parsedDate))) {
                return false;
            }

            // Optionally, you can add more checks based on your requirements
            // For example, you might want to check if the date is not in the future

            // Return true if all checks pass
            return true;
        } catch (ParseException e) {
            // If parsing fails, the input is not a valid date
            return false;
        }
    }

    public String encrypt(String data) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(AES_SECRET_KEY.getBytes(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(hexStringToByteArray(AES_IV));
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e("Encryption error", e.getMessage());
            return "error";
        }
    }



    private static byte[] hexStringToByteArray(String hexString) {
        int length = hexString.length();
        byte[] data = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i+1), 16));
        }
        return data;
    }

    public String decrypt(String encryptedData){
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(AES_SECRET_KEY.getBytes(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(AES_IV.getBytes());

            byte[] ivBytes = hexStringToByteArray(AES_IV);

            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encryptedBytes = Base64.decode(encryptedData, Base64.DEFAULT);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e){
            Log.e("Decryption error", e.getMessage());
            return "error";
        }
    }

    public String decrypt1(String encryptedData) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(AES_SECRET_KEY.getBytes(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(AES_IV.getBytes());
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encryptedBytes = Base64.decode(encryptedData, Base64.DEFAULT);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            Log.e("Decryption error", e.getMessage());
            return "error";
        }
    }

}
