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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CheckBalanceCustomer extends AppCompatActivity {
  private String accountName;
  private TextView box;
  Context context;
  ATM atm = null;
  ArrayList<String> accountBalance = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_check_balance_customer);
    context = this.getApplicationContext();
    atm = (ATM) getIntent().getSerializableExtra("atm");
    accountBalance = getIntent().getStringArrayListExtra("balances");

    EditText etUserName = (EditText) findViewById(R.id.idofaccount);
    accountName = etUserName.getText().toString();

    Button button = (Button) findViewById(R.id.nameofaccountsubmit);
    box = (TextView) findViewById(R.id.balanceCustomer);
    button.setOnClickListener(new View.OnClickListener() {


      public void onClick(View view) {
        if (useridauthen1()) {
          boolean contains = false;
          BigDecimal balance = null;
          List<Account> listAcc = new ArrayList<>();
          try {
            listAcc = atm.listAccounts(context);
            for (int i = 0; i < listAcc.size(); i++) {
              if (accountName.equals(listAcc.get(i).getName())) {
                int accId = listAcc.get(i).getId();
                balance = atm.checkBalance(accId, context);
                contains = true;
              }
            }

          } catch (NoAccessToAccountException e) {
          }
          if (contains) {
            box.setText("$ " + balance.toPlainString());
            Toast.makeText(getApplicationContext(),"Close the keyboard", Toast.LENGTH_LONG).show();
          } else {
            Toast.makeText(getApplicationContext(),"Account Name does not exist", Toast.LENGTH_LONG).show();
          }

        }
      }
    });

    Button exit = (Button) findViewById(R.id.Exit);
    exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(CheckBalanceCustomer.this, Customerlogin.class);
        i.putExtra("atm", atm);
        i.putStringArrayListExtra("balances", accountBalance);
        startActivity(i);
      }
    });


  }

  public boolean useridauthen1() {
    EditText etUserName = (EditText) findViewById(R.id.idofaccount);
    accountName = etUserName.getText().toString();

    boolean useridauthen = true;
    if (TextUtils.isEmpty(accountName)) {
      etUserName.setError("No Account Name");
      useridauthen = false;
    }

    return (useridauthen);
  }
}
