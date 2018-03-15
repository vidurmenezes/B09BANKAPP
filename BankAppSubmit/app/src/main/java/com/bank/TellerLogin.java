package com.bank;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bank.exceptions.InvalidIdException;
import com.bank.terminals.TellerTerminal;
import com.example.vidur.bankapp.R;
import com.bank.database.DatabaseSelectHelper;
import com.bank.generics.EnumMapRoles;
import com.bank.generics.Roles;
import com.bank.user.User;

public class TellerLogin extends AppCompatActivity {
  String strUsernum;
  String strUserName2;
  TextView txtView;
  Context context;
  int id = -1;
  TellerTerminal terminal = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_teller_login);
    Button exit = (Button) findViewById(R.id.Exit);
    exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(TellerLogin.this, MainActivity.class);
        startActivity(i);
      }
    });

    Button tellersubmit = (Button) findViewById(R.id.TellerSubmit);
    context = this.getApplicationContext();
    tellersubmit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        if (useridauthen1(context)) {
          Intent i = new Intent(TellerLogin.this, TELLERmain.class);
          i.putExtra("name", strUsernum);
          i.putExtra("password", strUserName2);
          i.putExtra("terminal", terminal);
          i.putExtra("id", id);
          startActivity(i);
        }
      }
    });

  }

  public boolean useridauthen1(Context context) {
    EditText etUserName = (EditText) findViewById(R.id.TellerId);
    strUsernum = etUserName.getText().toString();
    EditText etUserName2 = (EditText) findViewById(R.id.TellerPassword);
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
      passed = runTeller(context);
    }
    return (passed);
  }

  private boolean runTeller(Context context) {
    String pass;
    EditText etUserName = (EditText) findViewById(R.id.TellerId);
    id = Integer.valueOf(etUserName.getText().toString());
    //Toast.makeText(getApplicationContext(), String.valueOf(id), Toast.LENGTH_LONG).show();
    EditText etUserName2 = (EditText) findViewById(R.id.TellerPassword);
    pass = etUserName2.getText().toString();
    // Create a map of all roles in the database
    EnumMapRoles roleMap = new EnumMapRoles(context);
    // Run TELLER mode
    User user = DatabaseSelectHelper.getUserDetails(id, context);
    if ((user != null) && (user.getRoleId() == roleMap.get(Roles.TELLER))) {
      // Authenticate inputed password
      com.bank.user.Teller teller = (com.bank.user.Teller) DatabaseSelectHelper.getUserDetails
          (id, context);
      boolean passed = false;
      terminal = new TellerTerminal(id, pass, context);
      passed = terminal.authenticateTeller(id, pass, context);
      if (passed) {
        // Allow TELLER to run the teller interface
        return true;
      } else {
        etUserName2.setError("Invalid password");
      }
    } else {
      etUserName.setError("This is not a valid Teller Id");
    }
    return false;
  }

  public String getUserString() {
    return this.strUsernum;
  }

}
