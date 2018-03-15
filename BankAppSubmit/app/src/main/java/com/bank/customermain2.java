package com.bank;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bank.terminals.ATM;
import com.example.vidur.bankapp.R;

import java.io.Serializable;
import java.util.ArrayList;

public class customermain2 extends AppCompatActivity {

  ArrayList<String> accountBalance = new ArrayList<>();
  ATM atm = null;
  int id;
  boolean fromTeller = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_customermain2);

    accountBalance = getIntent().getStringArrayListExtra("balances");
    atm = (ATM) getIntent().getSerializableExtra("atm");
    id = getIntent().getIntExtra("customerId", id);
    Button exit = (Button) findViewById(R.id.exit);

    exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        Intent i = new Intent(customermain2.this, MainActivity.class);
        startActivity(i);
      }
    });

    Button listaccountandbalance = (Button) findViewById(R.id.listAccountsBalances);

    listaccountandbalance.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        Intent i = new Intent(customermain2.this, ListAccountAndBalances.class);
        i.putStringArrayListExtra("balances", accountBalance);
        i.putExtra("atm", atm);
        startActivity(i);
      }
    });

    Button deposit = (Button) findViewById(R.id.makeDeposit);
    deposit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        Intent i = new Intent(customermain2.this, MakeDepositCustomer.class);
        i.putStringArrayListExtra("balances", accountBalance);
        i.putExtra("atm", atm);
        startActivity(i);
      }
    });

    Button checkbalance = (Button) findViewById(R.id.checkBalance);
    checkbalance.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        Intent i = new Intent(customermain2.this, CheckBalanceCustomer.class);
        i.putStringArrayListExtra("balances", accountBalance);
        i.putExtra("atm", atm);
        startActivity(i);
      }
    });

    Button withdrawlCustomer = (Button) findViewById(R.id.makeWithdrawlCustomer);
    withdrawlCustomer.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        Intent i = new Intent(customermain2.this, MakeWithdrawlCustomer.class);
        i.putStringArrayListExtra("balances", accountBalance);
        i.putExtra("atm", atm);
        startActivity(i);
      }
    });

    Button messages = (Button) findViewById(R.id.Messages);
    messages.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        Intent i = new Intent(customermain2.this, ViewAllMessagesCustomer.class);
        i.putStringArrayListExtra("balances", accountBalance);
        i.putExtra("customerId", String.valueOf(id));
        i.putExtra("atm", atm);
        startActivity(i);
      }
    });

    Button transfer = (Button) findViewById(R.id.transferFromCustomer);

    transfer.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        Intent i = new Intent(customermain2.this, TransferOptions.class);
        i.putStringArrayListExtra("balances", accountBalance);
        i.putExtra("atm", atm);
        i.putExtra("fromTeller", fromTeller);
        i.putExtra("customerId", id);
        startActivity(i);
      }
    });
  }


}
