package com.bank;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bank.terminals.ATM;
import com.bank.terminals.TellerTerminal;
import com.bank.user.User;
import com.example.vidur.bankapp.R;

public class TransferOptions extends AppCompatActivity {

  TellerTerminal terminal = null;
  ATM atm = null;
  boolean fromTeller;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_transfer_options);
    terminal = (TellerTerminal) getIntent().getSerializableExtra("terminal");
    atm = (ATM) getIntent().getSerializableExtra("atm");
    fromTeller = getIntent().getBooleanExtra("fromTeller", fromTeller);

    Button Exit = (Button) findViewById(R.id.Exit);
    Exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(TransferOptions.this, TellerOptions.class);
        i.putExtra("terminal", terminal);
        i.putExtra("atm", atm);
        i.putExtra("fromTeller", fromTeller);
        startActivity(i);
      }
    });

    Button AccountTransfer = (Button) findViewById(R.id.accountTransfer);
    AccountTransfer.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(TransferOptions.this, AccountTransfer.class);
        i.putExtra("terminal", terminal);
        i.putExtra("atm", atm);
        i.putExtra("fromTeller", fromTeller);
        startActivity(i);
      }
    });

    Button UserTransfer = (Button) findViewById(R.id.transferToUser);
    UserTransfer.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(TransferOptions.this, UserTransfer.class);
        i.putExtra("terminal", terminal);
        i.putExtra("atm", atm);
        i.putExtra("fromTeller", fromTeller);
        startActivity(i);
      }
    });

    Button ReceiveTransfer = (Button) findViewById(R.id.receiveTransfer);
    ReceiveTransfer.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(TransferOptions.this, ReceiveTransfer.class);
        i.putExtra("terminal", terminal);
        i.putExtra("atm", atm);
        i.putExtra("fromTeller", fromTeller);
        startActivity(i);
      }
    });
  }
}
