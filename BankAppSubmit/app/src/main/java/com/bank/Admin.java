package com.bank;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.vidur.bankapp.R;

import java.util.ArrayList;

public class Admin extends AppCompatActivity {
  String adminId;
  ArrayList<String> accountBalance = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_adminmain);
    Intent i = getIntent();
    adminId = i.getStringExtra("name");
    accountBalance = getIntent().getStringArrayListExtra("balances");
    Button createAdmin = (Button) findViewById(R.id.createAdmin);
    createAdmin.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(Admin.this, NewAdmin.class);
        startActivity(i);
      }
    });

    Button createTeller = (Button) findViewById(R.id.createTeller);
    createTeller.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(Admin.this, NewTeller.class);
        startActivity(i);
      }
    });
    Button viewAdmin = (Button) findViewById(R.id.viewAdmins);
    viewAdmin.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(Admin.this, CurrentAdmins.class);
        startActivity(i);
      }
    });
    Button viewTeller = (Button) findViewById(R.id.viewTellers);
    viewTeller.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(Admin.this, CurrentTellers.class);
        startActivity(i);
      }
    });
    Button viewCustomers = (Button) findViewById(R.id.viewCustomers);
    viewCustomers.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(Admin.this, CurrentCustomers.class);
        startActivity(i);
      }
    });
    Button amountinbank = (Button) findViewById(R.id.totalaccount);
    amountinbank.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(Admin.this, TotalBalanceAdmin.class);
        startActivity(i);
      }
    });

    Button database = (Button) findViewById(R.id.database);
    database.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(Admin.this, DatabaseOptions.class);
        startActivity(i);
      }
    });

    Button promoteteller = (Button) findViewById(R.id.promote);
    promoteteller.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(Admin.this, PromoteTeller.class);
        startActivity(i);
      }
    });

    Button messages = (Button) findViewById(R.id.messages);
    messages.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(Admin.this, MessageOptions.class);
        i.putExtra("name", adminId);
        startActivity(i);
      }
    });

    Button allAccountsBalance = (Button) findViewById(R.id.allAccountsBalance);
    allAccountsBalance.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(Admin.this, BalanceOfAccountsAdmin.class);
        i.putStringArrayListExtra("balances", accountBalance);
        startActivity(i);
      }
    });

    Button exit = (Button) findViewById(R.id.exit);
    exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(Admin.this, MainActivity.class);
        startActivity(i);
      }
    });

  }
}
