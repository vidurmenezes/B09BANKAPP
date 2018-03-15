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
import com.bank.database.DatabaseInsertHelper;
import com.bank.database.DatabaseSelectHelper;
import com.bank.database.DatabaseUpdateHelper;
import com.bank.exceptions.InsuffiecintFundsException;
import com.bank.exceptions.NoAccessToAccountException;
import com.bank.generics.AccountTypes;
import com.bank.generics.EnumMapAccountTypes;
import com.bank.terminals.ATM;
import com.bank.terminals.TellerTerminal;
import com.bank.user.User;
import com.example.vidur.bankapp.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountTransfer extends AppCompatActivity {

  Context context;
  String accountName1;
  String accountName2;
  String amount;
  ATM atm = null;
  TellerTerminal terminal = null;
  int id;
  boolean fromTeller;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_account_transfer);
    context = this.getApplicationContext();
    terminal = (TellerTerminal) getIntent().getSerializableExtra("terminal");
    atm = (ATM) getIntent().getSerializableExtra("atm");
    fromTeller = getIntent().getBooleanExtra("fromTeller", fromTeller);
    id = getIntent().getIntExtra("customerId", id);

    Button button = (Button) findViewById(R.id.accountTransferSubmit);
    button.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        if (useridauthen1(context)) {
          Intent i;
          if (fromTeller) {
            i = new Intent(AccountTransfer.this, TellerOptions.class);
          } else {
            i = new Intent(AccountTransfer.this, Customerlogin.class);
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
          i = new Intent(AccountTransfer.this, TellerOptions.class);
        } else {
          i = new Intent(AccountTransfer.this, Customerlogin.class);
        }
        i.putExtra("terminal", terminal);
        i.putExtra("atm", atm);
        i.putExtra("customerId", id);
        startActivity(i);
      }
    });
  }

  private boolean useridauthen1(Context context) {
    EditText etName1 = (EditText) findViewById(R.id.accountName1);
    accountName1 = etName1.getText().toString();
    EditText etName2 = (EditText) findViewById(R.id.accountName2);
    accountName2 = etName2.getText().toString();
    EditText etAmount = (EditText) findViewById(R.id.userTransferAmount);
    amount = etAmount.getText().toString();
    boolean useridauthen = true;
    boolean passwordauthen = true;
    if (TextUtils.isEmpty(accountName1)) {
      etName1.setError("No Account Name");
      useridauthen = false;
    }
    if (TextUtils.isEmpty(accountName2)) {
      etName2.setError("No Account Name");
      useridauthen = false;
    }
    if (TextUtils.isEmpty(amount)) {
      etAmount.setError("No amount");
      passwordauthen = false;
    }
    boolean passed = false;
    if (passwordauthen && useridauthen) {
      passed = runAccountTransfer(fromTeller, context);
    }
    return (passed);
  }

  private BigDecimal runWithdraw(boolean fromTeller, int accId1, Context context) {
    boolean success = false;

    EditText etAmount = (EditText) findViewById(R.id.userTransferAmount);
    amount = etAmount.getText().toString();

    EnumMapAccountTypes accountsMap = new EnumMapAccountTypes(context);
    try {
      if (!fromTeller) {
        success = atm.makeWithdrawal(new BigDecimal(amount), accId1, context);
      } else {
        success = terminal.makeWithdrawal(new BigDecimal(amount), accId1, context);
      }
    } catch (NoAccessToAccountException e) {
      Toast.makeText(getApplicationContext(), "No access to account", Toast.LENGTH_LONG).show();
    } catch (InsuffiecintFundsException e) {
      etAmount.setError("Not enough funds");
    }
    if (success) {
      Toast.makeText(getApplicationContext(), "Withdrawal was successful.", Toast.LENGTH_LONG).show();
      // If the account was a savings accounts
      if (DatabaseSelectHelper.getAccountType(accId1, context) == accountsMap.get(AccountTypes.SAVING)) {
        Account account = DatabaseSelectHelper.getAccountDetails(accId1, context);
        // If new balance is below $1000
        if (account.getBalance().compareTo(new BigDecimal("1000")) == -1) {
          DatabaseUpdateHelper.updateAccountType(accountsMap.get(AccountTypes.CHEQUING), accId1,
              context);
          Toast.makeText(getApplicationContext(), "Since this savings account dropped below " +
              "$1000, it is now a chequing account", Toast.LENGTH_LONG).show();
          // get the userId from account id
          int ctr = 1;
          int userId = -1;
          boolean loop = true;
          try {
            while (loop) {
              // get each user in the database
              User user = DatabaseSelectHelper.getUserDetails(ctr, context);
              // get a list of accounts for each user and add it to a list of lists
              List<Integer> accIdList = DatabaseSelectHelper.getAccountIds(user.getId(), context);
              for (Integer id : accIdList) {
                if (accId1 == id) {
                  userId = ctr;
                }
              }
              ctr++;
            }
          } catch (Exception e) {
          }
          String message = "Your account of the id: " + accId1 + ", was changed from a"
              + " saving to chequing account";
          int messageId = DatabaseInsertHelper.insertMessage(userId, message, context);
          Toast.makeText(getApplicationContext(), "Message inserted, the message Id is: "
              + String.valueOf(messageId), Toast.LENGTH_LONG).show();
        }
      }
    } else {
      Toast.makeText(getApplicationContext(), "Withdrawal was unsuccessful.(POSSIBLE: Wrong " +
          "accountName or invalid amount chosen)", Toast.LENGTH_LONG).show();
      amount = "0";
    }
    return new BigDecimal(amount);
  }

  private boolean runAccountTransfer(boolean fromTeller, Context context) {
    boolean validAccount1 = false, validAccount2 = false, success = false;
    int accId1 = 0, accId2 = 0;
    EditText etName1 = (EditText) findViewById(R.id.accountName1);
    accountName1 = etName1.getText().toString();
    EditText etName2 = (EditText) findViewById(R.id.accountName2);
    accountName2 = etName2.getText().toString();
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
        if (accountName1.equals(listAcc.get(i).getName())) {
          accId1 = listAcc.get(i).getId();
          validAccount1 = true;
        }
        if (accountName2.equals(listAcc.get(i).getName())) {
          accId2 = listAcc.get(i).getId();
          validAccount2 = true;
        }
        if (validAccount1 && validAccount2) {
          break;
        }
      }
    } catch (NoAccessToAccountException e) {
      Toast.makeText(getApplicationContext(), "No access to account", Toast.LENGTH_LONG).show();
    }
    if (!validAccount1) {
      etName1.setError("Invalid or Wrong Account Name");
    }
    if (!validAccount2) {
      etName2.setError("Invalid or Wrong Account Name");
    }
    if (validAccount1 && validAccount2) {
      BigDecimal amount = runWithdraw(fromTeller, accId1, context);
      try {
        if (!amount.equals(BigDecimal.ZERO)) {
          if (!fromTeller) {
            success = atm.makeDeposit(amount, accId2, context);
          } else {
            success = terminal.makeDeposit(amount, accId2, context);
          }
        }
      } catch (NoAccessToAccountException e) {
        e.printStackTrace();
      }
      if (success) {
        Toast.makeText(getApplicationContext(), "Transfer Successful", Toast.LENGTH_LONG).show();
      } else {
        Toast.makeText(getApplicationContext(), "Transfer Unsuccessful", Toast.LENGTH_LONG).show();
      }
    }
    return success;
  }

}
