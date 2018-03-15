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

import com.bank.account.Account;
import com.bank.exceptions.NoAccessToAccountException;
import com.bank.terminals.TellerTerminal;
import com.example.vidur.bankapp.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Interest extends AppCompatActivity {
  Context context;
  String accountName;
  String amount;
  TellerTerminal teller = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_interest);
    context = this.getApplication();

    teller = (TellerTerminal) getIntent().getSerializableExtra("terminal");


    Button button = (Button) findViewById(R.id.userTransferSubmit);
    button.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        if (useridauthen1(context)) {
          Intent i = new Intent(Interest.this, TellerOptions.class);
          i.putExtra("teller", teller);
          startActivity(i);
        }
      }
    });

    Button exit = (Button) findViewById(R.id.Exit);
    exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(Interest.this, TellerOptions.class);
        i.putExtra("teller", teller);
        startActivity(i);
      }
    });
  }

  private boolean useridauthen1(Context context) {
    EditText etName = (EditText) findViewById(R.id.nameOfAccount2);
    accountName = etName.getText().toString();
    boolean useridauthen = true;
    boolean passwordauthen = true;

    if (TextUtils.isEmpty(accountName)) {
      etName.setError("No Account Name");
      useridauthen = false;
    }

    boolean passed = false;
    if (useridauthen) {
      passed = runDeposit(context);

    }
    return (passed);
  }
  private boolean runDeposit(Context context) {
    int accId = 0;
    EditText etName = (EditText) findViewById(R.id.nameOfAccount2);
    accountName = etName.getText().toString();

    boolean success = false;
    //EnumMapAccountTypes accountsMap = new EnumMapAccountTypes(context);
    try {
      List<Account> listAcc = new ArrayList<>();
      listAcc = teller.listAccounts(context);
      // Get the id of the inputed account name and make deposit
      // of given amount
      for (int i = 0; i < listAcc.size(); i++) {
        if (accountName.equals(listAcc.get(i).getName())) {
          accId = listAcc.get(i).getId();
        }
      }
      teller.giveInterest(accId, context);
      Toast.makeText(getApplicationContext(), "Interest was given.", Toast.LENGTH_LONG).show();
      success = true;

    } catch (NoAccessToAccountException e) {
      etName.setError("You do not have access to this account");
    }
    if (!success) {

      Toast.makeText(getApplicationContext(), "Interest given was unsuccessful.(POSSIBLE: Wrong " +
              "accountName)", Toast.LENGTH_LONG).show();
    }
    return success;
  }


}
