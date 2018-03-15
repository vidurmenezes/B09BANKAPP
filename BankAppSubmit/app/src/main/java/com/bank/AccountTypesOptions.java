package com.bank;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bank.database.DatabaseInsertHelper;
import com.bank.database.DatabaseSelectHelper;
import com.bank.exceptions.InvalidNameException;
import com.bank.generics.AccountTypes;
import com.bank.generics.EnumMapAccountTypes;
import com.bank.generics.EnumMapRoles;
import com.bank.generics.Roles;
import com.bank.terminals.TellerTerminal;
import com.example.vidur.bankapp.R;

import java.math.BigDecimal;

public class AccountTypesOptions extends AppCompatActivity {

  Context context;
  String name;
  String amount;
    TellerTerminal terminal = null;
  EnumMapAccountTypes typeMap ;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_account_types_options);
    context = this.getApplication();
    typeMap = new EnumMapAccountTypes(context);
    name = getIntent().getStringExtra("accountName");
    amount = getIntent().getStringExtra("amount");
      terminal = (TellerTerminal) getIntent().getSerializableExtra("terminal");



    Button Exit = (Button) findViewById(R.id.Exit);
    Exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(AccountTypesOptions.this, TellerOptions.class);
          i.putExtra("terminal", terminal);
        startActivity(i);
      }
    });

    Button cheq = (Button) findViewById(R.id.chequing);
    cheq.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        makeAccount(context, typeMap.get(AccountTypes.CHEQUING));
        Intent i = new Intent(AccountTypesOptions.this, TellerOptions.class);
          i.putExtra("terminal", terminal);
        startActivity(i);
      }
    });

    Button saving = (Button) findViewById(R.id.savings);
    saving.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        makeAccount(context, typeMap.get(AccountTypes.SAVING));
        Intent i = new Intent(AccountTypesOptions.this, TellerOptions.class);
          i.putExtra("terminal", terminal);
        startActivity(i);
      }
    });

    Button tfsa = (Button) findViewById(R.id.tfsa);
    tfsa.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        makeAccount(context, typeMap.get(AccountTypes.TFSA));
        Intent i = new Intent(AccountTypesOptions.this, TellerOptions.class);
          i.putExtra("terminal", terminal);
        startActivity(i);
      }
    });

    Button restricted = (Button) findViewById(R.id.restrictedSavings);
    restricted.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        makeAccount(context, typeMap.get(AccountTypes.RESTRICTEDSAVING));
        Intent i = new Intent(AccountTypesOptions.this, TellerOptions.class);
          i.putExtra("terminal", terminal);
        startActivity(i);
      }
    });

    Button owing = (Button) findViewById(R.id.balanceOwing);
    owing.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        makeAccount(context, typeMap.get(AccountTypes.BALANCEOWING));
        Intent i = new Intent(AccountTypesOptions.this, TellerOptions.class);
          i.putExtra("terminal", terminal);
        startActivity(i);
      }
    });
  }

  private void makeAccount(Context context, int accType) {
    BigDecimal balance = new BigDecimal(amount);
    // Make sure only balance owing account can have a negative balance
    if (balance.compareTo(BigDecimal.ZERO) != -1 || accType == typeMap.get(AccountTypes.BALANCEOWING)) {
      // Add an account associated with this user to the database
        terminal.makeNewAccount(name, balance, accType, context);
        Toast.makeText(context, "Account made", Toast.LENGTH_LONG).show();
    } else {
        Toast.makeText(context, "Cannot have a negative balance for this type of account.", Toast.LENGTH_LONG).show();
        Toast.makeText(context, "Account not made", Toast.LENGTH_LONG).show();
    }
  }

}
