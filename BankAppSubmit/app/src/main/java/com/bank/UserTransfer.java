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
import com.bank.security.PasswordHelpers;
import com.bank.terminals.ATM;
import com.bank.terminals.TellerTerminal;
import com.bank.user.Customer;
import com.bank.user.User;
import com.example.vidur.bankapp.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UserTransfer extends AppCompatActivity {

  Context context;
  String accountName;
  String amount;
  String userId;
  String password;
  ATM atm = null;
  TellerTerminal terminal = null;
  boolean fromTeller;
  int id;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_transfer);
    context = this.getApplicationContext();
    terminal = (TellerTerminal) getIntent().getSerializableExtra("terminal");
    atm = (ATM) getIntent().getSerializableExtra("atm");
    fromTeller = getIntent().getBooleanExtra("fromTeller", fromTeller);
    id = getIntent().getIntExtra("customerId", id);

    Button button = (Button) findViewById(R.id.userTransferSubmit);
    button.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        if (useridauthen1(context)) {
          Intent i;
          if (fromTeller) {
            i = new Intent(UserTransfer.this, TellerOptions.class);
          } else {
            i = new Intent(UserTransfer.this, Customerlogin.class);
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
          i = new Intent(UserTransfer.this, TellerOptions.class);
        } else {
          i = new Intent(UserTransfer.this, Customerlogin.class);
        }
        i.putExtra("terminal", terminal);
        i.putExtra("atm", atm);
        i.putExtra("customerId", id);
        startActivity(i);
      }
    });
  }

  private boolean useridauthen1(Context context) {
    EditText etName = (EditText) findViewById(R.id.editText);
    accountName = etName.getText().toString();
    EditText etPassword = (EditText) findViewById(R.id.userTransferPassword);
    password = etPassword.getText().toString();
    EditText etId = (EditText) findViewById(R.id.userid);
    userId = etId.getText().toString();
    EditText etAmount = (EditText) findViewById(R.id.UserTransferAmount);
    amount = etAmount.getText().toString();
    boolean useridauthen = true;
    boolean passwordauthen = true;
    if (TextUtils.isEmpty(accountName)) {
      etName.setError("No Account Name");
      useridauthen = false;
    }
    if (TextUtils.isEmpty(userId)) {
      etId.setError("No User Id");
      useridauthen = false;
    }
    if (TextUtils.isEmpty(password)) {
      etPassword.setError("No Password");
      useridauthen = false;
    }
    if (TextUtils.isEmpty(amount)) {
      etAmount.setError("No amount");
      passwordauthen = false;
    }
    boolean passed = false;
    if (passwordauthen && useridauthen) {
      passed = runSendAmount(fromTeller, amount, context);
    }
    return (passed);
  }

  private boolean runSendAmount(boolean fromTeller, String stringAmount, Context context) {
    boolean success = false, validAccount = false, validUser = true;
    int accId = 0;
    EditText etName = (EditText) findViewById(R.id.editText);
    accountName = etName.getText().toString();
    EditText etId = (EditText) findViewById(R.id.userid);
    userId = etId.getText().toString();
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
      try {
        User user = (Customer) DatabaseSelectHelper.getUserDetails(Integer.parseInt(userId),
            context);
      } catch (Exception e) {
        validUser = false;
      }
      if (!validUser) {
        etId.setError("Invalid Id or is not a Customer");
      }
    }
    if (validAccount && validUser) {
      BigDecimal amount = runWithdraw(fromTeller, stringAmount, accId, context);
      if (!amount.equals(BigDecimal.ZERO)) {
        String message = "@#$%" + PasswordHelpers.passwordHash(password) + ":" + amount.toString();
        int messageId = DatabaseInsertHelper.insertMessage(Integer.valueOf(userId), message,
            context);
        Toast.makeText(getApplicationContext(), "Transfer was made. Message Id for receiver: " +
            String.valueOf(messageId), Toast.LENGTH_LONG).show();
        success = true;
      } else {
        Toast.makeText(getApplicationContext(), "Error: Nothing was sent. Make sure the amount " +
            "is valid.", Toast.LENGTH_LONG).show();
      }
    }
    return success;
  }

  private BigDecimal runWithdraw(boolean fromTeller, String stringAmount, int accId,
                                 Context context) {
    boolean success = false;

    EnumMapAccountTypes accountsMap = new EnumMapAccountTypes(context);
    try {
      if (!fromTeller) {
        success = atm.makeWithdrawal(new BigDecimal(amount), accId, context);
      } else {
        success = terminal.makeWithdrawal(new BigDecimal(amount), accId, context);
      }
    } catch (NoAccessToAccountException e) {
      Toast.makeText(getApplicationContext(), "No access to account", Toast.LENGTH_LONG).show();
    } catch (InsuffiecintFundsException e) {
      Toast.makeText(getApplicationContext(), "Not Enough Funds", Toast.LENGTH_LONG).show();
    }
    if (success) {
      Toast.makeText(getApplicationContext(), "Withdrawal was successful.", Toast.LENGTH_LONG).show();
      // If the account was a savings accounts
      if (DatabaseSelectHelper.getAccountType(accId, context) == accountsMap.get(AccountTypes.SAVING)) {
        Account account = DatabaseSelectHelper.getAccountDetails(accId, context);
        // If new balance is below $1000
        if (account.getBalance().compareTo(new BigDecimal("1000")) == -1) {
          DatabaseUpdateHelper.updateAccountType(accountsMap.get(AccountTypes.CHEQUING), accId,
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
                if (accId == id) {
                  userId = ctr;
                }
              }
              ctr++;
            }
          } catch (Exception e) {
          }
          String message = "Your account of the id: " + accId + ", was changed from a"
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

}
