package com.bank.bank;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.bank.database.DatabaseSelectHelper;
import com.bank.exceptions.InsuffiecintFundsException;
import com.bank.exceptions.InvalidIdException;
import com.bank.exceptions.InvalidNameException;
import com.bank.exceptions.NoAccessToAccountException;
import com.bank.generics.AccountTypes;
import com.bank.generics.EnumMapAccountTypes;
import com.bank.generics.EnumMapRoles;
import com.bank.generics.Roles;
import com.bank.user.User;
import com.bank.account.Account;
import com.bank.database.DatabaseInsertHelper;
import com.bank.database.DatabaseUpdateHelper;
import com.bank.security.PasswordHelpers;
import com.bank.terminals.TellerTerminal;
import com.bank.user.Customer;

public class TellerCommunicator {

  private static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
  private static TellerTerminal terminal = null;

  /**
   * Allows teller to run teller interface if user input is validated
   */
  public static void runTeller(Context context) throws NumberFormatException, IOException, InvalidNameException {
    int tellerId = 0;
    boolean tellerAuthen = false;
    // Create a map of all roles in the database
    EnumMapRoles roleMap = new EnumMapRoles(context);
    // Receive id of the teller
    while ((tellerAuthen != true)) {
      System.out.println("Enter your ID");
      try {
        tellerId = Integer.parseInt(input.readLine());
      } catch (NumberFormatException e) {
        System.out.println("Please enter a number");
      }
      // Check if id valid
      User tellerUser = DatabaseSelectHelper.getUserDetails(tellerId, context);
      if ((tellerUser != null) && (tellerUser.getRoleId() == roleMap.get(Roles.TELLER))) {
        // Authenticate password
        System.out.println("Enter the Teller password");
        String password = input.readLine();
        terminal = new TellerTerminal(tellerId, password, context);
        boolean authenticated = terminal.authenticateTeller(tellerId, password, context);
        if (authenticated == true) {
          // Show teller the prompt
          System.out.println("Welcome Teller, Please enter what you would like to do.");
          runTellerInterface(tellerUser.getId(), context);
          tellerAuthen = true;
        }
      } else {
        System.out.println("Id does not exist or is not a Teller Id");
      }
    }
  }

  /**
   * Allows teller to choose and run options from the display list
   *
   * @param tellerId the id of the currently logged in teller
   */
  protected static void runTellerInterface(int tellerId, Context context) throws IOException,
      InvalidNameException {
    String userInput = "";
    // Create a map of all roles in the database
    EnumMapRoles roleMap = new EnumMapRoles(context);
    EnumMapAccountTypes accountsMap = new EnumMapAccountTypes(context);
    // Keep receiving input until (11)
    while (!(userInput.equals("14"))) {
      // Display list of options
      System.out.println("{1} Authenticate new customer");
      System.out.println("{2} Make new customer");
      System.out.println("{3} Make new account");
      System.out.println("{4} Give interest");
      System.out.println("{5} Make Deposit");
      System.out.println("{6} Make a withdrawal");
      System.out.println("{7} Check balance");
      System.out.println("{8} Close customer session");
      System.out.println("{9} View total balance of all accounts of given user");
      System.out.println("{10} Update customer information");
      System.out.println("{11} View own messages");
      System.out.println("{12} View customer's messages");
      System.out.println("{13} Leave a message for the customer");
      System.out.println("{14} Exit");
      // Get user input
      userInput = input.readLine();
      // If user input is 1, authenticate new user
      if (userInput.equals("1")) {
        // Get user info
        System.out.println("Enter UserId");
        int id = -1;
        try {
          id = Integer.parseInt(input.readLine());
        } catch (NumberFormatException e) {
          System.out.println("Please enter a number");
        }
        System.out.println("Please enter password to authenticate yourself.");
        // Get user password
        String password = input.readLine();
        // Authenticate current customer
        User tellerUser = DatabaseSelectHelper.getUserDetails(id, context);
        Customer customer = null;
        if ((tellerUser != null) && (tellerUser.getRoleId() == roleMap.get(Roles.CUSTOMER))) {
          customer = (Customer) DatabaseSelectHelper.getUserDetails(id, context);
          terminal.setCurrentCustomer(customer);
          terminal.authenticateCurrentCustomer(password, context);
          System.out.println("Thank you.");
        } else {
          System.out.println("The userId you entered does not exist or is not a Customer Id");
        }
        // If user input is 2, create new user
      } else if (userInput.equals("2")) {
        // Get user info
        System.out.println("Enter the Customer's name.");
        String name = input.readLine();
        System.out.println("Enter the Customer's age.");
        int age = -1;
        try {
          age = Integer.parseInt(input.readLine());
        } catch (NumberFormatException e) {
          System.out.println("Please enter a number");
        }
        System.out.println("Enter the Customer's address.");
        String address = input.readLine();
        System.out.println("Enter the Customer's password");
        String password = input.readLine();
        // Add user to the database
        terminal.makeNewUser(name, age, address, password, context);
        System.out.println("Please authenticate yourself.");
        // If user input is 3, create new account
      } else if (userInput.equals("3")) {
        // Get account info
        System.out.println("Enter the name of your new account");
        String name = input.readLine();
        int type = -1;
        try {
          System.out.println("Enter the initial balance you want to give it.");
          String amount = input.readLine();
          BigDecimal balance = new BigDecimal(amount);
          // Display account types list
          System.out.println("Enter the type number of the account.");
          System.out.println("{1} CHEQUING");
          System.out.println("{2} SAVING");
          System.out.println("{3} TFSA");
          System.out.println("{4} RESTRICTEDSAVING");
          System.out.println("{5} BALANCEOWING");
          type = Integer.parseInt(input.readLine());
          // Make sure only balance owing account can have a negative balance
          if (balance.compareTo(BigDecimal.ZERO) != -1 || type == 5) {
            int accountType = getAccountType(type, context);
            // Add an account associated with this user to the database
            terminal.makeNewAccount(name, balance, accountType, context);
            System.out.println("Thank you.");
          } else {
            System.out.println("Cannot have a negative balance for this type of account.");
            System.out.println("Account not made.");
          }
        } catch (NumberFormatException e) {
          System.out.println("Please enter a number");
        }
        // If user input is 4, add interest
      } else if (userInput.equals("4")) {
        int accId = 0;
        // Get account name to add interest to
        System.out.println("Which account would you like to give interest to?");
        String accInput = input.readLine();
        List<Account> listAcc = new ArrayList<>();
        try {
          listAcc = terminal.listAccounts(context);
        } catch (NoAccessToAccountException e) {
          e.printStackTrace();
        }
        // Get the id for the inputed account name
        for (int i = 0; i < listAcc.size(); i++) {
          if (accInput.equals(listAcc.get(i).getName())) {
            accId = listAcc.get(i).getId();
          }
        }
        // Give interest
        terminal.giveInterest(accId, context);
        System.out.println("Thank you.");
        // If user input is 5, make deposit
      } else if (userInput.equals("5")) {
        boolean success = false;
        System.out.println("Which account would you like to make deposit to?");
        String accInput = input.readLine();
        try {
          System.out.println("How much would you like to deposit?");
          String amount = input.readLine();
          BigDecimal amnt = new BigDecimal(amount);
          List<Account> listAcc = new ArrayList<>();
          listAcc = terminal.listAccounts(context);
          // Get the id of the inputed account name and make deposit
          // of given amount
          for (int i = 0; i < listAcc.size(); i++) {
            if (accInput.equals(listAcc.get(i).getName())) {
              int accId = listAcc.get(i).getId();
              // Check if the account is a restricted saving
              if (DatabaseSelectHelper.getAccountDetails(accId, context).getType() == accountsMap
                  .get(AccountTypes.RESTRICTEDSAVING)) {
                System.out.println("Cannot make a deposit to a RestrictedSavingsAccount from a"
                    + " teller terminal.");
              } else {
                success = makeDeposit(amnt, accId, context);
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
        // If user input is 6, make a withdrawal
      } else if (userInput.equals("6")) {
        WithdrawalCommunicator.makeWithdrawal(true, context);
        // If user input is 7, check balance
      } else if (userInput.equals("7")) {
        BigDecimal balance = null;
        // Get name of account to check balance for
        System.out.println("Which account would you like to check balance for?");
        String accInput = input.readLine();
        List<Account> listAcc = new ArrayList<>();
        try {
          listAcc = terminal.listAccounts(context);
          // Get the id of the inputed account name and check its
          // balance
          for (int i = 0; i < listAcc.size(); i++) {
            if (accInput.equals(listAcc.get(i).getName())) {
              int accId = listAcc.get(i).getId();
              balance = terminal.checkBalance(accId, context);
            }
          }
        } catch (NoAccessToAccountException e) {
          e.printStackTrace();
        }
        // Print balance in account
        System.out.println("Your current balance for this account is " + balance.toPlainString());
        // If user input is 8, close customer session
      } else if (userInput.equals("8")) {
        terminal.deAuthenticateCustomer();
        System.out.println("Customer session closed");
      } else if (userInput.equals("9")) {
        AdminCommunicator.getTotalUserBalance(context);
      } else if (userInput.equals("10")) {
        boolean done = false;
        int choice = -1;
        System.out.println("What would you like to update? (enter 0 to exit)");
        System.out.println("{1} Name");
        System.out.println("{2} Address");
        System.out.println("{3} Password");
        while (!done) {
          try {
            choice = Integer.parseInt(input.readLine());
            done = true;
          } catch (NumberFormatException e) {
            System.out.println("Please enter a number");
          }
          if (choice == 0) {
            done = true;
          }
        }
        if (choice != 0) {
          boolean result = false;
          String change = "";
          if (choice == 1) {
            System.out.println("Enter a new name:");
            change = input.readLine();
            result = terminal.updateName(change, context);
          } else if (choice == 2) {
            result = terminal.updateAddress(change, context);
            System.out.println("Enter a new address:");
            change = input.readLine();
          } else if (choice == 3) {
            System.out.println("Enter a new password:");
            change = input.readLine();
            result = terminal.updatePassword(PasswordHelpers.passwordHash(change), context);
          }
          if (!result) {
            System.out.println("Customer is not authenticated. Information not updated");
          } else {
            System.out.println("Customer information successfully updated");
          }
        } else {
          System.out.println("Information not updated");
        }
      } else if (userInput.equals("11")) {

        String output = "";
        // get a list of all of this user's message ids
        List<Integer> messageIds = DatabaseSelectHelper.getAllMessageIds(tellerId, context);
        output += "This is a list of messages for your own account: " + "\n";
        for (Integer iD : messageIds) {
          output += DatabaseSelectHelper.getSpecificMessage(iD, context) + "\n";
          DatabaseUpdateHelper.updateUserMessageState(iD, context);
        }
        output += "Each message's status has been changed";
        System.out.println(output);

      } else if (userInput.equals("12")) {
        Customer customer = null;
        try {
          customer = terminal.getCurrentCustomer();
        } catch (NullPointerException e) {
          System.out.println("Customer not logger in");
        }
        String output = "";
        // get a list of all of this user's message ids
        List<Integer> messageIds = DatabaseSelectHelper.getAllMessageIds(customer.getId(), context);
        output += "This is a list of messages for the logged in customer's account: " + "\n";
        for (Integer iD : messageIds) {
          output += DatabaseSelectHelper.getSpecificMessage(iD, context) + "\n";
          DatabaseUpdateHelper.updateUserMessageState(iD, context);
        }
        output += "Each message's status has been changed";
        System.out.println(output);

      } else if (userInput.equals("13")) {
        int id = -1;
        System.out
            .println("Enter the UserId of the customer who you would like to leave a message for.");
        // get the id
        try {
          id = Integer.parseInt(input.readLine());
        } catch (NumberFormatException e) {
          System.out.println("Please enter a number");
        }
        User customerUser = DatabaseSelectHelper.getUserDetails(id, context);
        if (customerUser.getRoleId() == roleMap.get(Roles.CUSTOMER)) {
          System.out.println("Please enter the message.");
          String message = input.readLine();
          int messageId = DatabaseInsertHelper.insertMessage(id, message, context);
          System.out.println("Message inserted, the message Id is: " + messageId);
        } else {
          System.out.println("This id is not that of a customer");
        }
      } else {
        // If user input is 11, close interface
        if (userInput.equals("14")) {
          System.out.println("Interface Closed");
        } else {
          System.out.println("Please enter a valid number");
        }
      }
    }
  }

  /**
   * @param type is an account type input that is wanted
   * @return the real account type from the database
   */
  private static int getAccountType(int type, Context context) {
    int accountType = 0;
    EnumMapAccountTypes accountsMap = new EnumMapAccountTypes(context);
    if (type == 1) {
      accountType = accountsMap.get(AccountTypes.CHEQUING);
    } else if (type == 2) {
      accountType = accountsMap.get(AccountTypes.SAVING);
    } else if (type == 3) {
      accountType = accountsMap.get(AccountTypes.TFSA);
    } else if (type == 4) {
      accountType = accountsMap.get(AccountTypes.RESTRICTEDSAVING);
    } else if (type == 5) {
      accountType = accountsMap.get(AccountTypes.BALANCEOWING);
    }
    return accountType;
  }

  /**
   * @return list of accounts for the customer
   */
  public static List<Account> listAccounts(Context context) throws NoAccessToAccountException {
    return terminal.listAccounts(context);
  }

  /**
   * @return If withdrawal was successful
   */
  public static boolean makeWithdrawal(BigDecimal amount, int accountId, Context context)
      throws InsuffiecintFundsException, NoAccessToAccountException {
    return terminal.makeWithdrawal(amount, accountId, context);
  }

  /**
   * @return If deposit was successful
   */
  public static boolean makeDeposit(BigDecimal amount, int accountId, Context context)
      throws NoAccessToAccountException {
    return terminal.makeDeposit(amount, accountId, context);
  }
}
