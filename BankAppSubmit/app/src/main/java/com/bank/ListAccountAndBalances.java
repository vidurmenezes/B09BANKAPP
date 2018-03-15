package com.bank;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.bank.account.Account;
import com.bank.exceptions.NoAccessToAccountException;
import com.bank.terminals.ATM;
import com.example.vidur.bankapp.R;

import java.util.ArrayList;
import java.util.List;

public class ListAccountAndBalances extends AppCompatActivity {
  private ListView mainListView;
  private ArrayAdapter<String> listAdapter;
  Context context;
  ArrayList<String> accountBalance = new ArrayList<>();
  ATM atm = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_account_and_balances);
    accountBalance = getIntent().getStringArrayListExtra("balances");
    atm = (ATM) getIntent().getSerializableExtra("atm");
    this.getApplicationContext();

    mainListView = (ListView) findViewById(R.id.mainListView);

    listAdapter = new ArrayAdapter<>(this, R.layout.simplerow, accountBalance);

    mainListView.setAdapter(listAdapter);

    Button exit = (Button) findViewById(R.id.Exit1);
    exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        Intent i = new Intent(ListAccountAndBalances.this, Customerlogin.class);
        i.putStringArrayListExtra("balances", accountBalance);
        i.putExtra("atm", atm);
        startActivity(i);
      }
    });
  }
}
