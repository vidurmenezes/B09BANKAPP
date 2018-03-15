package com.bank;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bank.terminals.TellerTerminal;
import com.example.vidur.bankapp.R;

public class NewAccountTeller extends AppCompatActivity {
    TellerTerminal terminal = null;
  Context context;
  String name;
  String amount;

  @Override

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_account_teller);
      terminal = (TellerTerminal) getIntent().getSerializableExtra("terminal");
    Button newAccountSubmit = (Button) findViewById(R.id.newAccountSubmit);
    newAccountSubmit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        if (useridauthen1(context)) {
          Intent i = new Intent(NewAccountTeller.this, AccountTypesOptions.class);
          i.putExtra("accountName",name);
          i.putExtra("amount",amount);
            i.putExtra("terminal", terminal);
          startActivity(i);
        }
      }
    });

    Button Exit = (Button) findViewById(R.id.exit);
    Exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(NewAccountTeller.this, TELLERmain.class);
          i.putExtra("terminal", terminal);
        startActivity(i);
      }
    });
  }

  public boolean useridauthen1(Context context) {
    boolean userNameAuthen = true;
    boolean amountAuthen = true;

    EditText etUserName = (EditText) findViewById(R.id.Name);
    name = etUserName.getText().toString();
    EditText etAge = (EditText) findViewById(R.id.age);
    amount = etAge.getText().toString();

    if (TextUtils.isEmpty(name)) {
      etUserName.setError("No Account Name");
      userNameAuthen = false;
    }
    if (TextUtils.isEmpty(amount)) {
      etAge.setError("No amount");
      amountAuthen = false;
    }
    boolean passed = false;
    if (userNameAuthen && amountAuthen) {
      passed = true;
    }
    return (passed);
  }

}
