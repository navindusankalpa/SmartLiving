package com.example.smartliving.HomeControlDashBoard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.smartliving.FireBase.Dashboard;
import com.example.smartliving.FireBase.FireBaseHandler;
import com.example.smartliving.R;
import com.google.android.material.textfield.TextInputEditText;

public class GetHelpActivity extends AppCompatActivity {
    TextInputEditText title, message;
    String txtMessage, txtTitle, sid;
    Button btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_help);

        btnContinue = findViewById(R.id.get_help_btn_continue);
        title = findViewById(R.id.get_help_title);
        message = findViewById(R.id.get_help_message);
        sid = new FireBaseHandler().getUid();

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtMessage = message.getText().toString().trim();
                txtTitle = title.getText().toString().trim();

                if (!txtMessage.isEmpty() || !txtTitle.isEmpty()){
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"anjana.deneth155@gmail.com"});
                    intent.putExtra(Intent.EXTRA_SUBJECT, txtTitle + " | " + sid);
                    intent.putExtra(Intent.EXTRA_TEXT, txtMessage);
                    intent.setType("message/rfc822");
                    startActivity(Intent.createChooser(intent, "Choose an email client"));

                } else{
                    Toast.makeText(GetHelpActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}