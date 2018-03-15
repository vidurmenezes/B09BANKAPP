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
import com.bank.bank.AtmCommunicator;
import com.bank.bank.TellerCommunicator;
import com.bank.database.DatabaseInsertHelper;
import com.bank.database.DatabaseSelectHelper;
import com.bank.database.DatabaseUpdateHelper;
import com.bank.exceptions.InsuffiecintFundsException;
import com.bank.exceptions.NoAccessToAccountException;
import com.bank.generics.AccountTypes;
import com.bank.generics.EnumMapAccountTypes;
import com.bank.terminals.ATM;
import com.bank.user.User;
import com.example.vidur.bankapp.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MakeWithdrawlCustomer extends AppCompatActivity {
  Context context;
  String accountName;
  String amount;
  ATM atm = null;
  ArrayList<String> accountBalance = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_make_withdrawl_customer);

    context = this.getApplication();
    atm = (ATM) getIntent().getSerializableExtra("atm");
    accountBalance = getIntent().getStringArrayListExtra("balances");

    Button button = (Button) findViewById(R.id.withdrawlsubmit);
    button.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        if (useridauthen1(context)) {
          Intent i = new Intent(MakeWithdrawlCustomer.this, Customerlogin.class);
          i.putExtra("atm", atm);
          i.putStringArrayListExtra("balances", accountBalance);
          startActivity(i);
        }
      }
    });

    Button exit = (Button) findViewById(R.id.Exit);
    exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(MakeWithdrawlCustomer.this, Customerlogin.class);
        i.putExtra("atm", atm);
        i.putStringArrayListExtra("balances", accountBalance);
        startActivity(i);
      }
    });
  }

  private boolean useridauthen1(Context context) {
    EditText etName = (EditText) findViewById(R.id.nameOfAccount2);
    accountName = etName.getText().toString();
    EditText etAmount = (EditText) findViewById(R.id.amount);
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
      passed = runWithdraw(false, context);
    }
    return (passed);
  }

  private boolean runWithdraw(boolean fromTeller, Context context) {
    boolean success = false;

    EditText etName = (EditText) findViewById(R.id.nameOfAccount2);
    accountName = etName.getText().toString();
    EditText etAmount = (EditText) findViewById(R.id.amount);
    amount = etAmount.getText().toString();

    EnumMapAccountTypes accountsMap = new EnumMapAccountTypes(context);
    int accId = 0;
    try {
      List<Account> listAcc = new ArrayList<>();
      // If withdrawal from atm then use atmcommunicator to get accounts
      if (!fromTeller) {
        listAcc = atm.listAccounts(context);
      } else {
        listAcc = TellerCommunicator.listAccounts(context);
      }
      // Get the id of the inputed account name and withdraw money
      for (int i = 0; i < listAcc.size(); i++) {
        if (accountName.equals(listAcc.get(i).getName())) {
          accId = listAcc.get(i).getId();
          if (fromTeller && (DatabaseSelectHelper.getAccountDetails(accId, context).getType()
                  == accountsMap.get(AccountTypes.RESTRICTEDSAVING))) {
            System.out.println("Cannot make a withdrawal to a RestrictedSavingsAccount from a"
                    + " teller terminal.");
          } else {
            if (!fromTeller) {
              success = atm.makeWithdrawal(new BigDecimal(amount), accId, context);
            } else {
              success = TellerCommunicator.makeWithdrawal(new BigDecimal(amount), accId, context);
            }
          }
          break;
        }
      }
    } catch (NoAccessToAccountException e) {
    } catch (InsuffiecintFundsException e) {
      etAmount.setError("Not enough funds");
    } catch (NumberFormatException e) {
      etAmount.setError("Please Enter a Number");
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
          Toast.makeText(getApplicationContext(),"Since this savings account dropped below " +
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
          Toast.makeText(getApplicationContext(),"Message inserted, the message Id is: "
                  + String.valueOf(messageId), Toast.LENGTH_LONG).show();
        }
      }
    } else {
      Toast.makeText(getApplicationContext(),"Withdrawal was unsuccessful.(POSSIBLE: Wrong " +
              "accountName)", Toast.LENGTH_LONG).show();
    }
    return success;
  }
}
