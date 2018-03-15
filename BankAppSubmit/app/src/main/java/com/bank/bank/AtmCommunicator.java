package com.bank.bank;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.bank.database.DatabaseSelectHelper;
import com.bank.database.DatabaseUpdateHelper;
import com.bank.exceptions.InsuffiecintFundsException;
import com.bank.exceptions.NoAccessToAccountException;
import com.bank.terminals.ATM;
import com.bank.user.User;
import com.bank.account.Account;
import com.bank.generics.EnumMapRoles;
import com.bank.generics.Roles;

public class AtmCommunicator {

  private static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
  private static ATM atm = null;

  /**
   * Checks if id is valid and password is authenticated, and runs ATM interface
   */
  public static void runAtm(Context context) throws NumberFormatException, IOException {
    // Run the ATM interface
    int userId = 0;
    boolean userAuthen = false;
    // Create a map of all roles in the database
    EnumMapRoles roleMap = new EnumMapRoles(context);
    while ((userAuthen != true)) {
      System.out.println("Enter your ID");
      try {
        userId = Integer.parseInt(input.readLine());
      } catch (NumberFormatException e) {
        System.out.println("Please enter a number");
      }
      // Check if id is valid
      User customerUser = DatabaseSelectHelper.getUserDetails(userId, context);
      if ((customerUser != null) && (customerUser.getRoleId() == roleMap.get(Roles.CUSTOMER))) {
        // Authenticate password
        System.out.println("Enter your password");
        String password = input.readLine();
        atm = new ATM(userId, context);
        boolean authenticated = atm.authenticate(userId, password, context);
        if (authenticated == true) {
          // Give customer prompts
          System.out.println("Welcome " + customerUser.getName() + ". Please enter what you would "
              + "like to do");
          runAtmInterface(context);
          userAuthen = true;
        }
      } else {
        System.out.println("Id does not exist or is not a Customer Id");
      }
    }
  }

  /**
   * Allows customer to choose and run options from the display list
   */
  protected static void runAtmInterface(Context context) throws IOException {
    String userInput = "";
    String output = "";
    // Keep running until (6)
    while (!(userInput.equals("6"))) {
      // Display list of options
      System.out.println("{1} List accounts and balances");
      System.out.println("{2} Make deposit");
      System.out.println("{3} Check balance");
      System.out.println("{4} Make withdrawal");
      System.out.println("{5} View all messages");
      System.out.println("{6} Exit");
      // Read user input
      userInput = input.readLine();
      // If user input is 1, list all of the customer's accounts and their
      // balance
      if (userInput.equals("1")) {
        List<Account> listAcc = new ArrayList<>();
        try {
          listAcc = atm.listAccounts(context);
          for (int i = 0; i < listAcc.size(); i++) {
            output += "Account Name: " + listAcc.get(i).getName();
            output += " , Balance: " + listAcc.get(i).getBalance().toPlainString();
            output += "\n";
          }
        } catch (NoAccessToAccountException e) {
          e.printStackTrace();
        }
        System.out.println(output);
        // Reset output
        output = "";
        // If user input is 2, get name of account and its id and make
        // deposit
      } else if (userInput.equals("2")) {
        boolean success = false;
        System.out.println("Which account would you like to make deposit to?");
        // Get account name to deposit into
        String accInput = input.readLine();
        try {
          System.out.println("How much would you like to deposit?");
          // Get amount to deposit
          String amount = input.readLine();
          BigDecimal amnt = new BigDecimal(amount);
          List<Account> listAcc = new ArrayList<>();
          listAcc = atm.listAccounts(context);
          for (int i = 0; i < listAcc.size(); i++) {
            if (accInput.equals(listAcc.get(i).getName())) {
              int accId = listAcc.get(i).getId();
              success = makeDeposit(amnt, accId, context);
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
        // If user input is 3, get name of account and its id and check
        // balance
      } else if (userInput.equals("3")) {
        BigDecimal balance = null;
        System.out.println("Which account would you like to check balance for?");
        // Get account name to check balance for
        String accInput = input.readLine();
        List<Account> listAcc = new ArrayList<>();
        try {
          listAcc = atm.listAccounts(context);
          for (int i = 0; i < listAcc.size(); i++) {
            if (accInput.equals(listAcc.get(i).getName())) {
              int accId = listAcc.get(i).getId();
              balance = atm.checkBalance(accId, context);
            }
          }
        } catch (NoAccessToAccountException e) {
          e.printStackTrace();
        }
        System.out.println("Your current balance for this account is " + balance.toPlainString());
        // If user input is 4, get name of account and its id and
        // withdraw money
      } else if (userInput.equals("4")) {
        WithdrawalCommunicator.makeWithdrawal(false, context);
        // If user input is 5, close interface
      } else if (userInput.equals("5")) {
        String output2 = "";
        // get a list of all of this user's message ids
        List<Integer> messageIds =
            DatabaseSelectHelper.getAllMessageIds(atm.getCurrentCustomer().getId(), context);
        output2 += "This is a list of messages for your own account: " + "\n";
        for (Integer iD : messageIds) {
          output2 += DatabaseSelectHelper.getSpecificMessage(iD, context) + "\n";
          DatabaseUpdateHelper.updateUserMessageState(iD, context);
        }
        output2 += "Each message's status has been changed";
        System.out.println(output2);
      } else if (!userInput.equals("6")) {
        System.out.println("Please enter a valid number");
      }
    }
  }

  /**
   * @return list of accounts for the customer
   */
  public static List<Account> listAccounts(Context context) throws NoAccessToAccountException {
    return atm.listAccounts(context);
  }

  /**
   * @return If withdrawal was successful
   */
  public static boolean makeWithdrawal(BigDecimal amount, int accountId, Context context)
      throws InsuffiecintFundsException, NoAccessToAccountException {
    return atm.makeWithdrawal(amount, accountId, context);
  }

  /**
   * @return If deposit was successful
   */
  public static boolean makeDeposit(BigDecimal amount, int accountId, Context context)
      throws NoAccessToAccountException {
    return atm.makeDeposit(amount, accountId, context);
  }
}

