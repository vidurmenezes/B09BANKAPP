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
import com.bank.database.DatabaseSelectHelper;
import com.bank.exceptions.NoAccessToAccountException;
import com.bank.generics.EnumMapAccountTypes;
import com.bank.generics.EnumMapRoles;
import com.bank.generics.Roles;
import com.bank.terminals.TellerTerminal;
import com.bank.user.User;
import com.example.vidur.bankapp.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TotalofAllaccountsUser extends AppCompatActivity {

  String accountName;
  String amount;
  BigDecimal balance;
  TellerTerminal teller = null;
  int id;
  Context context;
  String name = "NA";
  String strUsernum;
  ArrayList<String> accountBalance = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_totalof_allaccounts_user);
    context = this.getApplication();

    Button tellersubmit = (Button) findViewById(R.id.nameofaccountsubmit);
    context = this.getApplicationContext();
    tellersubmit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        if (useridauthen1(context)) ;
        {
          Toast.makeText(getApplicationContext(), "Close the keyboard", Toast.LENGTH_LONG).show();
        }

      }
    });

    Button Exit = (Button) findViewById(R.id.Exit);
    Exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(TotalofAllaccountsUser.this, TellerOptions.class);
        startActivity(i);
      }
    });
  }

  public boolean useridauthen1(Context context) {

    EditText etUserName = (EditText) findViewById(R.id.idofaccount);
    strUsernum = etUserName.getText().toString();
    boolean useridauthen = true;
    if (TextUtils.isEmpty(strUsernum)) {
      etUserName.setError("No UserId");
      useridauthen = false;
    }
    boolean passed = false;
    if (useridauthen) {
      passed = true;
      getTotalUserBalance(context);
    }

    return (passed);
  }

  private void getTotalUserBalance(Context context) {
    EditText etUserID = (EditText) findViewById(R.id.idofaccount);
    id = Integer.valueOf(etUserID.getText().toString());
    TextView textViewToChange = (TextView) findViewById(R.id.amounttotalofaccounts);
    User customer = DatabaseSelectHelper.getUserDetails(id, context);

    EnumMapRoles roleMap = new EnumMapRoles(context);
    if (((customer != null) && (customer.getRoleId() == roleMap.get(Roles.CUSTOMER)))) {
      // If the id is of a customer
      BigDecimal total = BigDecimal.ZERO;
      List<Integer> accIds = DatabaseSelectHelper.getAccountIds(id, context);
      Account account = null;
      // Add the balance to total from each account
      for (Iterator<Integer> i = accIds.iterator(); i.hasNext(); ) {
        id = i.next();
        account = DatabaseSelectHelper.getAccountDetails(id, context);
        total = total.add(account.getBalance());
      }
      name = customer.getName();
      textViewToChange.setText("$" + total.toString());
      Toast.makeText(getApplicationContext(), "Total Balance for " + name, Toast.LENGTH_LONG).show();
      name = "NA";
      id = 0;

    } else {
      etUserID.setError("Invalid UserId");
      textViewToChange.setText("");
      id = 0;
    }
  }

}