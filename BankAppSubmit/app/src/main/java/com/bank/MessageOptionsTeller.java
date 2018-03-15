package com.bank;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bank.terminals.TellerTerminal;
import com.example.vidur.bankapp.R;

public class MessageOptionsTeller extends AppCompatActivity {

  TellerTerminal terminal = null;
  int id = -1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_message_options_teller);

    terminal = (TellerTerminal) getIntent().getSerializableExtra("terminal");
    id = getIntent().getIntExtra("customerId", id);

    Button viewcustomerMessagesTeller = (Button) findViewById(R.id.viewcustomerMessagesTeller);
    viewcustomerMessagesTeller.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(MessageOptionsTeller.this, ViewCustomerMessagesTeller.class);
        i.putExtra("terminal", terminal);
        i.putExtra("customerId", id);
        startActivity(i);
      }
    });

    Button leaveMessagesTeller = (Button) findViewById(R.id.leaveMessagesTeller);
    leaveMessagesTeller.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(MessageOptionsTeller.this, LeaveMessagesTeller.class);
        i.putExtra("terminal", terminal);
        i.putExtra("customerId", id);
        startActivity(i);
      }
    });

    Button Exit = (Button) findViewById(R.id.exit);
    Exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        Intent i = new Intent(MessageOptionsTeller.this, TellerOptions.class);
        i.putExtra("customerId", id);
        i.putExtra("terminal", terminal);
        startActivity(i);
      }
    });
  }
}
