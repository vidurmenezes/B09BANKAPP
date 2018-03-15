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
import com.bank.terminals.ATM;
import com.example.vidur.bankapp.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MakeDepositCustomer extends AppCompatActivity {

  Context context;
  String accountName;
  String amount;
  ATM atm = null;
  ArrayList<String> accountBalance = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_make_deposit_customer);
    context = this.getApplication();
    atm = (ATM) getIntent().getSerializableExtra("atm");
    accountBalance = getIntent().getStringArrayListExtra("balances");

    Button button = (Button) findViewById(R.id.depositsubmitcustomer);
    button.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        if (useridauthen1(context)) {
          Intent i = new Intent(MakeDepositCustomer.this, Customerlogin.class);
          i.putExtra("atm", atm);
          i.putStringArrayListExtra("balances", accountBalance);
          startActivity(i);
        }
      }
    });

    Button exit = (Button) findViewById(R.id.Exit);
    exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(MakeDepositCustomer.this, Customerlogin.class);
        i.putExtra("atm", atm);
        i.putStringArrayListExtra("balances", accountBalance);
        startActivity(i);
      }
    });
  }

  private boolean useridauthen1(Context context) {
    EditText etName = (EditText) findViewById(R.id.nameOfAccountDeposit);
    accountName = etName.getText().toString();
    EditText etAmount = (EditText) findViewById(R.id.amountDepositCustomer);
    amount = etAmount.getText().toString();
    boolean useridauthen = true;
    boolean passwordauthen = true;
    if (TextUtils.isEmpty(accountName)) {
      etName.setError("No Account Name");
      useridauthen = false;
    }
    if (TextUtils.isEmpty(amount)) {
      etAmount.setError("No amount");
      passwordauthen = false;
    }
    boolean passed = false;
    if (passwordauthen && useridauthen) {
      passed = runDeposit(context);
    }
    return (passed);
  }

  private boolean runDeposit(Context context) {
    EditText etName = (EditText) findViewById(R.id.nameOfAccountDeposit);
    accountName = etName.getText().toString();
    EditText etAmount = (EditText) findViewById(R.id.amountDepositCustomer);
    amount = etAmount.getText().toString();
    boolean success = false;
    try {
      BigDecimal amnt = new BigDecimal(amount);
      List<Account> listAcc = new ArrayList<>();
      listAcc = atm.listAccounts(context);
      for (int i = 0; i < listAcc.size(); i++) {
        if (accountName.equals(listAcc.get(i).getName())) {
          int accId = listAcc.get(i).getId();
          success = atm.makeDeposit(amnt, accId, context);
        }
      }
    } catch (NoAccessToAccountException e) {
      etAmount.setError("You do not have access to this account");
    } catch (NumberFormatException e) {
      etAmount.setError("Please Enter a Number");
    }
    if (success) {
      Toast.makeText(getApplicationContext(), "Deposit was made.", Toast.LENGTH_LONG).show();
    } else {
      Toast.makeText(getApplicationContext(), "Deposit was unsuccessful.(POSSIBLE: Wrong " +
              "accountName)", Toast.LENGTH_LONG).show();
    }
    return success;
  }

}
