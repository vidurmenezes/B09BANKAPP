package com.bank;

import static com.bank.bank.TellerCommunicator.makeDeposit;

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
import com.bank.bank.TellerCommunicator;
import com.bank.database.DatabaseSelectHelper;
import com.bank.exceptions.NoAccessToAccountException;
import com.bank.generics.AccountTypes;
import com.bank.generics.EnumMapAccountTypes;
import com.bank.terminals.ATM;
import com.bank.terminals.TellerTerminal;
import com.bank.user.Teller;
import com.example.vidur.bankapp.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MakeDeposit extends AppCompatActivity {
  Context context;
  String accountName;
  String amount;
  TellerTerminal teller = null;
  ArrayList<String> accountBalance = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_make_deposit);

    context = this.getApplication();

    teller = (TellerTerminal) getIntent().getSerializableExtra("terminal");


    accountBalance = getIntent().getStringArrayListExtra("balances");
    Button button = (Button) findViewById(R.id.userTransferSubmit);
    button.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        if (useridauthen1(context)) {
          Intent i = new Intent(MakeDeposit.this, TellerOptions.class);
          i.putExtra("terminal", teller);

          startActivity(i);
        }
      }
    });

    Button exit = (Button) findViewById(R.id.Exit);
    exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(MakeDeposit.this, TellerOptions.class);
        i.putExtra("teller", teller);
        startActivity(i);
      }
    });
  }

  private boolean useridauthen1(Context context) {
    EditText etName = (EditText) findViewById(R.id.nameOfAccount2);
    accountName = etName.getText().toString();
    EditText etAmount = (EditText) findViewById(R.id.accountTransferAmount);
    amount = etAmount.getText().toString();
    boolean useridauthen = true;
    boolean passwordauthen = true;

    if (TextUtils.isEmpty(accountName)) {
      etName.setError("No Account Name");
      useridauthen = false;
    }
    if (TextUtils.isEmpty(amount)) {
      etAmount.setError("No amount");
      passwordauthen = false;
    }
    boolean passed = false;
    if (passwordauthen && useridauthen) {
      passed = runDeposit(context);

    }
    return (passed);
  }

  private boolean runDeposit(Context context) {
    EditText etName = (EditText) findViewById(R.id.nameOfAccount2);
    accountName = etName.getText().toString();
    EditText etAmount = (EditText) findViewById(R.id.accountTransferAmount);
    amount = etAmount.getText().toString();
    boolean success = false;
    //EnumMapAccountTypes accountsMap = new EnumMapAccountTypes(context);
    try {
      BigDecimal amnt = new BigDecimal(amount);
      List<Account> listAcc = new ArrayList<>();
      listAcc = teller.listAccounts(context);
      // Get the id of the inputed account name and make deposit
      // of given amount
      for (int i = 0; i < listAcc.size(); i++) {
        if (accountName.equals(listAcc.get(i).getName())) {
          int accId = listAcc.get(i).getId();
          // Check if the account is a restricted saving
          success = teller.makeDeposit(amnt, accId, context);
          Toast.makeText(getApplicationContext(), "Deposit was made.", Toast.LENGTH_LONG).show();

        }
        break;
      }
    }
    catch (NoAccessToAccountException e) {
      etAmount.setError("You do not have access to this account");
    } catch (NumberFormatException e) {
      etAmount.setError("Please Enter a Number");
    }
    if (success) {
      Toast.makeText(getApplicationContext(), "Deposit was made.", Toast.LENGTH_LONG).show();
    } else {
      Toast.makeText(getApplicationContext(), "Deposit was unsuccessful.(POSSIBLE: Wrong " +
          "accountName)", Toast.LENGTH_LONG).show();
    }
    return success;
  }

}