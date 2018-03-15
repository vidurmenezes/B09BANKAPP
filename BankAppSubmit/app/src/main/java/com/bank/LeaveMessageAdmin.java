package com.bank;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bank.database.DatabaseInsertHelper;
import com.example.vidur.bankapp.R;

public class LeaveMessageAdmin extends AppCompatActivity {

  Context context;
  int id;
  String message;
  String adminId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_leave_message_admin);
    Button button = (Button) findViewById(R.id.MesagesSubmit);
    context = this.getApplicationContext();
    adminId = getIntent().getStringExtra("name");
    button.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        if (useridauthen1(context)) {
          Intent i = new Intent(LeaveMessageAdmin.this, MessageOptions.class);
          i.putExtra("name", adminId);
          startActivity(i);
        }
      }
    });
    Button exit = (Button) findViewById(R.id.Exit);
    exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(LeaveMessageAdmin.this, MessageOptions.class);
        i.putExtra("name", adminId);
        startActivity(i);
      }
    });
  }

  private boolean useridauthen1(Context context) {
    EditText etUserName = (EditText) findViewById(R.id.UserIdMessage);
    id = Integer.valueOf(etUserName.getText().toString());
    EditText etUserName2 = (EditText) findViewById(R.id.MessageInsert);
    message = etUserName2.getText().toString();
    boolean useridauthen = true;
    boolean messageauthen = true;
    if (TextUtils.isEmpty(String.valueOf(id))) {
      etUserName.setError("No UserId");
      useridauthen = false;
    }
    if (TextUtils.isEmpty(message)) {
      etUserName2.setError("No message");
      messageauthen = false;
    }
    boolean passed = false;
    if (messageauthen && useridauthen) {
      passed = runLeave(context);
    }
    return (passed);
  }

  private boolean runLeave(Context context) {
    boolean pass = false;
    int messageId = DatabaseInsertHelper.insertMessage(id, message, context);
    if (messageId != -1) {
      Toast.makeText(getApplicationContext(), "Message inserted, the message Id is: " +
          String.valueOf(messageId), Toast.LENGTH_LONG).show();
      pass = true;
    } else {
      Toast.makeText(getApplicationContext(), "Message was not Inserted", Toast.LENGTH_LONG).show();
      pass = false;
    }
    return pass;
  }
}
