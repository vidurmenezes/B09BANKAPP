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

public class AccountTransferCommunicator {

  private static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

  public static void accountTransfer(boolean fromTeller, Context context) throws IOException {
    BigDecimal amount = WithdrawalCommunicator.makeWithdrawal(false, context);
    if (amount.compareTo(BigDecimal.ZERO) != 0) {
      boolean success = false;
      System.out.println("Which account would you like to transfer to?");
      String accInput = input.readLine();
      try {
        List<Account> listAcc = new ArrayList<>();
        // If withdrawal from atm then use atmcommunicator to get accounts
        if (!fromTeller) {
          listAcc = AtmCommunicator.listAccounts(context);
        } else {
          listAcc = TellerCommunicator.listAccounts(context);
        }
        // Get the id of the inputed account name and make deposit
        // of given amount
        for (int i = 0; i < listAcc.size(); i++) {
          if (accInput.equals(listAcc.get(i).getName())) {
            int accId = listAcc.get(i).getId();
            if (!fromTeller) {
              success = AtmCommunicator.makeDeposit(amount, accId, context);
            } else {
              success = TellerCommunicator.makeDeposit(amount, accId, context);
            }
            break;
          }
        }
      } catch (NoAccessToAccountException e) {
        e.printStackTrace();
      } catch (NumberFormatException e) {
        System.out.println("Please enter a number");
      }
      if (success) {
        System.out.println("Deposit was made.");
      } else {
        System.out.println("Deposit was unsuccessful");
      }
    }
  }
}
