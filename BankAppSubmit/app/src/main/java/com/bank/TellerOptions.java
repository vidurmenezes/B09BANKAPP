package com.bank;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bank.terminals.TellerTerminal;
import com.example.vidur.bankapp.R;

public class TellerOptions extends AppCompatActivity {

  TellerTerminal terminal = null;
  boolean fromTeller = true;
  int id = -1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_teller_options);

    terminal = (TellerTerminal) getIntent().getSerializableExtra("terminal");
    id = getIntent().getIntExtra("customerId", id);

    Button deposit = (Button) findViewById(R.id.makeDeposit);

    deposit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        //if(useridauthen1()) {
        Intent i = new Intent(TellerOptions.this, MakeDeposit.class);
        i.putExtra("terminal", terminal);
        startActivity(i);
        //}

      }
    });

    Button transfers = (Button) findViewById(R.id.transfers);

    transfers.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        //if(useridauthen1()) {
        Intent i = new Intent(TellerOptions.this, TransferOptions.class);
        i.putExtra("terminal", terminal);
        i.putExtra("fromTeller", fromTeller);
        i.putExtra("customerId", String.valueOf(id));
        startActivity(i);
        //}

      }
    });

    Button newAccountTeller = (Button) findViewById(R.id.newAccountTeller);
    newAccountTeller.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        //if(useridauthen1()) {
        Intent i = new Intent(TellerOptions.this, NewAccountTeller.class);
        i.putExtra("terminal", terminal);
        startActivity(i);
        //}

      }
    });

    Button withdrawl = (Button) findViewById(R.id.withdrawlTeller);

    withdrawl.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        //if(useridauthen1()) {
        Intent i = new Intent(TellerOptions.this, MakeWithdrawl.class);
        i.putExtra("terminal", terminal);
        startActivity(i);
        //}

      }
    });

    Button checkbalance = (Button) findViewById(R.id.checkBalance);

    checkbalance.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        //if(useridauthen1()) {
        Intent i = new Intent(TellerOptions.this, CheckBalance.class);
        i.putExtra("terminal", terminal);
        startActivity(i);
        //}

      }
    });

    Button interest = (Button) findViewById(R.id.giveInterest);

    interest.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        //if(useridauthen1()) {
        Intent i = new Intent(TellerOptions.this, Interest.class);
        i.putExtra("terminal", terminal);
        startActivity(i);
        //}

      }
    });

    Button totalbalance = (Button) findViewById(R.id.totalbalance);

    totalbalance.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        //if(useridauthen1()) {
        Intent i = new Intent(TellerOptions.this, TotalofAllaccountsUser.class);
        i.putExtra("terminal", terminal);
        startActivity(i);
        //}

      }
    });
    Button closesession = (Button) findViewById(R.id.closeCustomer);

    closesession.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        //if(useridauthen1()) {
        Intent i = new Intent(TellerOptions.this, TELLERmain.class);
        i.putExtra("terminal", terminal);
        startActivity(i);
        //}

      }
    });

    Button tellerMessages = (Button) findViewById(R.id.tellerMessages);
    tellerMessages.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(TellerOptions.this, MessageOptionsTeller.class);
        i.putExtra("terminal", terminal);
        i.putExtra("customerId", id);
        startActivity(i);
      }
    });

    Button Exit = (Button) findViewById(R.id.exit);
    Exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(TellerOptions.this, TELLERmain.class);
        i.putExtra("terminal", terminal);
        startActivity(i);
      }
    });
  }
}
