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
import com.bank.generics.EnumMapAccountTypes;
import com.bank.terminals.TellerTerminal;
import com.example.vidur.bankapp.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CheckBalance extends AppCompatActivity {
  Context context;
  String accountName;
  String amount;
  BigDecimal balance = BigDecimal.valueOf(0);
  TellerTerminal teller = null;
  ArrayList<String> accountBalance = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_check_balance);
    context = this.getApplication();


    teller = (TellerTerminal) getIntent().getSerializableExtra("terminal");
    //accountBalance = getIntent().getStringArrayListExtra("balances");

    Button button = (Button) findViewById(R.id.nameofaccountsubmit);
    button.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        if (useridauthen1(context)) {
          Intent i = new Intent(CheckBalance.this, CheckBalance.class);
          //textViewToChange.setText("$"+ balance.toString());
          i.putExtra("teller", teller);

        }
      }
    });

    Button exit = (Button) findViewById(R.id.Exit);
    exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(CheckBalance.this, TellerOptions.class);
        i.putExtra("teller", teller);
        startActivity(i);
      }
    });
  }

  private boolean useridauthen1(Context context) {
    EditText etName = (EditText) findViewById(R.id.idofaccount);
    accountName = etName.getText().toString();
    boolean useridauthen = true;

    if (TextUtils.isEmpty(accountName)) {
      etName.setError("No Account Name");
      useridauthen = false;
    }

    boolean passed = false;
    if (useridauthen) {
      passed = runCheckBalance(context);

    }
    return (passed);
  }

  private boolean runCheckBalance(Context context) {
    EditText etName = (EditText) findViewById(R.id.idofaccount);
    accountName = etName.getText().toString();

    TextView textViewToChange = (TextView) findViewById(R.id.amountmoney);
    boolean success = false;

    try {
      List<Account> listAcc = new ArrayList<>();
      listAcc = teller.listAccounts(context);

      for (int i = 0; i < listAcc.size(); i++) {
        if (accountName.equals(listAcc.get(i).getName())) {
          int accId = listAcc.get(i).getId();
          balance = teller.checkBalance(accId, context);
          textViewToChange.setText(balance.toString());
          success = true;
        }

      }

    } catch (NoAccessToAccountException e) {
      e.printStackTrace();
    }

    if (!success) {
      Toast.makeText(getApplicationContext(), "NOT ABLE TO VIEW TOTAL BALANCE, WRONG ACCOUNT NAME", Toast.LENGTH_LONG).show();
      etName.setError("ERROR WRONG NAME");
    }
    return success;
  }
}