package com.bank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bank.terminals.TellerTerminal;
import com.example.vidur.bankapp.R;


public class TELLERmain extends TellerLogin {

  TellerTerminal terminal = null;
  int id;
  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tellermain);

    Intent i = getIntent();
    terminal = (TellerTerminal) i.getSerializableExtra("terminal");
    id = i.getIntExtra("id", id);


    Button customerlogin = (Button) findViewById(R.id.tellertocustomerSubmit);
    customerlogin.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(TELLERmain.this, TellerCustomerLogin.class);
        i.putExtra("terminal", terminal);
        startActivity(i);
      }
    });

    Button newuser = (Button) findViewById(R.id.newUser);
    newuser.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(TELLERmain.this, NewUserTeller.class);
        i.putExtra("terminal", terminal);
        startActivity(i);
      }
    });

    Button viewOwnMessagesTeller = (Button) findViewById(R.id.viewownMessagesTeller);
    viewOwnMessagesTeller.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(TELLERmain.this, ViewAllTellerMessages.class);
        i.putExtra("terminal", terminal);
        i.putExtra("id", id);
        startActivity(i);
      }
    });


    Button exit = (Button) findViewById(R.id.Exit);
    exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(TELLERmain.this, MainActivity.class);
        startActivity(i);
      }
    });

  }
}



