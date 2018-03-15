package com.bank;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.bank.account.Account;
import com.bank.database.DatabaseSelectHelper;
import com.bank.terminals.ATM;
import com.example.vidur.bankapp.R;

import java.util.ArrayList;
import java.util.Iterator;

public class BalanceOfAccountsAdmin extends AppCompatActivity {
  ArrayList<String> accountBalance = new ArrayList<>();
  Context context;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_balance_of_accounts_admin);
    this.getApplicationContext();
    accountBalance = getIntent().getStringArrayListExtra("balances");
    ArrayAdapter<String> listAdapter;
    ListView mainListView;

    mainListView = (ListView) findViewById(R.id.mainListView);

    listAdapter = new ArrayAdapter<>(this, R.layout.simplerow, accountBalance);

    mainListView.setAdapter(listAdapter);

    Button exit = (Button) findViewById(R.id.Exit1);
    exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        Intent i = new Intent(BalanceOfAccountsAdmin.this, SecondActivity.class);
        i.putStringArrayListExtra("balances", accountBalance);
        startActivity(i);
      }
    });
  }
}
