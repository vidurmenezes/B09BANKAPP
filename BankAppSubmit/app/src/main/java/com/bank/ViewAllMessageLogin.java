package com.bank;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.vidur.bankapp.R;

public class ViewAllMessageLogin extends AppCompatActivity {
  String strUsernum;
  String adminId;
  boolean useridauthen;
  Context context;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_all_message_login);
    Intent i = getIntent();
    adminId = i.getStringExtra("name");
    context = this.getApplicationContext();
    Button submit = (Button) findViewById(R.id.CustomerSubmit);
    submit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        if (useridauthen1(context)) {
          Intent i = new Intent(ViewAllMessageLogin.this, ViewAllMessage.class);
          i.putExtra("id", strUsernum);
          i.putExtra("name", adminId);
          startActivity(i);
        }
      }
    });

    Button Exit = (Button) findViewById(R.id.Exit);
    Exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(ViewAllMessageLogin.this, MessageOptions.class);
        startActivity(i);
      }
    });
  }

  private boolean useridauthen1(Context context) {
    EditText etUserName = (EditText) findViewById(R.id.CustomerId);
    strUsernum = etUserName.getText().toString();
    useridauthen = true;
    if (TextUtils.isEmpty(strUsernum)) {
      etUserName.setError("No UserId");
      useridauthen = false;
    }
    return useridauthen;
  }
}
