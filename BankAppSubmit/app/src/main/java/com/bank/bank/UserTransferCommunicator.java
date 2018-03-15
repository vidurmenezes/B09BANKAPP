package com.bank.bank;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.bank.account.Account;
import com.bank.exceptions.NoAccessToAccountException;
import com.bank.security.PasswordHelpers;

public class UserTransferCommunicator {

  private static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

  public static void receiveTransfer(String hashedPassword, BigDecimal amount, boolean fromTeller,
                                     Context context) throws IOException, NoAccessToAccountException {
    boolean success = false, correctPass = false;
    String guess;
    while (!correctPass) {
      System.out.println("Please enter the password (or -1 to stop):");
      guess = input.readLine();
      if (PasswordHelpers.comparePassword(hashedPassword, guess) || guess.compareTo("-1") == 0) {
        correctPass = true;
      }
    }
    String accountName = "";
    int accId = 0;
    List<Account> listAcc = new ArrayList<>();
    // If from atm then use atmcommunicator to get accounts
    if (!fromTeller) {
      listAcc = AtmCommunicator.listAccounts(context);
    } else {
      listAcc = TellerCommunicator.listAccounts(context);
    }
    System.out.println("Please enter the account that you want to take in the amount");
    while (!success) {
      accountName = input.readLine();
      // Get the id of the inputed account name and make deposit
      // of given amount
      for (int i = 0; i < listAcc.size(); i++) {
        if (accountName.equals(listAcc.get(i).getName())) {
          accId = listAcc.get(i).getId();
          success = true;
          break;
        }
      }
      if (success) {
        if (!fromTeller) {
          success = AtmCommunicator.makeDeposit(amount, accId, context);
        } else {
          success = TellerCommunicator.makeDeposit(amount, accId, context);
        }
      }
    }
  }
}
