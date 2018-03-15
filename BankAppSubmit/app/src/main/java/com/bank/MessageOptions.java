package com.bank;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.vidur.bankapp.R;

public class MessageOptions extends AppCompatActivity {

  String adminId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_message_options);
    Intent i = getIntent();
    adminId = i.getStringExtra("name");

    Button viewAll = (Button) findViewById(R.id.viewMessages);
    viewAll.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        Intent i = new Intent(MessageOptions.this, ViewAllMessageLogin.class);
        i.putExtra("name", adminId);
        startActivity(i);
      }
    });

    Button leave = (Button) findViewById(R.id.leaveMessages);
    leave.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        Intent i = new Intent(MessageOptions.this, LeaveMessageAdmin.class);
        i.putExtra("name", adminId);
        startActivity(i);
      }
    });

    Button exit = (Button) findViewById(R.id.exit);
    exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        Intent i = new Intent(MessageOptions.this, Admin.class);
        i.putExtra("name", adminId);
        startActivity(i);
      }
    });
  }
}
