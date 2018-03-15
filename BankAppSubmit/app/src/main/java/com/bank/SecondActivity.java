package com.bank;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bank.database.DatabaseSelectHelper;
import com.bank.generics.EnumMapRoles;
import com.bank.generics.Roles;
import com.bank.user.User;
import com.example.vidur.bankapp.R;

public class SecondActivity extends AppCompatActivity {
  String strUsernum;
  String strUserName2;
  TextView txtView;
  Context context;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_second);
    context = this.getApplicationContext();

    Button button = (Button) findViewById(R.id.enter);
    button.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        if (useridauthen1(context)) {
          Intent i = new Intent(SecondActivity.this, Admin.class);
          i.putExtra("name", strUsernum);
          i.putExtra("password", strUserName2);
          startActivity(i);
        }
      }
    });
    Button exit = (Button) findViewById(R.id.Exit);
    exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(SecondActivity.this, MainActivity.class);
        startActivity(i);
      }
    });

  }

  private boolean useridauthen1(Context context) {
    EditText etUserName = (EditText) findViewById(R.id.editText5);
    strUsernum = etUserName.getText().toString();
    EditText etUserName2 = (EditText) findViewById(R.id.editText4);
    strUserName2 = etUserName2.getText().toString();
    boolean useridauthen = true;
    boolean passwordauthen = true;
    if (TextUtils.isEmpty(strUsernum)) {
      etUserName.setError("No UserId");
      useridauthen = false;
    }
    if (TextUtils.isEmpty(strUserName2)) {
      etUserName2.setError("No password");
      passwordauthen = false;
    }
    boolean passed = false;
    if (passwordauthen && useridauthen) {
      passed = runAdmin(context);
    }
    return (passed);
  }

  private boolean runAdmin(Context context) {
    int id = 0;
    String pass;
    EditText etUserName = (EditText) findViewById(R.id.editText5);
    id = Integer.valueOf(etUserName.getText().toString());
    //Toast.makeText(getApplicationContext(), String.valueOf(id), Toast.LENGTH_LONG).show();
    EditText etUserName2 = (EditText) findViewById(R.id.editText4);
    pass = etUserName2.getText().toString();
    // Create a map of all roles in the database
    EnumMapRoles roleMap = new EnumMapRoles(context);
    // Run ADMIN mode
    User user = DatabaseSelectHelper.getUserDetails(id, context);
    //Toast.makeText(getApplicationContext(), String.valueOf(user), Toast.LENGTH_LONG).show();
    //(user.getRoleId() == roleMap.get(Roles.ADMIN))
    if ((user != null) && (user.getRoleId() == roleMap.get(Roles.ADMIN))) {
      // Authenticate inputed password
      com.bank.user.Admin admin = (com.bank.user.Admin) DatabaseSelectHelper.getUserDetails
          (id, context);
      boolean passed = admin.authenticate(pass, context);
      if (passed) {
        // Allow ADMIN to run the admin interface
        return true;
      } else {
        etUserName2.setError("Invalid password");
      }
    } else {
      etUserName2.setError("This is not a valid Admin Id");
    }
    return false;
  }

  public String getUserString() {
    return this.strUsernum;
  }
}

