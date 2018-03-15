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

import com.bank.account.Account;
import com.bank.exceptions.NoAccessToAccountException;
import com.bank.terminals.ATM;
import com.example.vidur.bankapp.R;
import com.bank.database.DatabaseSelectHelper;
import com.bank.generics.EnumMapRoles;
import com.bank.generics.Roles;
import com.bank.user.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Customerlogin extends AppCompatActivity {
  String strUsernum;
  String strUserName2;
  TextView txtView;
  Context context;
  ATM atm = null;
  int id = -1;
  ArrayList<String> accountBalance = new ArrayList<>();
  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_customerlogin);
    Button customersubmit = (Button) findViewById(R.id.CustomerSubmit);
    context = this.getApplicationContext();
    customersubmit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        if (useridauthen1(context)) {
          Intent i = new Intent(Customerlogin.this, customermain2.class);
          i.putExtra("name", strUsernum);
          i.putExtra("password", strUserName2);
          i.putStringArrayListExtra("balances", accountBalance);
          i.putExtra("atm", atm);
          i.putExtra("customerId", id);
          startActivity(i);
        }
      }
    });
    Button exit = (Button) findViewById(R.id.Exit);
    exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        Intent i = new Intent(Customerlogin.this, MainActivity.class);
        startActivity(i);
      }
    });
  }

  private boolean useridauthen1(Context context) {
    EditText etUserName = (EditText) findViewById(R.id.CustomerId);
    strUsernum = etUserName.getText().toString();
    id = Integer.valueOf(strUsernum);
    EditText etUserName2 = (EditText) findViewById(R.id.CustomerPassword);
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
      passed = runCustomer(context);
    }
    return (passed);
  }

  private boolean runCustomer(Context context) {
    String pass;
    EditText etUserName = (EditText) findViewById(R.id.CustomerId);
    id = Integer.valueOf(etUserName.getText().toString());
    //Toast.makeText(getApplicationContext(), String.valueOf(id), Toast.LENGTH_LONG).show();
    EditText etUserName2 = (EditText) findViewById(R.id.CustomerPassword);
    pass = etUserName2.getText().toString();
    // Create a map of all roles in the database
    EnumMapRoles roleMap = new EnumMapRoles(context);
    // Run CUSTOMER mode
    User user = DatabaseSelectHelper.getUserDetails(id, context);
    if ((user != null) && (user.getRoleId() == roleMap.get(Roles.CUSTOMER))) {
      // Authenticate inputed password
      com.bank.user.Customer customer = (com.bank.user.Customer) DatabaseSelectHelper.getUserDetails
          (id, context);
      atm = new ATM(id, context);
      boolean passed = atm.authenticate(id, pass, context);

      if (passed) {
        List<Account> listAcc = new ArrayList<>();
        try {
          listAcc = atm.listAccounts(context);
        } catch (NoAccessToAccountException e) {
          e.printStackTrace();
        }
        for (int i = 0; i < listAcc.size(); i++) {
          accountBalance.add("Account Name: " + listAcc.get(i).getName() +
                  " , Balance: " + listAcc.get(i).getBalance().toPlainString());
        }
        // Allow CUSTOMER to run the customer interface
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

