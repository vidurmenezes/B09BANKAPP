package com.bank;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bank.account.Account;
import com.bank.database.DatabaseSelectHelper;
import com.bank.database.DatabaseUpdateHelper;
import com.bank.exceptions.NoAccessToAccountException;
import com.bank.security.PasswordHelpers;
import com.bank.terminals.ATM;
import com.bank.terminals.TellerTerminal;
import com.example.vidur.bankapp.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ReceiveTransfer extends AppCompatActivity {

  Context context;
  String password;
  ATM atm = null;
  TellerTerminal terminal = null;
  String accountName;
  boolean fromTeller;
  String messageId;
  int id;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_receive_transfer);
    context = this.getApplicationContext();
    terminal = (TellerTerminal) getIntent().getSerializableExtra("terminal");
    atm = (ATM) getIntent().getSerializableExtra("atm");
    fromTeller = getIntent().getBooleanExtra("fromTeller", fromTeller);
    id = getIntent().getIntExtra("customerId", id);

    Button button = (Button) findViewById(R.id.receiveTransferSubmit);
    button.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        if (useridauthen1(context)) {
          Intent i;
          if (fromTeller) {
            i = new Intent(ReceiveTransfer.this, TellerOptions.class);
          } else {
            i = new Intent(ReceiveTransfer.this, Customerlogin.class);
          }
          i.putExtra("terminal", terminal);
          i.putExtra("atm", atm);
          i.putExtra("customerId", id);
          startActivity(i);
        }
      }
    });

    Button Exit = (Button) findViewById(R.id.Exit);
    Exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        Intent i;
        if (fromTeller) {
          i = new Intent(ReceiveTransfer.this, TellerOptions.class);
        } else {
          i = new Intent(ReceiveTransfer.this, Customerlogin.class);
        }
        i.putExtra("terminal", terminal);
        i.putExtra("atm", atm);
        i.putExtra("customerId", id);
        startActivity(i);
      }
    });
  }

  private boolean useridauthen1(Context context) {
    EditText etName = (EditText) findViewById(R.id.accountName);
    accountName = etName.getText().toString();
    EditText etPassword = (EditText) findViewById(R.id.receiveTransferPassword);
    password = etPassword.getText().toString();
    EditText etMessageId = (EditText) findViewById(R.id.messageid);
    messageId = etMessageId.getText().toString();
    boolean useridauthen = true;
    boolean passwordauthen = true;
    if (TextUtils.isEmpty(accountName)) {
      etName.setError("No Account Name");
      useridauthen = false;
    }
    if (TextUtils.isEmpty(messageId)) {
      etMessageId.setError("No Message Id");
      useridauthen = false;
    }
    if (TextUtils.isEmpty(password)) {
      etPassword.setError("No Password");
      passwordauthen = false;
    }
    boolean passed = false;
    if (passwordauthen && useridauthen) {
      passed = runReceiveTransfer(fromTeller, messageId, context);
    }
    return (passed);
  }

  private boolean runReceiveTransfer(boolean fromTeller, String messageId, Context context) {
    boolean validAccount = false, success = false;
    int accId = 0;
    EditText etName = (EditText) findViewById(R.id.accountName);
    accountName = etName.getText().toString();
    EditText etPassword = (EditText) findViewById(R.id.receiveTransferPassword);
    password = etPassword.getText().toString();
    try {
      List<Account> listAcc = new ArrayList<>();
      // If withdrawal from atm then use atmcommunicator to get accounts
      if (!fromTeller) {
        listAcc = atm.listAccounts(context);
      } else {
        listAcc = terminal.listAccounts(context);
      }
      // Get the id of the inputed account name and withdraw money
      for (int i = 0; i < listAcc.size(); i++) {
        if (accountName.equals(listAcc.get(i).getName())) {
          accId = listAcc.get(i).getId();
          validAccount = true;
        }
        if (validAccount) {
          break;
        }
      }
    } catch (NoAccessToAccountException e) {
      Toast.makeText(getApplicationContext(), "No access to account", Toast.LENGTH_LONG).show();
    }
    if (!validAccount) {
      etName.setError("Invalid or Wrong Account Name");
    } else {
      String currMessage = DatabaseSelectHelper.getSpecificMessage(Integer.valueOf(messageId),
          context);
      List<Integer> statuses = DatabaseSelectHelper.getAllMessageStatuses(id, context);
      if (statuses.get(Integer.valueOf(messageId) - 1) == 1) {
        Toast.makeText(getApplicationContext(), "Amount already received from this transaction.",
            Toast.LENGTH_LONG).show();
      } else {
        currMessage = currMessage.substring(4);
        String givenPass = currMessage.split(":")[0];
        BigDecimal amount = new BigDecimal(currMessage.split(":")[1]);
        if (PasswordHelpers.comparePassword(givenPass, password)) {
          try {
            if (!fromTeller) {
              success = atm.makeDeposit(amount, accId, context);
            } else {
              success = terminal.makeDeposit(amount, accId, context);
            }
          } catch (NoAccessToAccountException e) {
            e.printStackTrace();
          }
        } else {
          etPassword.setError("Wrong Password");
        }
      }
      if (success) {
        Toast.makeText(getApplicationContext(), "Transfer Successful", Toast.LENGTH_LONG).show();
        DatabaseUpdateHelper.updateUserMessageState(Integer.valueOf(messageId), context);
      } else {
        Toast.makeText(getApplicationContext(), "Transfer Unsuccessful", Toast.LENGTH_LONG).show();
      }
    }
    return success;
  }
}
