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

import com.bank.terminals.TellerTerminal;
import com.example.vidur.bankapp.R;
import com.bank.database.DatabaseSelectHelper;
import com.bank.generics.EnumMapRoles;
import com.bank.generics.Roles;
import com.bank.user.User;

public class TellerCustomerLogin extends AppCompatActivity {
  String strUsernum;
  String strUserName2;
  TextView txtView;
  Context context;
  TellerTerminal terminal = null;
  int id =-1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_teller_customer_login);
    Button tellercustomersubmit = (Button) findViewById(R.id.CustomerTellerSubmit);
    context = this.getApplicationContext();
    terminal = (TellerTerminal) getIntent().getSerializableExtra("terminal");

    tellercustomersubmit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        if (useridauthen1(context)) {
          Intent i = new Intent(TellerCustomerLogin.this, TellerOptions.class);
          i.putExtra("terminal", terminal);
          i.putExtra("name", strUsernum);
          i.putExtra("password", strUserName2);
          i.putExtra("customerId", id);
          startActivity(i);
        }
      }
    });
    Button exit = (Button) findViewById(R.id.Exit);
    exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(TellerCustomerLogin.this, TELLERmain.class);
        startActivity(i);
      }
    });
  }

  private boolean useridauthen1(Context context) {
    EditText etUserName = (EditText) findViewById(R.id.CustomerTeller);
    strUsernum = etUserName.getText().toString();
    EditText etUserName2 = (EditText) findViewById(R.id.CustomerTellerPassword);
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
      passed = runCustomerTeller(context);
    }
    return (passed);
  }

  private boolean runCustomerTeller(Context context) {
    String pass;
    EditText etUserName = (EditText) findViewById(R.id.CustomerTeller);
    id = Integer.valueOf(etUserName.getText().toString());
    //Toast.makeText(getApplicationContext(), String.valueOf(id), Toast.LENGTH_LONG).show();
    EditText etUserName2 = (EditText) findViewById(R.id.CustomerTellerPassword);
    pass = etUserName2.getText().toString();
    // Create a map of all roles in the database
    EnumMapRoles roleMap = new EnumMapRoles(context);
    // Run CUSTOMER mode
    User user = DatabaseSelectHelper.getUserDetails(id, context);
    if ((user != null) && (user.getRoleId() == roleMap.get(Roles.CUSTOMER))) {
      // Authenticate inputed password
      com.bank.user.Customer customer = (com.bank.user.Customer) DatabaseSelectHelper.getUserDetails
          (id, context);
      boolean passed = customer.authenticate(pass, context);
      if (passed) {
        // Allow CUSTOMER to run the customer interface
        terminal.setCurrentCustomer(customer);
        terminal.authenticateCurrentCustomer(pass, context);
        return true;
      } else {
        etUserName2.setError("Invalid password");
      }
    } else {
      etUserName.setError("This is not a valid Customer Id");
    }
    return false;
  }

  public String getUserString() {
    return this.strUsernum;
  }
}

